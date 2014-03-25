package org.jboss.as.console.testsuite.tests.console.homepage;

import junit.framework.Assert;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.homepage.HomepageSectionFragment;
import org.jboss.as.console.testsuite.pages.BasePage;
import org.jboss.as.console.testsuite.pages.admin.RoleAssignmentPage;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.pages.domain.ServerGroupsPage;
import org.jboss.as.console.testsuite.pages.home.HomePage;
import org.jboss.as.console.testsuite.pages.runtime.DeploymentPage;
import org.jboss.as.console.testsuite.pages.runtime.DomainDeploymentPage;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.pages.runtime.TopologyPage;
import org.jboss.as.console.testsuite.tests.categories.DomainTest;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
public class SectionTestCase {
    @Drone
    private WebDriver browser;

    @Page
    private HomePage homePage;

    @Before
    public void setup() {
        Graphene.goTo(HomePage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }


    @Test @Category(SharedTest.class)
    public void configurationSection() {
        String id = PropUtils.get("homepage.section.config.header.id");

        assertSectionIsPresent(id);
        assertLinkInSection(id, "config", "ds", DatasourcesPage.class);
    }

    @Test @Category(StandaloneTest.class)
    public void runtimeSectionStandalone() {
        String id = PropUtils.get("homepage.section.runtime.header.id");
        assertSectionIsPresent(id);

        assertLinkInSection(id, "runtime", "deployment", DeploymentPage.class);
        assertLinkInSection(id, "runtime", "patching", PatchManagementPage.class);
    }

    @Test @Category(DomainTest.class)
    public void runtimeSectionDomain() {
        String id = PropUtils.get("homepage.section.runtime.header.id");
        assertSectionIsPresent(id);

        assertLinkInSection(id, "runtime", "deployment", DomainDeploymentPage.class);
        assertLinkInSection(id, "runtime", "topology", TopologyPage.class);
    }

    @Test @Category(SharedTest.class)
    public void administrationSection() {
        String id = PropUtils.get("homepage.section.admin.header.id");

        assertSectionIsPresent(id);
        assertLinkInSection(id, "admin", "role", RoleAssignmentPage.class);
    }


    @Test @Category(DomainTest.class)
    public void domainSection() {
        String id = PropUtils.get("homepage.section.domain.header.id");

        assertSectionIsPresent(id);
        assertLinkInSection(id, "domain", "patching", PatchManagementPage.class);
        assertLinkInSection(id, "domain", "servergroups", ServerGroupsPage.class);
    }

    private void assertSectionIsPresent(String id) {
        try {
            HomepageSectionFragment section = homePage.getSection(id);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to find section " + id);
        }
    }

    private void assertLinkInSection(String id, String sectionKey, String linkKey,
                                     Class<? extends BasePage> page) {
        String key = "homepage.section." + sectionKey + ".link." + linkKey + ".label";
        String label = PropUtils.get(key);

        HomepageSectionFragment section = homePage.getSection(id);
        section.open();

        String link = ConfigUtils.getPageLocation(page);
        try {
            section.getLink(label, link);
        } catch (NoSuchElementException e) {
            Assert.fail("Unable to find link " + label + "[" + link +"] in section" + id);
        }
    }

}
