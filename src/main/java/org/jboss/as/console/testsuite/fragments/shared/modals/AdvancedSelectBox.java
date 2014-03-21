package org.jboss.as.console.testsuite.fragments.shared.modals;

import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author jcechace
 *
 * This class represents an advanced select box used for example as a profile picker
 * in Configuration section of the console. Unlike other fragments, a root attribute of this class
 * doesn't hold a wrapping element. Rather it represents a button-wrapping  element
 * which is used to display a list of all available options upon click.
 */
public class AdvancedSelectBox extends BaseFragment {

    public AdvancedSelectBoxOptions open() {
        WebElement elem = getValueElement();
        elem.click();

        AdvancedSelectBoxOptions options = Console.withBrowser(browser)
                .openedPopup(AdvancedSelectBoxOptions.class);

        return options;
    }

    public void pickOption(String label) {
        if (getValue().equalsIgnoreCase(label)) {
            return;
        }
        AdvancedSelectBoxOptions options = open();
        options.pick(label);
    }

    public String getValue() {
       WebElement element = getValueElement();

        return element.getText();
    }

    public WebElement getValueElement() {
        String clasName = PropUtils.get("components.selecbox.value.class");
        By selector = By.className(clasName);
        WebElement element = root.findElement(selector);

        return element;
    }

}
