package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.common.ServerUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
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
        removePatchViaCliUsingRollback(cliClient, BASIC_PATCH_NAME);

        browser.navigate().refresh();
    }

    @Test
    public void rollbackPatch() {
            WizardWindow rollbackPatchWizard = patchManagementPage.rollbackPatch(BASIC_PATCH_NAME);
            log.debug("Started rollback wizard");
            rollbackPatchWizard.next();
            rollbackPatchWizard.next();
            log.debug("Finishing rollback wizard");
            rollbackPatchWizard.finish();
            log.debug("Wizard finished");
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
            log.debug("Server should be started");
            Assert.assertFalse("Patch rollback failed",
                    patchCliManager.getPatchInfo().getCumulativePatchId().equals(BASIC_PATCH_NAME));
            Assert.assertFalse("Restart is required by the server, should be already done by console",
                    cliClient.restartRequired());
    }

}
