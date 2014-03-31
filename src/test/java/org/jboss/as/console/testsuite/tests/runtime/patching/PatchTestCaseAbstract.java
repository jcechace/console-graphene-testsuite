package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.PatchManager;
import org.jboss.qa.management.common.Library;
import org.jboss.qa.management.common.ServerUtils;
import org.jboss.qa.management.common.ZipUtils;
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
@Category(StandaloneTest.class)
public abstract class PatchTestCaseAbstract {

    public static final String BASIC_PATCH_ZIP_TEMPLATE_NAME = "console-test-patch-basic.zip.template";
    public static final String CUMULATIVE_PATCH_ZIP_TEMPLATE_NAME = "console-test-patch-cumulated.zip.template";

    public static final String BASIC_PATCH_NAME = "basic-console-test-patch"; // simple cumulative patch
    public static final String CUMULATIVE_PATCH_NAME = "cumulative-console-test-patch"; // patch dependent on BASIC_PATCH_NAME
    protected File basicPatchFile = initPatchFile(PatchTestCaseAbstract.BASIC_PATCH_ZIP_TEMPLATE_NAME, BASIC_PATCH_NAME);
    protected File cumulativePatchFile = initPatchFile(PatchTestCaseAbstract.CUMULATIVE_PATCH_ZIP_TEMPLATE_NAME, CUMULATIVE_PATCH_NAME);

    @Drone
    protected WebDriver browser;

    @Page
    protected PatchManagementPage patchManagementPage;

    private static final Logger log = LoggerFactory.getLogger(PatchTestCaseAbstract.class);

    protected static CliClient cliClient = CliProvider.getClient();

    protected PatchManager patchCliManager = new PatchManager(cliClient, true);

    public File initPatchFile(String patchFileTemplateName, String patchId) {
        String pathToPatchTemplate = getClass().getResource("/patching/"+patchFileTemplateName).getPath();
        String serverVersion = ServerUtils.getVersion(cliClient);
        return createPatchZipFromTemplate(new File(pathToPatchTemplate), serverVersion, serverVersion, patchId);
    }


    public void prepareAndApplyPatchViaCli(CliClient cliClient, File patchFile, String patchName) {
        patchCliManager.apply(patchFile.getAbsolutePath());
        log.debug("patch {} applied", patchName);
        Assert.assertTrue("Patch wasn't successfully applied via CLI",
                patchCliManager.isPatchInstaled(patchName));
        browser.navigate().refresh();
    }

    public void removePatchViaCliUsingRollback(CliClient cliClient, String patchName) {
        patchCliManager.rollback(patchName, false);
    }


    public static File createPatchZipFromTemplate(File srcPatchZip, String origPatchServerVersion,
                                                  String newPatchServerVersion, String patchName) {
        try {
            File newPatchZip = File.createTempFile("newPatch", ".zip");
            newPatchZip.delete();
            newPatchZip.deleteOnExit();
            FileUtils.copyFile(srcPatchZip, newPatchZip);
            File patchXml = File.createTempFile("patch", ".xml");
            patchXml.delete();
            if (!ZipUtils.extractOneFileFromZip(newPatchZip, "patch.xml", patchXml)) {
                throw new RuntimeException("patch.xml not found in " + newPatchZip.getAbsolutePath());
            }
            Map<String, String> substitutions = new HashMap<String, String>();
            substitutions.put("%EAP_VERSION_ORIG%", origPatchServerVersion);
            substitutions.put("%EAP_VERSION_NEW%", newPatchServerVersion);
            substitutions.put("%PATCH_NAME%", patchName);
            Library.replaceStringsInFile(substitutions, patchXml);
            ZipUtils.updateOneFileInZip(newPatchZip, patchXml, "patch.xml");
            patchXml.delete();
            return newPatchZip;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            return null;
        }
    }


    public void assertRestartDoneByConsole(boolean expectServerAsRestarted) {
        if (expectServerAsRestarted) {
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
            Assert.assertFalse("Restart is required by the server, should be already done by console",
                    cliClient.restartRequired());
        } else {
            Assert.assertTrue("Restart shouldn't be done by console => should be still required by server",
                    cliClient.restartRequired());
        }
    }
}
