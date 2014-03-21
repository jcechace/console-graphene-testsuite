package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
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

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
@Category(StandaloneTest.class)
public abstract class PatchTestCaseAbstract {

    public static final String BASIC_PATCH_ZIP_TEMPLATE_NAME = "console-test-patch-basic.zip.template";
    public static final String CUMULATIVE_PATCH_ZIP_TEMPLATE_NAME = "console-test-patch-cumulated.zip.template";

    public static final String BASIC_PATCH_NAME = "basic-console-test-patch";
    protected File basicPatchFile = initPatchFile(PatchTestCaseAbstract.BASIC_PATCH_ZIP_TEMPLATE_NAME, BASIC_PATCH_NAME);

    @Drone
    protected WebDriver browser;

    @Page
    protected PatchManagementPage patchManagementPage;

    private static final Logger log = LoggerFactory.getLogger(PatchTestCaseAbstract.class);

    protected static CliClient cliClient = new CliClient();

    protected PatchManager patchCliManager = new PatchManager(cliClient, true);

    public File initPatchFile(String patchFileTemplateName, String patchId) {
        String pathToPatchTemplate = getClass().getResource("/patching/"+patchFileTemplateName).getPath();
        return createPatchZipFromTemplate(new File(pathToPatchTemplate), ServerUtils.getVersion(cliClient), patchId);
    }


    public void prepareAndApplyPatchViaCli(CliClient cliClient, File patchFile, String patchName) {
        patchCliManager.apply(patchFile.getAbsolutePath());
        log.debug("patch {} applied", patchName);
        Assert.assertTrue("Patch wasn't successfully applied via CLI",
                patchCliManager.getPatchInfo().getCumulativePatchId().equals(patchName));
        browser.navigate().refresh();
    }

    public void removePatchViaCliUsingRollback(CliClient cliClient, String patchName) {
        patchCliManager.rollback(patchName, false);
    }



    public static File createPatchZipFromTemplate(File srcPatchZip, String patchServerVersion, String patchName) {
        try {
            File newPatchZip = File.createTempFile("newPatch", ".zip");
            newPatchZip.delete();
            FileUtils.copyFile(srcPatchZip, newPatchZip);
            File patchXml = File.createTempFile("patch", ".xml");
            patchXml.delete();
            if (!ZipUtils.extractOneFileFromZip(newPatchZip, "patch.xml", patchXml)) {
                throw new RuntimeException("patch.xml not found in " + newPatchZip.getAbsolutePath());
            }
            Library.replaceStringInFile("%EAP_VERSION%", patchServerVersion, patchXml);
            Library.replaceStringInFile("%PATCH_NAME%", patchName, patchXml);
            ZipUtils.updateOneFileInZip(newPatchZip, patchXml, "patch.xml");
            return newPatchZip;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            return null;
        }
    }
}
