package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 01/03/14.
 */
public class ConfigFragment extends BaseFragment {
    public void clickButton(String label) {
        WebElement button = getButton(label);
        button.click();
    }

    public WebElement getButton(String label) {
        By selector = ByJQuery.selector("button:contains('" + label + "')");
        WebElement button = root.findElement(selector);
        return button;
    }
}
