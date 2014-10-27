package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.config.federation.ServiceProviderConfigArea;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
public class SpSamlHandlerTestCase extends ConfiguredSpAbstract {

    private static final Logger log = LoggerFactory.getLogger(SpSamlHandlerTestCase.class);

    public static final String HANDLER = "test-handler";
    public static final String HANDLER_CLASS="com.example.Handler";
    public static final String HANDLER_ADDR = SP_ADDR + "/handler=" + HANDLER;
    public static final String HANDLER_GUI_CLASS="com.example.GuiHandler";
    public static final String HANDLER_GUI_ADDR = SP_ADDR + "/handler=" + HANDLER_GUI_CLASS;

    @Before
    public void setupSamlHandler() {
        cliClient.executeCommand(HANDLER_ADDR + ":add(class-name='" + HANDLER_CLASS + "')");
        navigateToFederation();
        federationPage.switchToServiceProvider();
    }

    @After
    public void tearDownSamlHandler() {
        cliClient.executeCommand(HANDLER_ADDR + ":remove()");
    }

    @Test
    public void removeHandler() {
        ServiceProviderConfigArea config = federationPage.getSpConfig();
        ConfigFragment samlConfig = config.samlConfig();

        ResourceManager rm = samlConfig.getResourceManager(HANDLER_ADDR, cliClient);
        rm.removeResourceAndConfirm(HANDLER);

        rm.verifyResource(false);
    }

    @Test
    public void addHandler() {
        addProcessHandlerWizard(HANDLER_GUI_CLASS, true);

        ResourceManager rm = federationPage.getResourceManager(HANDLER_GUI_ADDR, cliClient);
        rm.verifyResource(true);
        rm.verifyAttribute("className", HANDLER_GUI_CLASS, cliClient);
    }


    @Test
    public void addHandlerEmptyClass() {
        addProcessHandlerWizard("", false);

        ResourceManager rm = federationPage.getResourceManager(HANDLER_GUI_ADDR, cliClient);
        rm.verifyResource(false);
    }

    private void addProcessHandlerWizard(String className, boolean shouldFinish) {
        ServiceProviderConfigArea config = federationPage.getSpConfig();
        ConfigFragment samlConfig = config.samlConfig();

        ResourceManager rm = samlConfig.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.text("className", className);

        wizard.assertFinish(shouldFinish);
    }
}
