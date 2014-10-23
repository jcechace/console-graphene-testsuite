package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.pages.config.FederationPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.CliClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class ConfiguredIdpAbstract extends ConfiguredFederationAbstract{

    protected static CliClient cliClient = CliProvider.getClient();

    @Drone
    protected WebDriver browser;
    @Page
    protected FederationPage federationPage;

    // IdentityProvider
    public static final String IDP_WAR = "idp-post.war";
    public static final String IDP_RESOURCE_PATH = "/picketlink/" + IDP_WAR;
    public static final String IDP_WAR_URL = "http://example.com/idp/";
    public static final String IDP_ADDR = FEDERATION_ADDR + "/identity-provider=" + IDP_WAR;


    @BeforeClass
    public static void deployIdp() {
        String deployment = ConfiguredIdpAbstract.class.getResource(IDP_RESOURCE_PATH).getPath();
        cliClient.executeCommand("deploy " + deployment + " --disabled");
    }

    @AfterClass
    public static void undeployIdp() {
        cliClient.executeCommand("undeploy " + IDP_WAR);
        cliClient.executeCommand(IDP_ADDR + ":remove()");
    }

    @Before
    public void setupIdp() {
        cliClient.executeCommand(IDP_ADDR + ":add(external=true, url=\"" + IDP_WAR_URL + "\")");
    }

    @After
    public void tearDownIdp() {
        cliClient.executeCommand(IDP_ADDR + ":remove()");
    }
}
