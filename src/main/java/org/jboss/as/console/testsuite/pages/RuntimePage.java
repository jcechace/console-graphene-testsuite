package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.SwitchServerFragment;
import org.jboss.as.console.testsuite.util.Console;
import org.openqa.selenium.WebElement;

/**
 * Created by mvelas on 31.3.2014.
 */
@Location("#topology")
public class RuntimePage extends BasePage {

    /**
     * @return pop-up fragment for switching host/server context in domain mode
     */
    private SwitchServerFragment openSwitchPopUp() {
        WebElement changeButton = getNavigation().getRoot().findElement(SwitchServerFragment.BUTTON_SELECTOR);
        changeButton.click();

        Console console = Console.withBrowser(browser);
        return console.openedPopup(SwitchServerFragment.class);
    }

    public void switchContext(String host, String server) {
        SwitchServerFragment switchPopUp = openSwitchPopUp();
        switchPopUp.changeContext(host);
        switchPopUp.changeContext(server);
    }

    public String getServerContext() {
        SwitchServerFragment switchPopUp = openSwitchPopUp();
        return switchPopUp.getCurrentServerName();
    }

    public String getHostContext() {
        SwitchServerFragment switchPopUp = openSwitchPopUp();
        return switchPopUp.getCurrentHostName();
    }
}
