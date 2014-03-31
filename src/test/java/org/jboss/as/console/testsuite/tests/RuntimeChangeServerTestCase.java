package org.jboss.as.console.testsuite.tests;

/**
 * Created by mvelas on 11.3.2014.
 */

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.RuntimePage;
import org.jboss.as.console.testsuite.pages.runtime.TopologyPage;
import org.jboss.as.console.testsuite.tests.categories.DomainTest;
import org.jboss.as.console.testsuite.util.Console;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@Category(DomainTest.class)
public class RuntimeChangeServerTestCase {

    static final String DEFAULT_SERVER = "server-one";
    static final String NEW_SERVER = "server-two";
    static final String HOST = "master";

    @Drone
    private WebDriver browser;

    @Page
    private RuntimePage runtimePage;
    private Console console;

    @Before
    public void setup() {
        Graphene.goTo(TopologyPage.class);
        console = Console.withBrowser(browser);
        console.waitUntilLoaded();
        browser.manage().deleteAllCookies();
    }

    @Test
    public void serverContextTest() {
        assertEquals(DEFAULT_SERVER, runtimePage.getServerContext());
        assertEquals(HOST, runtimePage.getHostContext());
        runtimePage.switchContext(HOST, NEW_SERVER);
        assertEquals(NEW_SERVER, runtimePage.getServerContext());
        assertEquals(HOST, runtimePage.getHostContext());
    }
}
