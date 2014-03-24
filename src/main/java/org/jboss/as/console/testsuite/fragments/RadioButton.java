package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mvelas on 24.3.2014.
 */

public class RadioButton extends BaseFragment {

    protected List<WebElement> choices;

    public void findChoices(String name) {
        ByJQuery selector = ByJQuery.selector("input:radio[name=" + name + "]");
        choices = root.findElements(selector);
    }

    public void pick(int index) {
        choices.get(index).click();
    }
}
