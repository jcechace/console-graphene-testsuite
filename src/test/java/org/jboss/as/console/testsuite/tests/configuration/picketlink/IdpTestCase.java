package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class IdpTestCase extends ConfiguredFederationAbstract {

    private static CliClient cliClient = CliProvider.getClient();

    private static final Logger log = LoggerFactory.getLogger(IdpTestCase.class);

    public static final String IDP_WAR = "idp-post.war";
    public static final String IDP_RESOURCE_PATH = "/picketlink/" + IDP_WAR;
    public static final String IDP_WAR_URL = "http://localhost:8080/idp/";
    public static final String IDP_ADDR = FEDERATION_ADDR + "/identity-provider=" + IDP_WAR;
    public static final String IDP_EXT = FEDERATION + "-external-idp";
    public static final String IDP_EXT_URL = "http://examplecom/external-idp/";
    public static final String IDP_EXT_ADDR = FEDERATION_ADDR + "/identity-provider=" + IDP_EXT;


    @BeforeClass
    public static void setupIdentityProvider() {
        String deployment = IdpTestCase.class.getResource(IDP_RESOURCE_PATH).getPath();
        cliClient.executeCommand("deploy " + deployment + " --disabled");
    }

    @AfterClass
    public static void tearDownIdentityProvider() {
        cliClient.executeCommand("undeploy " + IDP_WAR);
    }

    @Before
    public void setup() {
        navigateToFederation();
        federationPage.switchToIdentityProvider();
    }

    @After
    public void tearDown() {
        cliClient.executeCommand(IDP_ADDR + ":remove()");
        cliClient.executeCommand(IDP_EXT_ADDR + ":remove()");
    }

    @Test
    public void addExternalIdentityProvider() {
        ResourceManager rm = federationPage.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.checkbox("external", true);
        editor.text("url", IDP_EXT_URL);

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
        cliClient.executeCommand(IDP_EXT_ADDR + ":add(external=true, url=\"" + IDP_EXT_URL + "\")");

        navigateToFederation();
        federationPage.switchToIdentityProvider();

        ResourceManager rm = federationPage.getResourceManager();

        ConfirmationWindow confirmationWindow = rm.removeResource(IDP_EXT);
        confirmationWindow.confirm();

        boolean exists = cliClient.executeForSuccess(IDP_EXT_ADDR + ":read-resource()");
        Assert.assertFalse("Removed IDP should not exist", exists);
    }

}
