package org.jboss.as.console.testsuite.util;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.fragments.PopUpFragment;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.openqa.selenium.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by jcechace on 18/02/14.
 */
public class Console {
    private WebDriver browser;

    public static Console withBrowser(WebDriver browser) {
        Console console = new Console();
        console.browser = browser;

        return console;
    }

    // Prevent constructor instantiation
    private Console() {}

    /**
     * Wait until the application is loaded.
     */
    public void waitUntilLoaded() {
        // TODO: this should rather wait until the loading box is not present
        Graphene.waitAjax().until().element(By.className("header-panel")).is().present();
    }

    /**
     * Wait until content is loaded.
     */
    public void waitForContent() {
        // TODO: might be unreliable
        Graphene.waitAjax().until().element(By.className("content-header-label")).is().present();
    }

    /**
     *  Retrieves currently opened popup menu
     *
     * @param clazz
     * @param selector
     * @param <T>
     * @return currently opened menu
     */
    public  <T extends PopUpFragment> T openedPopup(Class<T> clazz, By selector) {
        Graphene.waitGui().until().element(PopUpFragment.ROOT_SELECTOR).is().present();

        WebElement popupRoot =  browser.findElement(selector);
        T popup = Graphene.createPageFragment(clazz, popupRoot);

        return popup;
    }

    public  <T extends PopUpFragment> T openedPopup(Class<T> clazz) {
        return openedPopup(clazz, PopUpFragment.ROOT_SELECTOR);
    }

    public  PopUpFragment openedPopup() {
        return openedPopup(PopUpFragment.class, PopUpFragment.ROOT_SELECTOR);
    }


    /**
     * Retrieves currently opened window using specified selector.
     *
     * @param clazz
     * @param selector
     * @param <T>
     * @return currently opened window
     */
    public <T extends WindowFragment> T openedWindow(Class<T> clazz, By selector) {
        Graphene.waitGui().until().element(selector).is().present();

        WebElement windowRoot = browser.findElement(selector);
        T window = Graphene.createPageFragment(clazz, windowRoot);

        return window;
    }

    /**
     * Retrieves currently opened window using default selector.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends WindowFragment> T openedWindow(Class<T> clazz) {
        return openedWindow(clazz, WindowFragment.ROOT_SELECTOR);

    }


    /**
     * Retrieves currently opened window with specified head title using default selector.
     *
     * @param headTitle
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends WindowFragment> T openedWindow(String headTitle, Class<T> clazz) {
        By selector = WindowFragment.ROOT_SELECTOR;
        Graphene.waitGui().until().element(selector).is().present();

        List<WebElement> windowEelements = browser.findElements(selector);
        T window = null;

        for (WebElement el : windowEelements) {
            window  = Graphene.createPageFragment(clazz, el);
            if (window.getHeadTitle().equals(headTitle)) {
                return window;
            }
        }
        // Window with given title not found
        throw new NoSuchElementException("Unable to find window with title '"+ headTitle
                + "' using " + selector);
    }

    public WindowFragment openedWindow() {
        return openedWindow(WindowFragment.class, WindowFragment.ROOT_SELECTOR);
    }


    public <T extends WizardWindow> T openedWizard(Class<T> clazz) {
        return openedWindow(clazz);
    }

    public WizardWindow openedWizard() {
        return openedWindow(WizardWindow.class);
    }




    public WebElement findElement(By selector, WebElement root) {
        WebElement element = null;
        if (root == null) {
            element = browser.findElement(selector);
        } else {
            element = root.findElement(selector);
        }
        return element;
    }

    public WebElement findElement(By selector, BaseFragment fragment) {
        WebElement root = fragment.getRoot();
        return findElement(selector, root);
    }

}
