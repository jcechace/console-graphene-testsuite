package org.jboss.as.console.testsuite.util;

import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.*;

/**
 *  This class is pure EVIL and as such any method which uses it is in need of refactoring.
 *
 * @author jcechace
 */
public class Workaround {
    private WebDriver browser;
    private TakesScreenshot screenshot;

    public static Workaround withBrowser(WebDriver browser) {
        Workaround workaround = new Workaround();
        workaround.browser = browser;

        return workaround;
    }


    // TODO: fix problem with window not being opened after link is clicked
    public void clickLinkUntilWindowIsOpened(WebElement link, By selector) {
        boolean done = false;
        int attempts = 10;

        do {
            attempts = attempts - 1;
            try {
                link.click();
                Graphene.waitGui().until().element(selector).is().present();
                done = true;
            } catch (TimeoutException e) {
                if (attempts == 0) {
                    throw e;
                }
            }
        } while (done == false && attempts > 0);
    }
}
