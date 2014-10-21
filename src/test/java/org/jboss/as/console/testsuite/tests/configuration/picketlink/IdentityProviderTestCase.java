package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.pages.config.FederationPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.jboss.qa.management.cli.CliClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class IdentityProviderTestCase {

    private static CliClient cliClient = CliProvider.getClient();

    @Drone
    private WebDriver browser;
    @Page
    private FederationPage federationPage;

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderTestCase.class);

    public static final String FEDERATION = "test-federation";
    public static final String FEDERATION_ADDR = "/subsystem=picketlink-federation/federation=" + FEDERATION;
    public static final String IDP_WAR = "idp-post.war";
    public static final String IDP_RESOURCE_PATH = "/picketlink/" + IDP_WAR;
    public static final String IDP_ADDR = FEDERATION_ADDR +"/identity-provider=external-idp";
    public static final String IDP_EXT_ADDR = FEDERATION_ADDR + "/identity-provider=" + FEDERATION + "-external-idp";


    @BeforeClass
    public static void stupClass() {
        String deployment = IdentityProviderTestCase.class.getResource(IDP_RESOURCE_PATH).getPath();
        cliClient.executeCommand("deploy " + deployment);
    }

    @AfterClass
    public static void tearDownClass() {
        cliClient.executeCommand("undeploy " + IDP_WAR);
    }

    @Before
    public void setup() {
        cliClient.executeCommand(FEDERATION_ADDR + ":add()");

        browser.navigate().refresh();
        Graphene.goTo(FederationPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        federationPage.getResourceManager().viewByName(FEDERATION);
        federationPage.switchToIdentityProvider();
    }

    @After
    public void tearDown() {
        cliClient.executeCommand(FEDERATION_ADDR + ":remove()");
    }

    @Test
    public void addExternalIdentityProvider() {
        ResourceManager rm = federationPage.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.checkbox("external", true);
        editor.text("url", "http://localhost:8080/foo");            //this URL does not exist

        wizard.assertFinish(true);

        boolean exists = cliClient.executeForSuccess(IDP_EXT_ADDR + ":read-resource()");
        Assert.assertTrue("Added IDP should exist", exists);
    }

    @Test
    public void addIdentityProvider() {
        ResourceManager rm = federationPage.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.checkbox("external", false);
        editor.select("name", IDP_WAR);
        editor.select("securityDomain", "other");

        wizard.assertFinish(true);

        boolean exists = cliClient.executeForSuccess(IDP_ADDR + ":read-resource()");
        Assert.assertTrue("Added IDP should exist", exists);
    }

    @Test
    public void removeIdentityProvider() {
        cliClient.executeCommand(IDP_EXT_ADDR + ":add(external=true, url=\"url\")");
        browser.navigate().refresh();

        ResourceManager rm = federationPage.getResourceManager();
        rm.viewByName(FEDERATION);

        ConfirmationWindow confirmationWindow = rm.removeResource("external-idp");
        confirmationWindow.confirm();

        boolean exists = cliClient.executeForSuccess(IDP_EXT_ADDR + ":read-resource()");
        Assert.assertFalse("Removed IDP should not exist", exists);
    }
}
