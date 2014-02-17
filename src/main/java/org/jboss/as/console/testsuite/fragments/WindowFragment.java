package org.jboss.as.console.testsuite.fragments;

import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * Created by jcechace on 18/02/14.
 */
public class WindowFragment extends BaseFragment {

    public static final By ROOT_SELECTOR = By.className(PropUtils.get("modals.window.class"));

    protected boolean closed;

    public boolean isClosed() {
        try {
            if (closed) {
                return true;
            }
            root.isDisplayed();
            return false;
        } catch (NoSuchElementException ignored) {
            return true;
        }
    }

    public void waitUntilClosed(boolean fail) {
        try {
            Graphene.waitGui().until().element(root).is().not().present();
        } catch (TimeoutException e) {
            if (fail) {
                throw e;
            }
        }
    }

    public void waitUntilClosed() {
        waitUntilClosed(true);
    }


    public void close() {
        By selector = By.className(PropUtils.get("modals.window.close.class"));
        WebElement close = root.findElement(selector);

        close.click();
        waitUntilClosed();
        closed = true;
    }

    public void cancel() {
        String label = PropUtils.get("modals.window.cancel.label");
        clickButton(label, true);

        waitUntilClosed();
        closed = true;
    }


    public WindowFragment clickButton(String label, boolean close) {
        By selector = ByJQuery.selector("button:contains(" + label + ")");
        WebElement button = root.findElement(selector);

        button.click();
        if (close) {
            waitUntilClosed();
            closed = true;
        }
        if (isClosed()) {
            return null;
        }

        return this;
    }

    public <T extends WindowFragment> T clickButton(String label, Class<T> followingWindowType) {
        By selector = ByJQuery.selector("button:contains(" + label + ")");
        WebElement button = root.findElement(selector);

        button.click();
        if (isClosed()) {
            return null;
        }

        Console console = Console.withBrowser(browser);
        T followingWindow = console.openedWindow(followingWindowType);
        return  followingWindow;
    }

    public String getHeadTitle() {
        By selector = By.className(PropUtils.get("modals.window.title.class"));
        WebElement title = root.findElement(selector);

        return title.getText();
    }

    public String getTitle() {
        By selector = By.tagName("h3");
        WebElement title = root.findElement(selector);

        return title.getText();
    }

}
