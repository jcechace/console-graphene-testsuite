package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.patching.PatchingWizard;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.qa.management.common.ServerUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(StandaloneTest.class)
public class ApplyPatchTestCase extends PatchTestCaseAbstract {

    private static final Logger log = LoggerFactory.getLogger(ApplyPatchTestCase.class);

    @Before
    public void before() {
        if (!ServerUtils.isServerRunning(cliClient)) {
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
        }
        cliClient.restart(false);
        if (patchCliManager.isPatchInstaled(CUMULATIVE_PATCH_NAME)) {
            removePatchViaCliUsingRollback(cliClient, CUMULATIVE_PATCH_NAME);
        }
        if (patchCliManager.isPatchInstaled(BASIC_PATCH_NAME)) {
            removePatchViaCliUsingRollback(cliClient, BASIC_PATCH_NAME);
        }

        Graphene.goTo(PatchManagementPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @After
    public void after() {
        if (!ServerUtils.isServerRunning(cliClient)) {
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
        }
        if (patchCliManager.isPatchInstaled(CUMULATIVE_PATCH_NAME)) {
            removePatchViaCliUsingRollback(cliClient, CUMULATIVE_PATCH_NAME);
        }
        if (patchCliManager.isPatchInstaled(BASIC_PATCH_NAME)) {
            removePatchViaCliUsingRollback(cliClient, BASIC_PATCH_NAME);
        }

        browser.navigate().refresh();
    }

    @Test
    public void applyValidPatchTest() {
        applyPatch(basicPatchFile, BASIC_PATCH_NAME, true);
        applyPatch(cumulativePatchFile, CUMULATIVE_PATCH_NAME, true);
    }

    @Test
    public void applyInvalidPatchTest() {
        applyPatch(cumulativePatchFile, CUMULATIVE_PATCH_NAME, false); // requires basic patch to be installed, which is not in this state
    }

    @Test
    public void applyOneOffPatchTest() {
        final String oneOffPatchName = "dummyOneOffPatch";
        File oneOffPatchSimple = initPatchFile("console-test-oneoff-patch.zip.template", oneOffPatchName);
        try {
            applyPatch(oneOffPatchSimple, oneOffPatchName, true);
            Assert.assertEquals("Displayed patch type doesn't match", "one-off",
                    patchManagementPage.getResourceTable().getRowByText(0, oneOffPatchName).getCellValue(2));
        } finally {
            if (patchCliManager.isPatchInstaled(oneOffPatchName)) {
                removePatchViaCliUsingRollback(cliClient, oneOffPatchName);
            }
        }
    }


    private void applyPatch(File patchFile, String patchName, boolean expectedResult) {
        PatchingWizard applyPatchWizard = patchManagementPage.applyNewPatch();
        applyPatchWizard.getEditor().uploadFile(patchFile, PropUtils.get("runtime.patching.apply.fileupload.name"));
        applyPatchWizard.next();
        Assert.assertEquals("Result of applying patch, " + applyPatchWizard.getResultMessage(), expectedResult,
                applyPatchWizard.isSuccess());
        if (expectedResult) {
            applyPatchWizard.finish();
        } else {
            applyPatchWizard.cancel();
        }
        Assert.assertEquals(patchName +" was successfully applied:", expectedResult, patchCliManager.isPatchInstaled(patchName));
        Assert.assertFalse("Restart is required by the server, should be already done by console",
                cliClient.restartRequired());

        Assert.assertEquals(patchName + " is visible in console:", expectedResult, patchManagementPage.getResourceTable().getRowByText(0, patchName) != null);
    }


}
