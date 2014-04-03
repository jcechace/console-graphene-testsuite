package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.runtime.ServerContextPicker;
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
    private ServerContextPicker openSwitchPopUp() {
        WebElement changeButton = getNavigation().getRoot().findElement(ServerContextPicker.BUTTON_SELECTOR);
        changeButton.click();

        Console console = Console.withBrowser(browser);
        return console.openedPopup(ServerContextPicker.class);
    }

    public void switchContext(String host, String server) {
        ServerContextPicker switchPopUp = openSwitchPopUp();
        switchPopUp.changeContext(host);
        switchPopUp.changeContext(server);
    }

    public String getServerContext() {
        ServerContextPicker switchPopUp = openSwitchPopUp();
        return switchPopUp.getCurrentServerName();
    }

    public String getHostContext() {
        ServerContextPicker switchPopUp = openSwitchPopUp();
        return switchPopUp.getCurrentHostName();
    }
}
