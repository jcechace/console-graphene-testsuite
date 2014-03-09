package org.jboss.as.console.testsuite.fragments.config.datasources;

import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * @author jcechace
 */
public class DatasourceWizard extends WizardWindow {
    public TestConnectionWindow testConnection() {
        String label = PropUtils.get("config.datasources.configarea.connection.test.label");
        clickButton(label);
        Console.withBrowser(browser).waitUntilFinished();

        String windowTitle =  PropUtils.get("config.datasources.window.connection.test.head.label");
        TestConnectionWindow window =  Console.withBrowser(browser)
                .openedWindow(windowTitle, TestConnectionWindow.class);

        return window;
    }

}
