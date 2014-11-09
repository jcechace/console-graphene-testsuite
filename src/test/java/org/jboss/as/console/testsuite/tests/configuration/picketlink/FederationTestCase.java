package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.config.federation.FederationConfigArea;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.pages.config.FederationPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.jboss.qa.management.cli.CliClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class FederationTestCase {

    private CliClient cliClient = CliProvider.getClient();
    @Drone
    private WebDriver browser;
    @Page
    private FederationPage federationPage;

    private static final Logger log = LoggerFactory.getLogger(FederationTestCase.class);

    public static final String FEDERATION_GUI = "test-federation-gui";
    public static final String FEDERATION_GUI_ADDR = "/subsystem=picketlink-federation/federation=" + FEDERATION_GUI;
    public static final String FEDERATION = "test-federation";
    public static final String FEDERATION_ADDR = "/subsystem=picketlink-federation/federation=" + FEDERATION;
    public static final String SAML_CFG_ADDR = FEDERATION_ADDR + "/saml=saml";


    @Before
    public void setup() {
        cliClient.executeCommand(FEDERATION_ADDR + ":add()");

        if (ConfigUtils.isDomain()) {
            federationPage.navigate("full");
        } else {
            federationPage.navigate();
        }
    }

    @After
    public void tearDown() {
        cliClient.executeCommand(FEDERATION_ADDR + ":remove()");
        cliClient.executeCommand(FEDERATION_GUI_ADDR + ":remove()");
    }

    @Test
    public void addFederation() {
        ResourceManager rm = federationPage.getResourceManager();
        WizardWindow wizardWindow = rm.addResource();
        Editor editor = wizardWindow.getEditor();
        editor.text("name", FEDERATION_GUI);
        wizardWindow.assertFinish(true);

        assertFederation(FEDERATION_GUI_ADDR, true);
    }

    @Test
    public void removeFederation() {
        ResourceManager rm = federationPage.getResourceManager();
        ConfirmationWindow window = rm.removeResource(FEDERATION);
        window.confirm();

        assertFederation(FEDERATION_ADDR, false);
    }

    @Test
    public void configureSAMLAttributes() {
        ResourceManager rm = federationPage.getResourceManager();
        rm.selectByName(FEDERATION);
        FederationConfigArea config = federationPage.getConfig();
        ConfigFragment samlConfig = config.samlConfig();

        Editor editor = samlConfig.edit();
        editor.text("tokenTimeout", "-10");
        Assert.assertFalse(samlConfig.save());

        editor.text("tokenTimeout", "10");
        editor.text("clockSkew", "-10");
        Assert.assertFalse(samlConfig.save());

        editor.text("clockSkew", "12");
        Assert.assertTrue(samlConfig.save());


        rm.setDmrPath(SAML_CFG_ADDR);
        rm.verifyAttribute("tokenTimeout", "10", cliClient);
        rm.verifyAttribute("clockSkew", "12", cliClient);
    }


    private void assertFederation(String addr, boolean shouldExist) {
        boolean result = cliClient.executeForSuccess(addr + ":read-resource");

        if (shouldExist) {
            Assert.assertTrue("federation named " + addr + " should exists", result);
        } else {
            Assert.assertFalse("federation named " + addr + " should not exist", result);
        }
    }
}
