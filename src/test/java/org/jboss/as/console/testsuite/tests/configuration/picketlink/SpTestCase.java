package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class SpTestCase extends ConfiguredSpAbstract {

    private static final Logger log = LoggerFactory.getLogger(SpTestCase.class);

    // Service providers
    public static final String SP_GUI_WAR = "sp-post1-gui.war";
    public static final String SP_GUI_ADDR = FEDERATION_ADDR +"/service-provider=" + SP_GUI_WAR;
    public static final String SP_GUI_URL = "http://example.com/sp-gui/";


    @Before
    public void setup() {
        navigateToFederation();
        federationPage.switchToServiceProvider();
    }

    @After
    public void tearDown() {
        cliClient.executeCommand(SP_GUI_ADDR + ":remove()");

    }

    @Test
    public void addServiceProvider() {
        ResourceManager rm = federationPage.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.text("name", SP_GUI_WAR);
        editor.select("securityDomain", "jboss-web-policy");
        editor.text("url", SP_GUI_URL);

        wizard.assertFinish(true);

        boolean exists = cliClient.executeForSuccess(SP_GUI_ADDR + ":read-resource");
        Assert.assertTrue("Added Service Provider should exist", exists);

        rm.setDmrPath(SP_GUI_ADDR);
        rm.verifyAttribute("securityDomain", "jboss-web-policy", cliClient);
        rm.verifyAttribute("url", SP_GUI_URL, cliClient);
    }

    @Test
    public void addServiceProviderEmpty() {
        ResourceManager rm = federationPage.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        wizard.assertFinish(false);

        editor.text("name", SP_GUI_WAR);
        wizard.assertFinish(false);

        editor.text("name", "");
        editor.select("securityDomain", "jboss-web-policy");
        wizard.assertFinish(false);


        editor.text("name", SP_GUI_WAR);
        editor.text("url", "");

        wizard.assertFinish(true);

        boolean exists = cliClient.executeForSuccess(SP_GUI_ADDR + ":read-resource");
        Assert.assertTrue("Added Service Provider should exist", exists);
    }

    @Test
    public void removeServiceProvider() {
        ResourceManager rm = federationPage.getResourceManager();

        ConfirmationWindow confirmationWindow = rm.removeResource(SP_WAR);
        confirmationWindow.confirm();

        boolean exists = cliClient.executeForSuccess(SP_ADDR + ":read-resource()");
        Assert.assertFalse("Removed IDP should not exist", exists);
    }

}
