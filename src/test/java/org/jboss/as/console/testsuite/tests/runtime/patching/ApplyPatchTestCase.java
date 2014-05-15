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
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.qa.management.cli.DomainCliClient;
import org.jboss.qa.management.common.DomainManager;
import org.jboss.qa.management.common.Library;
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
import java.util.concurrent.TimeUnit;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(SharedTest.class)
public class ApplyPatchTestCase extends PatchTestCaseAbstract {

    private static final Logger log = LoggerFactory.getLogger(ApplyPatchTestCase.class);

    @Before
    public void before() {
        if (!serverManager.isInRunningState()) {
            serverManager.waitUntilAvailable();
        }

        cliClient.restart(false);

        if (patchCliManager.isPatchInstalled(CUMULATIVE_PATCH_NAME)) {
            removePatchViaCliUsingRollback(CUMULATIVE_PATCH_NAME);
        }
        if (patchCliManager.isPatchInstalled(BASIC_PATCH_NAME)) {
            removePatchViaCliUsingRollback(BASIC_PATCH_NAME);
        }

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
        if (patchCliManager.isPatchInstalled(BASIC_PATCH_NAME)) {
            removePatchViaCliUsingRollback(BASIC_PATCH_NAME);
        }

        browser.navigate().refresh();
    }

    @Test
    public void applyValidPatchTest() {
        applyPatch(basicPatchFile, true, true);
        verifyInstalledPatch(BASIC_PATCH_NAME, true);
        applyPatch(cumulativePatchFile, true, true);
        verifyInstalledPatch(BASIC_PATCH_NAME, true);
        verifyInstalledPatch(CUMULATIVE_PATCH_NAME, true);
    }

    @Test
    public void applyInvalidPatchTest() {
        // cumulativePatchFile requires basic patch to be installed, which is not in this state
        applyPatch(cumulativePatchFile, false, true);
        verifyInstalledPatch(CUMULATIVE_PATCH_NAME, false);
    }

    @Test
    public void applyOneOffPatchTest() {
        final String oneOffPatchName = "dummy-oneoff-patch";
        File oneOffPatchSimple = initPatchFile("console-test-oneoff-patch.zip.template", oneOffPatchName);
        try {
            applyPatch(oneOffPatchSimple, true, true);
            verifyInstalledPatch(oneOffPatchName, true);
            Assert.assertEquals("Displayed patch type doesn't match", "one-off",
                    patchManagementPage.getResourceTable().getRowByText(0, oneOffPatchName).getCellValue(2));
        } finally {
            if (patchCliManager.isPatchInstalled(oneOffPatchName)) {
                removePatchViaCliUsingRollback(oneOffPatchName);
            }
        }
    }

    @Test
    public void applyBundlePatchTest() {
        File bundlePatch = createBundlePatch(new File[]{basicPatchFile, cumulativePatchFile});
        applyPatch(bundlePatch, true, true);
        verifyInstalledPatch(CUMULATIVE_PATCH_NAME, true);
        verifyInstalledPatch(BASIC_PATCH_NAME, true);
    }


    private void applyPatch(File patchFile, boolean expectedResult, boolean restartServer) {
        PatchingWizard applyPatchWizard = patchManagementPage.applyNewPatch();

        if(ConfigUtils.isDomain()) {
            DomainManager dm = new DomainManager(cliClient);
            if (!dm.listRunningServers().isEmpty()) {
                applyPatchWizard.shutdownServer(true);
                applyPatchWizard.next();
                applyPatchWizard.waitUntilFinished();
            }
        }

        applyPatchWizard.getEditor().uploadFile(patchFile, PropUtils.get("runtime.patching.apply.fileupload.name"));
        applyPatchWizard.next();
        Assert.assertEquals("Result of applying patch, " + applyPatchWizard.getResultMessage(), expectedResult,
                applyPatchWizard.isSuccess());
        if (expectedResult) {
            applyPatchWizard.restartSever(restartServer);
            applyPatchWizard.finish();
        } else {
            applyPatchWizard.cancel();
        }
        assertRestartDoneByConsole(restartServer);
    }

    private void verifyInstalledPatch(String patchName, boolean expectedResult) {
        log.debug("Trying to verify installed patch");
        serverManager.waitUntilAvailable();
        log.debug("Server should be available according to CLI.");

        Assert.assertEquals(patchName +" was successfully applied:", expectedResult, patchCliManager.isPatchInstalled(patchName));

        log.debug("Reloading Admin Console.");
        browser.navigate().to(ConfigUtils.getUrl());
        Graphene.goTo(PatchManagementPage.class);
        Console.withBrowser(browser).waitUntilLoaded();

        Assert.assertEquals(patchName + " is visible in console:", expectedResult,
                patchManagementPage.getResourceTable().getRowByText(0, patchName) != null);
    }


}
