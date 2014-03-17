package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.ResourceTableFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 22/02/14.
 */
public class ConfigPage extends BasePage {
    public ResourceTableFragment getResourceTable() {
        By selector = ByJQuery.selector(".default-cell-table[role='grid']:visible");
        WebElement tableRoot =  getContentRoot().findElement(selector);
        ResourceTableFragment table = Graphene.createPageFragment(ResourceTableFragment.class, tableRoot);

        return table;
    }

    /**
     * Returns the ConfigArea portion of page as given implementation.
     * Not reliable - you might need to override this method.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends ConfigAreaFragment> T getConfig(Class<T> clazz) {
        By selector = getConfigSelector(); // TODO: replace with proper selector once there is a usable class
        WebElement configRoot = getContentRoot().findElement(selector);
        T config = Graphene.createPageFragment(clazz, configRoot);

        return config;
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

    // TODO: There is no id or usable class on config tabpane - thus this workaround
    // TODO: Not reliable!
    private By getConfigSelector() {
        String selectionLabel =
                ".//div[contains(@class, 'content-group-label') and contains(text(), 'Selection')]";
        By selector =
                By.xpath(selectionLabel + "/following::*[contains(@class, 'default-tabpanel')]");

        return selector;
    }

    public <T extends WizardWindow> T addResource(Class<T> clazz, String label) {
        clickButton(label);

        T wizard = Console.withBrowser(browser).openedWizard(clazz);

        return wizard;
    }


    public <T extends WizardWindow> T addResource(Class<T> clazz) {
        String label = PropUtils.get("config.shared.add.label");
        return addResource(clazz, label);
    }

    public WizardWindow addResource() {
        return addResource(WizardWindow.class);
    }
}
