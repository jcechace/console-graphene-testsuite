package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.config.FederationPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
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
public class ConfiguredSpAbstract extends ConfiguredIdpAbstract{

    protected static CliClient cliClient = CliProvider.getClient();

    @Drone
    protected WebDriver browser;
    @Page
    protected FederationPage federationPage;

    // Service Provider
    public static final String SP_WAR = "sp-post1.war";
    public static final String SP_ADDR = FEDERATION_ADDR +"/service-provider=" + SP_WAR;
    public static final String SP_URL = "http://example.com/sp/";


    @Before
    public void setupSp() {
        cliClient.executeCommand(SP_ADDR + ":add(security-domain=\"jboss-web-policy\", " +
                "url=\"" + SP_URL  + "\")");
    }

    @After
    public void tearDownSp() {
        cliClient.executeCommand(SP_ADDR + ":remove()");
    }
}
