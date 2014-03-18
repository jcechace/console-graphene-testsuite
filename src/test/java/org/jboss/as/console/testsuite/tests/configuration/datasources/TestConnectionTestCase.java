package org.jboss.as.console.testsuite.tests.configuration.datasources;

import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.config.datasources.ConnectionConfig;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceConfigArea;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceWizard;
import org.jboss.as.console.testsuite.fragments.config.datasources.TestConnectionWindow;
import org.jboss.as.console.testsuite.util.Editor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
        
/**
 * Created by jcechace on 21/02/14.
 */
@RunWith(Arquillian.class)
public class TestConnectionTestCase {
    
    private static final Logger log = LoggerFactory.getLogger(TestConnectionTestCase.class);
    
     @Drone
    private WebDriver browser;

    @Page
    private DatasourcesPage datasourcesPage;

    @Before
    public void setup() {
        Graphene.goTo(DatasourcesPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @After
    public void afer() {
        browser.navigate().refresh();
    }

    @Test
    public void validDatasource() {
        testConnection("ExampleDS", true);
    }

    @Test
    public void invalidDatasource() {
        String name = createInvalidDatasource();
        try {
            testConnection(name, false);
        } finally {
            removeInvalidDatasource(name, false);
        }
    }

    @Test
    public void validInWizard() {
       String name = RandomStringUtils.randomAlphabetic(6);
       testConnectionInWizard(name, "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1", null, null, true);
    }

    @Test
    public void invalidInWizard() {
        String name = RandomStringUtils.randomAlphabetic(6);
        testConnectionInWizard(name, "invalidUrl", null, null, false);
    }

    @Test
    public void validXADatasource() {
        datasourcesPage.switchTab("XA Datasources");

        String name = createValidXADatasource();
        testConnection(name, true);
    }


    private String createInvalidDatasource() {
        String name = RandomStringUtils.randomAlphanumeric(5);
        // TODO: implement me;
        log.debug(name);
        return "testds";
    }

    private void removeInvalidDatasource(String name, boolean xa) {
        // TODO : implement me;
    }

    private String createValidXADatasource() {
        return "testxads";
    }

    private String createInvalidXADatasource() {
        return "testxads2";
    }

    private void assertNotExists(String name) {
        // TODO: impelement me;
    }


    private void testConnection(String name, boolean expected) {
        datasourcesPage.selectByName(name);

        DatasourceConfigArea config = datasourcesPage.getConfig();
        ConnectionConfig connection = config.connectionConfig();
        TestConnectionWindow window = connection.testConnection();

        assertConnectionTest(window, expected);
    }

    private void testConnectionInWizard(String name, String url, String username,
                                        String password, boolean expected) {
        DatasourceWizard wizard = datasourcesPage.addResource();
        Editor editor = wizard.getEditor();

        editor.text("name", name);
        editor.text("jndiName", "java:/" + name);

        wizard.next();

        wizard.next();
        editor.text("connectionUrl", url);
        if (username != null) {
            editor.text("username", username);
        }
        if (password != null){
            editor.password("password", password);
        }

        assertConnectionTest(wizard.testConnection(), expected);
        assertNotExists(name);
    }

    private void assertConnectionTest(TestConnectionWindow window, boolean expected) {
        boolean result = window.isSuccessful();
        window.close();

        if (expected) {
            Assert.assertTrue("Connection test was expected to succeed", result);
        } else {
            Assert.assertFalse("Connection test was expected to fail", result);
        }
    }
}
