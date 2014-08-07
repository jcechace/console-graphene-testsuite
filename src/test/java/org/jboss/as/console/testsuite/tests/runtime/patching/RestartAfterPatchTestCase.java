package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.DomainCliClient;
import org.jboss.qa.management.cli.PatchManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mvelas on 22.4.2014.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(StandaloneTest.class)
public class RestartAfterPatchTestCase extends PatchTestCaseAbstract {

    private static final Logger log = LoggerFactory.getLogger(ApplyPatchTestCase.class);

    @Before
    public void before() {
        if (!serverManager.isInRunningState()) {
            serverManager.waitUntilAvailable();
        }

        Graphene.goTo(PatchManagementPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        if (ConfigUtils.isDomain()) {
            patchManagementPage.pickHost(((DomainCliClient) cliClient).getDomainHost());
        }

        patchCliManager = new PatchManager(cliClient, false);
    }

    @After
    public void after() {
        if (patchCliManager.isPatchInstalled(BASIC_PATCH_NAME)) {
            removePatchViaCliUsingRollback(BASIC_PATCH_NAME);
        }
    }

    @Test
    public void applyPatchWithManualRestart() {

        prepareAndApplyPatchViaCli(basicPatchFile, BASIC_PATCH_NAME);
        Assert.assertTrue("Missing information that the restart is required",
                patchManagementPage.getInfoTable().get("Latest Applied Patch").contains("Pending restart"));
        patchManagementPage.restartServer().confirm();

        serverManager.waitUntilAvailable();
        browser.navigate().refresh();

        Assert.assertFalse("Restart was performed but pending is still displayed",
                patchManagementPage.getInfoTable().get("Latest Applied Patch").contains("Pending restart"));
    }
}
