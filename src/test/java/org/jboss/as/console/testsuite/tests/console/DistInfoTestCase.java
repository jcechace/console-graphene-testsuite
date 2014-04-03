package org.jboss.as.console.testsuite.tests.console;

import junit.framework.Assert;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.tables.InfoTable;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.pages.runtime.OverviewPage;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.qa.management.cli.CliClient;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(StandaloneTest.class)
public class DistInfoTestCase {

    private static final Logger log = LoggerFactory.getLogger(DistInfoTestCase.class);

    private static final CliClient cliClient = CliProvider.getClient();

    @Drone
    private WebDriver browser;

    @Page
    private OverviewPage overviewPage;

    private InfoTable infoTable;

    @Before
    public void before() {
        Graphene.goTo(OverviewPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        infoTable = overviewPage.getInfoTable();

    }

    @Test
    public void checkCodeName() {
        String expected = cliClient.readAttribute("/", "release-codename");
        String actual = infoTable.get(PropUtils.get("runtime.overview.info.codename.label"));

        Assert.assertEquals(expected , actual);
    }

    @Test
    public void checkVersion() {
        String expected = cliClient.readAttribute("/", "product-version");
        String actual = infoTable.get(PropUtils.get("runtime.overview.info.version.label"));

        Assert.assertEquals(expected , actual);
    }
}
