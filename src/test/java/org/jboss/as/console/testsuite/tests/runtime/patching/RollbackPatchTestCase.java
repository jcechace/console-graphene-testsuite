package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.patching.PatchingWizard;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.PatchManager;
import org.jboss.qa.management.common.ServerUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(StandaloneTest.class)
public class RollbackPatchTestCase extends PatchTestCaseAbstract{

    private static final Logger log = LoggerFactory.getLogger(RollbackPatchTestCase.class);

    @Before
    public void before() {
        if (!ServerUtils.isServerRunning(cliClient)) {
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
        }
        cliClient.restart(false);
        prepareAndApplyPatchViaCli(cliClient, basicPatchFile, BASIC_PATCH_NAME);

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
        removePatchViaCliUsingRollback(cliClient, BASIC_PATCH_NAME);

        browser.navigate().refresh();
    }

    @Test
    public void rollbackCumulativePatchTest() {
        prepareAndApplyPatchViaCli(cliClient, cumulativePatchFile, CUMULATIVE_PATCH_NAME);
        rollbackPatch(CUMULATIVE_PATCH_NAME, true, true);
        Assert.assertFalse("Patch rollback failed",
                patchCliManager.getCumulativePatchId().equals(CUMULATIVE_PATCH_NAME));
        log.debug("Patch history cum: {}", patchCliManager.getHistory().contains(new PatchManager.PatchHistoryEntry(CUMULATIVE_PATCH_NAME, "cumulative", null)));
        Assert.assertTrue("Patch rollback failed, only cumulative patch should be rolled backed",
                patchCliManager.getCumulativePatchId().equals(BASIC_PATCH_NAME));
    }

    @Test
    public void rollbackOriginalAfterCumulativePatchFailureTest() {
        prepareAndApplyPatchViaCli(cliClient, cumulativePatchFile, CUMULATIVE_PATCH_NAME);
        rollbackPatch(BASIC_PATCH_NAME, false, true);
        Assert.assertTrue("Patch rollback should fail => cumulative patch should still be there",
                patchCliManager.getPatchInfo().getCumulativePatchId().equals(CUMULATIVE_PATCH_NAME));
        log.debug("Patch history: {}", patchCliManager.getHistory().contains(new PatchManager.PatchHistoryEntry(BASIC_PATCH_NAME, "cumulative", null)));
        Assert.assertTrue("Original patch shouldn't be rollbacked",
                patchCliManager.getCumulativePatchId().equals(CUMULATIVE_PATCH_NAME));
    }

    @Test
    public void rollbackPatchTest() {
        rollbackPatch(BASIC_PATCH_NAME, true, true);
        Assert.assertFalse("Patch rollback failed",
                patchCliManager.getCumulativePatchId().equals(BASIC_PATCH_NAME));
        Assert.assertFalse("Restart is required by the server, should be already done by console",
                cliClient.restartRequired());
    }

    private void rollbackPatch(String patchName, boolean expectedResult, boolean withServerRestart) {
        PatchingWizard rollbackPatchWizard = patchManagementPage.rollbackPatch(patchName);
        rollbackPatchWizard.next();
        rollbackPatchWizard.next();
        Assert.assertEquals("Result of applying patch, " + rollbackPatchWizard.getResultMessage(), expectedResult, rollbackPatchWizard.isSuccess());
        if (expectedResult) {
            rollbackPatchWizard.restartSever(withServerRestart);
            rollbackPatchWizard.finish();
        } else {
            rollbackPatchWizard.cancel();
        }
        assertRestartDoneByConsole(withServerRestart);
        Assert.assertEquals("Patch rollback result", expectedResult,
                !patchCliManager.getHistory().contains(new PatchManager.PatchHistoryEntry(patchName, null, null)));
    }

}
