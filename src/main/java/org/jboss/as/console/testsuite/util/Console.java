package org.jboss.as.console.testsuite.util;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.fragments.PopUpFragment;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.openqa.selenium.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
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

    public void refresh() {
        browser.navigate().refresh();
        waitUntilFinished();
    }

    /**
     * Wait until content is loaded.
     */
    public void waitForContent() {
        // TODO: might be unreliable
        Graphene.waitAjax().until().element(By.className("content-header-label")).is().present();
    }

    /**
     *  Waits until operation is finished (progress bar is hidden)
     */
    public void waitUntilFinished() {
        By selector = By.className("hal-ProgressElement");
        Graphene.waitModel().until().element(selector).is().not().visible();
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

    public int getWindowCount(By selector) {
        List<WebElement> elements = browser.findElements(selector);
        return elements.size();
    }

    public int getWindowCount() {
        return getWindowCount(WindowFragment.ROOT_SELECTOR);
    }

    public <T extends WizardWindow> T openedWizard(Class<T> clazz) {
        return openedWindow(clazz);
    }

    public WizardWindow openedWizard() {
        return openedWindow(WizardWindow.class);
    }


    public WebElement getTableRootByHeaderCell(String label, WebElement root) {
        String cssClass = PropUtils.get("tables.default.class");
        String tableSelector = "table[contains(@class, ': + cssClass + ')";
        String headerSelector = "//th[contains(text(), '" + label + "']";
        By selector = By.xpath(".//" +tableSelector + headerSelector +
                "/descendant::" + tableSelector);

        WebElement tableRoot = findElement(selector, root);

        return tableRoot;
    }

    public WebElement getTableRootByHeaderCell(String label) {
        return getTableRootByHeaderCell(label, null);
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

    /**
     * Robot swithes focus into next field of GUI.
     *
     * @param robot executor
     */
    protected void switchField(Robot robot) {
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
    }

    /**
     * Robot types character into active input filed of GUI. First, the string is storred into clipboard
     * and then the string is copied (pressing Ctrl+V) into the input field.
     *
     * @param characters to be typed
     * @param robot      executor
     */
    protected void type(String characters, Robot robot) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(characters);
        clipboard.setContents(stringSelection, stringSelection);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * Method authenticates into webconsole.
     *
     * @param username name of user
     * @param password password of the user for the authentication
     */
    public boolean authenticate(String username, String password) {
        int minWaitTime = 1 * 1000;

        try {
            // waiting for login page to be loaded
            Thread.sleep(minWaitTime);

            Robot robot = new Robot();
            Alert alert = browser.switchTo().alert();
            alert.sendKeys(username);

            switchField(robot);
            Thread.sleep(minWaitTime);
            type(password, robot);
            Thread.sleep(minWaitTime);

            alert.accept();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
