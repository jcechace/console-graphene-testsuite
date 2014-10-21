package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
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
public class IdpConfigAbstract {

    @Drone
    protected WebDriver browser;
    @Page
    protected FederationPage federationPage;

    protected static CliClient cliClient = CliProvider.getClient();

    public static final String FEDERATION = "test-federation";
    public static final String IDP_WAR = "idp-post.war";
    public static final String IDP_WAR_URL = "http://localhost:8080/idp/";
    public static final String FEDERATION_ADDR = "/subsystem=picketlink-federation/federation=" + FEDERATION;
    public static final String IDP_ADDR = FEDERATION_ADDR + "/identity-provider=" + IDP_WAR;


    @BeforeClass
    public static void setupClass() {
        String deployment = IdentityProviderTestCase.class
                .getResource("/picketlink/" + IDP_WAR).getPath();
        cliClient.executeCommand("deploy " + deployment);
    }

    @AfterClass
    public static void tearDownClass() {
        cliClient.executeCommand("undeploy " + IDP_WAR);
    }

    @Before
    public void setup() {
        cliClient.executeCommand(FEDERATION_ADDR + ":add()");
        cliClient.executeCommand(IDP_ADDR + ":add(security-domain=\"other\", url=\"" + IDP_WAR_URL + "\")");
    }

    protected void setupNavigate() {
        browser.navigate().refresh();
        Graphene.goTo(FederationPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        federationPage.viewByName(FEDERATION);
    }

    @After
    public void tearDown() {
        cliClient.executeCommand(FEDERATION_ADDR + ":remove()");
    }

}
