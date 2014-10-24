package org.jboss.as.console.testsuite.tests.configuration.web.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.pages.config.ServletPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.CliConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class GlobalSessionTimeoutTestCase {

    private static final String DEFAULT_SESSION_TIMEOUT_CONSOLE_ID = "default-session-timeout";
    private static final String DEFAULT_SESSION_TIMEOUT_CLI_ATTR_NAME = DEFAULT_SESSION_TIMEOUT_CONSOLE_ID;
    private static final String DEFAULT_SESSION_TIMEOUT = "30";
    private static final String CHANGED_SESSION_TIMEOUT = "7";

    private static CliClient cliClient = CliProvider.getClient();

    @Drone
    private WebDriver browser;

    @Page
    private ServletPage servletPage;

    @Before
    public void setup() {
        browser.navigate().refresh();
        Graphene.goTo(ServletPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @After
    public void cleanUp() {
        cliClient.writeAttribute(CliConstants.WEB_SUBSYSTEM_ADDRESS, DEFAULT_SESSION_TIMEOUT_CLI_ATTR_NAME,
                DEFAULT_SESSION_TIMEOUT);
        cliClient.reload(false);
    }

    @Test
    public void changeSessionTimeout() {
        ConfigFragment globalConfig = servletPage.getConfig().global();
        Editor editor = globalConfig.edit();

        assertEquals(DEFAULT_SESSION_TIMEOUT, editor.text(DEFAULT_SESSION_TIMEOUT_CONSOLE_ID));

        editor.text(DEFAULT_SESSION_TIMEOUT_CONSOLE_ID, "-20");
        assertFalse(globalConfig.save());

        editor.text(DEFAULT_SESSION_TIMEOUT_CONSOLE_ID, CHANGED_SESSION_TIMEOUT);
        assertTrue(globalConfig.save());

        cliClient.reload(false);
        assertEquals(CHANGED_SESSION_TIMEOUT,
                cliClient.readAttribute(CliConstants.WEB_SUBSYSTEM_ADDRESS, DEFAULT_SESSION_TIMEOUT_CLI_ATTR_NAME));
    }
}
