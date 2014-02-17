package org.jboss.as.console.testsuite.fragments.config.datasources;

import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * Created by jcechace on 02/03/14.
 */
public class ConnectionConfig extends ConfigFragment {
    public TestConnectionWindow testConnection() {
        String label = PropUtils.get("config.datasources.configarea.connection.test.label");
        clickButton(label);
        TestConnectionWindow window =  Console.withBrowser(browser).openedWindow(TestConnectionWindow.class);

        return window;
    }
}
