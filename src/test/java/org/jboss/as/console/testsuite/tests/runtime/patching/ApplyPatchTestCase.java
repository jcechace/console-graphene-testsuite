package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.PatchManager;
import org.jboss.qa.management.common.Library;
import org.jboss.qa.management.common.ServerUtils;
import org.jboss.qa.management.common.ZipUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
public class ApplyPatchTestCase {
    @Drone
    private WebDriver browser;

    @Page
    private PatchManagementPage patchManagementPage;

    private static CliClient cliClient = new CliClient();

    private File testPatchFile;
    private String patchName;


    private PatchManager patchCliManager = new PatchManager(cliClient, true);

    public ApplyPatchTestCase() {
        String pathToPatchTemplate = getClass().getResource("/patching/console-test-patch-basic.zip.template").getPath();
        testPatchFile = createZipWithUpdatedVersionInPatchXml(new File(pathToPatchTemplate));
        patchName = "console-test-patch";
    }

    @Before
    public void setup() {
        Graphene.goTo(PatchManagementPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @After
    public void after() {
        browser.navigate().refresh();
    }

    @Test
    public void rollbackPatch() {
        try {
            prepareAndApplyPatchViaCli(cliClient, testPatchFile, patchName);
            WizardWindow rollbackPatchWizard = patchManagementPage.rollbackPatch(patchName);
            rollbackPatchWizard.next();
            rollbackPatchWizard.next();
            rollbackPatchWizard.finish();
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
            Assert.assertFalse("Patch wasn't successfully rollbacked",
                    patchCliManager.getPatchInfo().getCumulativePatchId().equals(patchName));
            Assert.assertFalse("Restart is required by the server, should be already done by console",
                    cliClient.restartRequired());
        } finally {
            removePatchViaCliUsingRollback(cliClient, patchName);
        }
    }

    @Test
    public void applyValidPatch() {
        try {
            removePatchViaCliUsingRollback(cliClient, patchName);
            applyPatch(testPatchFile);
            Assert.assertTrue("Patch wasn't successfully applied, probably due https://bugzilla.redhat.com/show_bug.cgi?id=1074623",
                    patchCliManager.getPatchInfo().getCumulativePatchId().equals(patchName));
        } finally {
            patchCliManager.rollback(patchName, false);
        }
    }

    public void prepareAndApplyPatchViaCli(CliClient cliClient, File patch, String patchName) {
        cliClient.restart(false);
        if (!patchCliManager.isPatchInstaled(patchName)) {
            patchCliManager.apply(testPatchFile.getAbsolutePath());
        }
        Assert.assertTrue("Patch wasn't successfully applied via CLI",
                patchCliManager.getPatchInfo().getCumulativePatchId().equals(patchName));
        browser.navigate().refresh();
    }

    public void removePatchViaCliUsingRollback(CliClient cliClient, String patchName) {
        cliClient.restart(false);
        patchCliManager.rollback(patchName, false);
    }


    public void applyPatch(File patchFile) {
        WizardWindow applyPatchWizard = patchManagementPage.applyNewPatch();
        applyPatchWizard.getEditor().uploadFile(patchFile, PropUtils.get("runtime.patching.apply.fileupload.name"));
        applyPatchWizard.next(); // only confirmation
        applyPatchWizard.next();
    }

    public static File createZipWithUpdatedVersionInPatchXml(File srcPatchZip) {
        try {
            File newPatchZip = File.createTempFile("newPatch", ".zip");
            newPatchZip.delete();
            FileUtils.copyFile(srcPatchZip, newPatchZip);
            File patchXml = File.createTempFile("patch", ".xml");
            patchXml.delete();
            if (!ZipUtils.extractOneFileFromZip(newPatchZip, "patch.xml", patchXml)) {
                throw new RuntimeException("patch.xml not found in " + newPatchZip.getAbsolutePath());
            }
            String serverVersion = ServerUtils.getVersion(cliClient);
            Library.replaceStringInFile("version=\"%EAP_VERSION%\"", "version=\""+serverVersion+"\"", patchXml);
            ZipUtils.updateOneFileInZip(newPatchZip, patchXml, "patch.xml");
            return newPatchZip;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            return null;
        }
    }
}
