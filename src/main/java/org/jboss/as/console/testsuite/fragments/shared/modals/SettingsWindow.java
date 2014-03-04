package org.jboss.as.console.testsuite.fragments.shared.modals;

import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * Created by jcechace on 18/02/14.
 */
public class SettingsWindow extends WindowFragment {

    public boolean getAnalytics() {
        String id = PropUtils.get("settings.analytics.id");


        return getEditor().checkbox(id);
    }

    public boolean setAnalytics(boolean val) {
        boolean old = getAnalytics();
        String id = PropUtils.get("settings.analytics.id");
        getEditor().checkbox(id, val);

        return old;
    }

    public void save(boolean reload) {
        this.saveExpectingConfigrmation().confirm();
        if (reload) {
            browser.navigate().refresh();
            Console.withBrowser(browser).waitUntilLoaded();
        }
    }

    public ConfirmationWindow saveExpectingConfigrmation() {
        String label = PropUtils.get("modals.window.save.label");

        clickButton(label);
        ConfirmationWindow confirmWindow = Console.withBrowser(browser)
                .openedWindow(ConfirmationWindow.class);
        closed = true;

        return confirmWindow;
    }
}
