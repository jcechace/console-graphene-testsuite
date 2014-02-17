package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.ResourceTableFragment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 22/02/14.
 */
public class ConfigPage extends BasePage {
    public ResourceTableFragment getResourceTable() {
        By selector = ByJQuery.selector(".default-cell-table[role='grid']");
        WebElement tableRoot =  browser.findElement(selector);
        ResourceTableFragment table = Graphene.createPageFragment(ResourceTableFragment.class, tableRoot);

        return table;
    }

    public <T extends ConfigAreaFragment> T getConfig(Class<T> clazz) {
        By selector = getConfigSelector(); // TODO: replace with proper selector once there is a usable class
        WebElement configRoot = browser.findElement(selector);
        T config = Graphene.createPageFragment(clazz, configRoot);

        return config;
    }

    public ConfigAreaFragment getConfig() {
       return getConfig(ConfigAreaFragment.class);
    }

    // TODO: There is no id or usable class on config tabpane - thus this workaround
    private By getConfigSelector() {
        String selectionLabel = "//div[contains(@class, 'content-group-label') and contains(text(), 'Selection')]";
        By selector = By.xpath(selectionLabel + "/following::*[contains(@class, 'default-tabpanel')]");

        return selector;
    }
}
