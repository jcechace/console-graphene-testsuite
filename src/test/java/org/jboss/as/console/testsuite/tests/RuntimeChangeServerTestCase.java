package org.jboss.as.console.testsuite.tests;

/**
 * Created by mvelas on 11.3.2014.
 */

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.runtime.TopologyPage;
import org.jboss.as.console.testsuite.util.Console;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class RuntimeChangeServerTestCase {

    static final String DEFAULT_SERVER = "server-one";
    static final String NEW_SERVER = "server-two";
    static final String HOST = "master";

    @Drone
    private WebDriver browser;

    @Page
    private TopologyPage topologyPage;
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
        assertEquals(DEFAULT_SERVER, topologyPage.getServerContext());
        assertEquals(HOST, topologyPage.getHostContext());
        topologyPage.switchContext(HOST, NEW_SERVER);
        assertEquals(NEW_SERVER, topologyPage.getServerContext());
        assertEquals(HOST, topologyPage.getHostContext());
    }
}
