package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.Editor;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

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
        clickButton(label);

        waitUntilClosed();
        closed = true;
    }

    public void clickButton(String label) {
        By selector = ByJQuery.selector("button:contains(" + label + "):visible");
        WebElement button = root.findElement(selector);

        button.click();
    }

    public Editor getEditor() {
        Editor editor = Graphene.createPageFragment(Editor.class, root);

        return editor;
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
