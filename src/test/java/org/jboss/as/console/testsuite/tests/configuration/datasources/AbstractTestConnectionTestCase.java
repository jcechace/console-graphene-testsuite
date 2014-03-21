package org.jboss.as.console.testsuite.tests.configuration.datasources;

import junit.framework.Assert;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.as.console.testsuite.fragments.config.datasources.ConnectionConfig;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceConfigArea;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceWizard;
import org.jboss.as.console.testsuite.fragments.config.datasources.TestConnectionWindow;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.util.Editor;
import org.jboss.as.console.testsuite.util.PropertyEditor;
import org.jboss.qa.management.cli.CliClient;
import org.openqa.selenium.WebDriver;

/**
 * @author jcechace
 */
public abstract class AbstractTestConnectionTestCase {
    @Drone
    protected WebDriver browser;

    @Page
    protected DatasourcesPage datasourcesPage;

    protected static CliClient cliClient;

    protected void testConnection(String name, boolean expected) {
        datasourcesPage.selectByName(name);

        DatasourceConfigArea config = datasourcesPage.getConfig();
        ConnectionConfig connection = config.connectionConfig();
        TestConnectionWindow window = connection.testConnection();

        assertConnectionTest(window, expected);
    }

    protected void testConnectionInWizard(String name, String url, boolean expected) {
        DatasourceWizard wizard = datasourcesPage.addResource();
        Editor editor = wizard.getEditor();

        editor.text("name", name);
        editor.text("jndiName", "java:/" + name);

        wizard.next();

        wizard.next();
        editor.text("connectionUrl", url);

        assertConnectionTest(wizard.testConnection(), expected);
        DSTestUtils.assertNotExists(cliClient, name, false);
    }

    protected void testXAConnectionInWizard(String name, String url, boolean expected) {
        DatasourceWizard wizard = datasourcesPage.addResource();
        Editor editor = wizard.getEditor();

        editor.text("name", name);
        editor.text("jndiName", "java:/" + name);

        wizard.next();

        wizard.next();
        PropertyEditor properties = editor.properties();
        properties.add("URL", url);

        wizard.next();

        assertConnectionTest(wizard.testConnection(), expected);
        DSTestUtils.assertNotExists(cliClient, name, true);
    }

    protected void assertConnectionTest(TestConnectionWindow window, boolean expected) {
        boolean result = window.isSuccessful();
        window.close();

        if (expected) {
            Assert.assertTrue("Connection test was expected to succeed", result);
        } else {
            Assert.assertFalse("Connection test was expected to fail", result);
        }
    }
}
