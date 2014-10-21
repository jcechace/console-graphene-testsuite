package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.tables.ResourceTableFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.AdvancedSelectBox;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.tables.ResourceTableRowFragment;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by jcechace on 22/02/14.
 */
public class ConfigPage extends BasePage {
    /**
     * Returns the ConfigArea portion of page as given implementation.
     * Not reliable - you might need to override this method.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends ConfigAreaFragment> T getConfig(Class<T> clazz) {

        WebElement configRoot = null;
        try {
            By selector = ByJQuery.selector("#master_detail-detail:visible");
            configRoot = getContentRoot().findElement(selector);
        } catch (NoSuchElementException e) { // TODO: this part should be removed once ensured the ID is everywhere
            List<WebElement> elements = getContentRoot().findElements(getConfigSelector());

            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    configRoot = element;
                }
            }
        }

        T config = Graphene.createPageFragment(clazz, configRoot);
        return config;
    }

    private By getConfigSelector() {
        String selectionLabel =
                ".//div[contains(@class, 'content-group-label') and (contains(text(), 'Selection') or contains(text(), 'Details'))]";
        By selector = By.xpath(selectionLabel + "/following::*[contains(@class, 'rhs-content-panel')]");

        return selector;
    }

    /**
     * Returns the default implementation of ConfigArea portion of page.
     * Not reliable - you might need to override this method.
     *
     * @return
     */
    public ConfigAreaFragment getConfig() {
        return getConfig(ConfigAreaFragment.class);
    }

    public <T extends WizardWindow> T addResource(Class<T> clazz, String label) {
        clickButton(label);

        T wizard = Console.withBrowser(browser).openedWizard(clazz);

        return wizard;
    }

    public void pickProfile(String label) {
        AdvancedSelectBox picker = getProfilePicker();
        picker.pickOption(label);

        Console.withBrowser(browser).waitUntilFinished();
    }

    public void pickHost(String label) {
        AdvancedSelectBox picker = getHostPicker();
        picker.pickOption(label);

        Console.withBrowser(browser).waitUntilFinished();
    }

    public AdvancedSelectBox getHostPicker() {
        return  getContextPicker("host");
    }


    public AdvancedSelectBox getProfilePicker() {
        return  getContextPicker("profile");
    }

    private AdvancedSelectBox getContextPicker(String label) {
        WebElement pickerRoot = getContextPickerRootByLabel(label);
        AdvancedSelectBox selectBox = Graphene.createPageFragment(AdvancedSelectBox.class,
                pickerRoot);

        return selectBox;

    }

    private WebElement getContextPickerRootByLabel(String label) {
        By selector = By.className(PropUtils.get("navigation.selector.class"));
        By pickerSelector = By.className(PropUtils.get("components.selectbox.class"));
        List<WebElement> elements = browser.findElements(selector);
        for (WebElement elem : elements) {
            if (elem.getText().toLowerCase().contains(label.toLowerCase())) {
                WebElement pickerRoot = elem.findElement(pickerSelector);
                return pickerRoot;
            }
        }

        throw new NoSuchElementException("Unable to find context picker root");
    }

    @Deprecated
    public ResourceTableFragment getResourceTable() {
        By selector = ByJQuery.selector("#master_detail-master:visible");
        WebElement tableRoot = getContentRoot().findElement(selector);
        ResourceTableFragment table = Graphene.createPageFragment(ResourceTableFragment.class, tableRoot);

        return table;
    }

    @Deprecated
    public <T extends WizardWindow> T addResource(Class<T> clazz) {
        String label = PropUtils.get("config.shared.add.label");
        return addResource(clazz, label);
    }

    @Deprecated
    public WizardWindow addResource() {
        return addResource(WizardWindow.class);
    }

    @Deprecated
    public <T extends WindowFragment> T removeResource(String name, Class<T> clazz) {
        selectByName(name);
        String label = PropUtils.get("config.shared.remove.label");
        clickButton(label);

        T window = Console.withBrowser(browser).openedWindow(clazz);

        return window;
    }

    @Deprecated
    public ConfirmationWindow removeResource(String name) {
        return removeResource(name, ConfirmationWindow.class);
    }

    /**
     * Select resource based on its name in firt column of resource table.
     *
     * @param name Name of the resource.
     */
    @Deprecated
    public ResourceTableRowFragment selectByName(String name) {
        return getResourceTable().selectRowByText(0, name);
    }

    /**
     * Select resource based on its name in first column of resource table and then
     * click on view option
     *
     * @param name Name of the resource.
     */
    @Deprecated
    public void viewByName(String name) {
        ResourceTableRowFragment row = selectByName(name);
        row.view();
    }

}
