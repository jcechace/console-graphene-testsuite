package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.patching.PatchingWizard;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.qa.management.common.ServerUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
public class ApplyPatchTestCase extends PatchTestCaseAbstract {

    private static final Logger log = LoggerFactory.getLogger(ApplyPatchTestCase.class);

    @Before
    public void before() {
        if (!ServerUtils.isServerRunning(cliClient)) {
            ServerUtils.waitForServerToBecomeAvailable(cliClient);
        }
        cliClient.restart(false);
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
        if (patchCliManager.isPatchInstaled(BASIC_PATCH_NAME)) {
            removePatchViaCliUsingRollback(cliClient, BASIC_PATCH_NAME);
        }

        browser.navigate().refresh();
    }


    @Test
    public void applyValidPatch() {
        applyPatch(basicPatchFile, true);
        Assert.assertTrue("Patch wasn't successfully applied, probably due https://bugzilla.redhat.com/show_bug.cgi?id=1074623",
                patchCliManager.getPatchInfo().getCumulativePatchId().equals(BASIC_PATCH_NAME));
        browser.navigate().refresh();
    }


    public void applyPatch(File patchFile, boolean expectedResult) {
        PatchingWizard applyPatchWizard = patchManagementPage.applyNewPatch();
        applyPatchWizard.getEditor().uploadFile(patchFile, PropUtils.get("runtime.patching.apply.fileupload.name"));
        applyPatchWizard.next();
        Assert.assertEquals("Result of applying patch, " + applyPatchWizard.getResultMessage(), expectedResult, applyPatchWizard.isSuccess());

        applyPatchWizard.finish();
    }


}
