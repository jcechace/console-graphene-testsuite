package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.patching.PatchingWizard;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.DomainCliClient;
import org.jboss.qa.management.common.DomainManager;
import org.jboss.qa.management.common.ServerUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
@Category(SharedTest.class)
public class RollbackPatchTestCase extends PatchTestCaseAbstract{

    private static final Logger log = LoggerFactory.getLogger(RollbackPatchTestCase.class);

    @Before
    public void before() {
        if (!serverManager.isInRunningState()) {
            serverManager.waitUntilAvailable();
        }

        cliClient.restart(false);

        prepareAndApplyPatchViaCli(basicPatchFile, BASIC_PATCH_NAME);

        Graphene.goTo(PatchManagementPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        if (ConfigUtils.isDomain()) {
            patchManagementPage.pickHost(((DomainCliClient) cliClient).getDomainHost());
        }
    }

    @After
    public void after() {
        if (!serverManager.isInRunningState()) {
            serverManager.waitUntilAvailable();
        }

        if (patchCliManager.isPatchInstalled(CUMULATIVE_PATCH_NAME)) {
            removePatchViaCliUsingRollback(CUMULATIVE_PATCH_NAME);
        }
        removePatchViaCliUsingRollback(BASIC_PATCH_NAME);

        browser.navigate().refresh();
    }

    @Test
    @Ignore("Ignored, this is just RFE: https://bugzilla.redhat.com/show_bug.cgi?id=1085833")
    public void rollbackOlderOneoffPatchTest() {
        final String oneOffPatch1 = "dummy-oneoff-patch1";
        final String oneOffPatch2 = "dummy-oneoff-patch2";
        File oneOffPatchSimple1 = initPatchFile("console-test-oneoff-patch.zip.template", oneOffPatch1);
        File oneOffPatchSimple2 = initPatchFile("console-test-oneoff-patch.zip.template", oneOffPatch2);
        try {
            prepareAndApplyPatchViaCli(oneOffPatchSimple1, oneOffPatch1);
            prepareAndApplyPatchViaCli(oneOffPatchSimple2, oneOffPatch2);
            rollbackPatch(oneOffPatch1, true, true);
        } finally {
            if (patchCliManager.isPatchInstalled(oneOffPatch2)) {
                removePatchViaCliUsingRollback(oneOffPatch2);
            }
            if (patchCliManager.isPatchInstalled(oneOffPatch1)) {
                removePatchViaCliUsingRollback(oneOffPatch1);
            }
        }
    }

    @Test
    public void rollbackCumulativePatchTest() {
        prepareAndApplyPatchViaCli(cumulativePatchFile, CUMULATIVE_PATCH_NAME);
        rollbackPatch(CUMULATIVE_PATCH_NAME, true, true);
        Assert.assertFalse("Patch rollback failed",
                patchCliManager.isPatchInstalled(CUMULATIVE_PATCH_NAME));
        Assert.assertTrue("Patch rollback failed, only cumulative patch should be rolled backed",
                patchCliManager.isPatchInstalled(BASIC_PATCH_NAME));
    }

    @Test
    public void rollbackOriginalAfterCumulativePatchFailureTest() {
        prepareAndApplyPatchViaCli(cumulativePatchFile, CUMULATIVE_PATCH_NAME);
        rollbackPatch(BASIC_PATCH_NAME, false, true);
        Assert.assertTrue("Patch rollback should fail => cumulative patch should still be there",
                patchCliManager.isPatchInstalled(CUMULATIVE_PATCH_NAME));
        Assert.assertTrue("Original patch shouldn't be rollbacked",
                patchCliManager.isPatchInstalled(CUMULATIVE_PATCH_NAME));
    }

    @Test
    public void rollbackPatchTest() {
        rollbackPatch(BASIC_PATCH_NAME, true, true);
        Assert.assertFalse("Patch rollback failed",
                patchCliManager.isPatchInstalled(BASIC_PATCH_NAME));
        Assert.assertFalse("Restart is required by the server, should be already done by console",
                cliClient.restartRequired());
    }

    private void rollbackPatch(String patchName, boolean expectedResult, boolean withServerRestart) {
        PatchingWizard rollbackPatchWizard = patchManagementPage.rollbackPatch(patchName);
        try {
            if(ConfigUtils.isDomain()) {
                DomainManager dm = new DomainManager(cliClient);
                if (!dm.listRunningServers().isEmpty()) {
                    rollbackPatchWizard.shutdownServer(true);
                    rollbackPatchWizard.next();
                    rollbackPatchWizard.waitUntilFinished();
                }
            }
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
                    !patchCliManager.isPatchInstalled(patchName));
        } finally {
            if (!rollbackPatchWizard.isClosed()) {
                rollbackPatchWizard.close();
            }
        }
    }

}
