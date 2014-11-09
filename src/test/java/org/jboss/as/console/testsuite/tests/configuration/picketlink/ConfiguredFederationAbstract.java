package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.pages.config.FederationPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.security.auth.login.ConfigUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class ConfiguredFederationAbstract {

    protected static CliClient cliClient = CliProvider.getClient();

    @Drone
    protected WebDriver browser;
    @Page
    protected FederationPage federationPage;

    // Federation
    public static final String FEDERATION = "test-federation";
    public static final String FEDERATION_ADDR = "/subsystem=picketlink-federation/federation=" + FEDERATION;



    @BeforeClass
    public static void setupFederation() {
        cliClient.executeCommand(FEDERATION_ADDR + ":add()");
    }

    @AfterClass
    public static void tearDownFederation() {
        cliClient.executeCommand(FEDERATION_ADDR + ":remove()");
    }

    protected void navigateToFederation() {
        browser.navigate().refresh();
        if (ConfigUtils.isDomain()) {
            federationPage.navigate("full");
        } else {
            federationPage.navigate();
        }
        ResourceManager rm = federationPage.getResourceManager();
        rm.viewByName(FEDERATION);
    }
}
