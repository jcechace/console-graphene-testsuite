package org.jboss.as.console.testsuite.tests.homepage;

import junit.framework.Assert;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.homepage.HomepageSideSectionFragment;
import org.jboss.as.console.testsuite.pages.home.HomePage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jcechace
 */
@Category(SharedTest.class)
@RunWith(Arquillian.class)
public class SideBarTestCase {

    @Drone
    private WebDriver browser;

    @Page
    private HomePage homePage;


    @Before
    public void setup() {
        Graphene.goTo(HomePage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }


    @Test
    public void generalResources() {
        String[] keys = {"doc", "about", "help", "training"};
        Map<String, String> links = expectedLinks("general", keys);

        String headLabel = PropUtils.get("homepage.sidebar.general.head.label");
        HomepageSideSectionFragment section = homePage.getSideBar().getSection(headLabel);

        assertLinksArePresent(section, links);
    }


    @Test
    public void developerResources() {
        String[] keys = {"quickstarts", "community"};
        Map<String, String> links = expectedLinks("dev", keys);

        String headLabel = PropUtils.get("homepage.sidebar.dev.head.label");
        HomepageSideSectionFragment section = homePage.getSideBar().getSection(headLabel);

        assertLinksArePresent(section, links);
    }

    @Test
    public void operationalResources() {
        String[] keys = {"matrix", "kb", "consulting"};
        Map<String, String> links = expectedLinks("operational", keys);

        String headLabel = PropUtils.get("homepage.sidebar.operational.head.label");
        HomepageSideSectionFragment section = homePage.getSideBar().getSection(headLabel);

        assertLinksArePresent(section, links);
    }

    public void assertLinksArePresent(HomepageSideSectionFragment section,
                                      Map<String, String> links) {
        Map<String, String> sectionLinks = section.getAllLinks();

        for (String label : links.keySet()) {
            Assert.assertTrue("Link <" + label + "> not found", sectionLinks.containsKey(label));
            Assert.assertEquals("Different address for link <" + label + ">", links.get(label),
                    sectionLinks.get(label));
            sectionLinks.remove(label);
        }

        Assert.assertTrue("Unexpected links: " + sectionLinks, sectionLinks.isEmpty());
    }

    private Map<String, String> expectedLinks(String sectionKey, String[] linkKeys) {
        Map<String, String> links = new HashMap<String, String>();

        for (String link : linkKeys) {
            String key = "homepage.sidebar." + sectionKey + ".links." + link;
            String label = PropUtils.get(key + ".label");
            String href = PropUtils.get(key + ".href");
            links.put(label, href);
        }

        return links;
    }
}
