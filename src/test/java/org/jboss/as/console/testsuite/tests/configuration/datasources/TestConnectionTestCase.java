package org.jboss.as.console.testsuite.tests.configuration.datasources;

import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.datasources.ConnectionConfig;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceConfigArea;
import org.jboss.as.console.testsuite.fragments.config.datasources.TestConnectionWindow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.util.Console;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 21/02/14.
 */
@RunWith(Arquillian.class)
public class TestConnectionTestCase {
    @Drone
    private WebDriver browser;

    @Page
    private DatasourcesPage datasourcesPage;

    @Before
    public void setup() {
        Graphene.goTo(DatasourcesPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @Test
    public void validDatasource() {
        testConnectionAndAssert("ExampleDS", true);
    }

    @Test
    public void invalidDatasource() {
        String name = createInvalidDatasource();
        try {
            testConnectionAndAssert(name, false);
        } finally {
            removeInvalidDatasource(name);
        }
    }

    @Test
    public void validInWizard() {
        datasourcesPage.addDatasource();
    }

    private String createInvalidDatasource() {
        String name = RandomStringUtils.randomAlphanumeric(5);
        // TODO: implement me;
        System.err.println(name);
        return "aaa";
    }

    private void removeInvalidDatasource(String name) {
        // TODO : implement me;
    }

    private void testConnectionAndAssert(String name, boolean expected) {
        datasourcesPage.selectByName(name);

        DatasourceConfigArea config = datasourcesPage.getConfig();
        ConnectionConfig connection = config.connectionConfig();
        TestConnectionWindow window = connection.testConnection();

        boolean success = window.isSuccessful();
        window.close();

        if (expected) {
            Assert.assertTrue("Connection test was expected to succeed", success);
        } else {
            Assert.assertFalse("Connection test was expected to fail", success);
        }
    }
}
