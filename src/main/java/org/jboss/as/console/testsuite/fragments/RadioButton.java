package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Implementation of radio input element.
 * Created by mvelas on 24.3.2014.
 */
public class RadioButton extends BaseFragment {

    /**
     * All radio buttons relevant to single choice.
     */
    protected List<WebElement> choices;

    /**
     * Finds all radio input elements related to choice of given name.
     * @param name name of the value the radio buttons set
     */
    public void findChoices(String name) {
        ByJQuery selector = ByJQuery.selector("input:radio[name=" + name + "]");
        choices = root.findElements(selector);
    }

    /**
     * Picks the radio button.
     * @param index index of the button to select
     */
    public void pick(int index) {
        choices.get(index).click();
    }

    /**
     * @return index of currently selected radio button
     */
    public int getIndex() {
        int i = 0;
        for(WebElement choice : choices) {
            if(choice.isSelected()) {
                return i;
            }
            i++;
        }
        return -1;  // nothing found
    }

    /**
     * @param i index of the radio butotn
     * @return radio input of i-th choice
     */
    public WebElement getInputElement(int i) {
        return choices.get(i);
    }
}
