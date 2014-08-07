package org.jboss.as.console.testsuite.tests.homepage;

import junit.framework.Assert;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.home.HomePage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.net.URL;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class EntryPointTestCase {
    @Drone
    private WebDriver browser;

    @Page
    private HomePage homePage;

    @Test
    public void entryPoint() {
        URL consoleURL = ConfigUtils.getUrl();
        browser.navigate().to(consoleURL);
        Console.withBrowser(browser).waitUntilLoaded();

        String currentUrl = browser.getCurrentUrl();

        Assert.assertTrue("Home page  should be an entry point to console",
                currentUrl.endsWith("#home"));
    }
}
