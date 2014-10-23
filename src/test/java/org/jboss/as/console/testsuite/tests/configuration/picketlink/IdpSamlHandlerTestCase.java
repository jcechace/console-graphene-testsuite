package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.config.federation.IdentityProviderConfigArea;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class IdpSamlHandlerTestCase extends ConfiguredIdpAbstract {

    private static final Logger log = LoggerFactory.getLogger(IdpSamlHandlerTestCase.class);

    public static final String HANDLER = "test-handler";
    public static final String HANDLER_CLASS="com.example.Handler";
    public static final String HANDLER_ADDR = IDP_ADDR + "/handler=" + HANDLER;
    public static final String HANDLER_GUI_CLASS="com.example.GuiHandler";
    public static final String HANDLER_GUI_ADDR = IDP_ADDR + "/handler=" + HANDLER_GUI_CLASS;

    @Before
    public void setupSamlHandler() {
        cliClient.executeCommand(HANDLER_ADDR + ":add(class-name='" + HANDLER_CLASS + "')");
        navigateToFederation();
        federationPage.switchToIdentityProvider();
    }

    @After
    public void tearDownSamlHandler() {
        cliClient.executeCommand(HANDLER_ADDR + ":remove()");
    }

    @Test
    public void removeHandler() {
        IdentityProviderConfigArea config = federationPage.getIdpConfig();
        ConfigFragment samlConfig = config.samlConfig();
        ResourceManager rm = samlConfig.getResourceManager();

        ConfirmationWindow confirmationWindow = rm.removeResource(HANDLER);
        confirmationWindow.confirm();

        boolean exists = cliClient.executeForSuccess(HANDLER_ADDR + ":read-resource()");
        Assert.assertFalse("Removed SAML handler should not exist", exists);
    }

    @Test
    public void addHandler() {
        addProcessHandlerWizard(HANDLER_GUI_CLASS, true);

        boolean exists = cliClient.executeForSuccess(HANDLER_GUI_ADDR + ":read-resource()");
        Assert.assertTrue("Added SAML handler should exist", exists);

        ResourceManager rm = federationPage.getResourceManager();
        rm.setDmrPath(HANDLER_GUI_ADDR);
        rm.verifyAttribute("className", HANDLER_GUI_CLASS, cliClient);
    }


    @Test
    public void addHandlerEmptyClass() {
        addProcessHandlerWizard("", false);
    }

    private void addProcessHandlerWizard(String className, boolean shouldFinish) {
        IdentityProviderConfigArea config = federationPage.getIdpConfig();
        ConfigFragment samlConfig = config.samlConfig();
        ResourceManager rm = samlConfig.getResourceManager();

        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.text("className", className);

        wizard.assertFinish(shouldFinish);
    }

}
