package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author jcechace
 */
public class ConfigAreaFragment extends  BaseFragment {

    public WebElement getTabByLabel(String label) {
        String tabClass = PropUtils.get("configarea.tab.class");
        By selector = ByJQuery.selector("." + tabClass + ":contains('" + label + "')");
        WebElement item = root.findElement(selector);
        return item;
    }

    public void clickTabByLabel(String label) {
        WebElement item = getTabByLabel(label);
        item.click();
        String selectedTabClass = PropUtils.get("configarea.tab.selected.class");
        Graphene.waitGui().until().element(item).attribute("class").contains(selectedTabClass);
    }

    public <T extends ConfigFragment> T switchTo(String label, Class<T> clazz) {
        clickTabByLabel(label);
        By selector = By.className(PropUtils.get("configarea.content.class"));
        WebElement contentRoot = root.findElement(selector);
        T content = Graphene.createPageFragment(clazz, contentRoot);

        return content;
    }

    public ConfigFragment switchTo(String label) {
        return switchTo(label, ConfigFragment.class);
    }



}
