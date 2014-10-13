package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.CliUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Map;

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

    public Editor getEditor() {
        Editor editor = Graphene.createPageFragment(Editor.class, root);

        return editor;
    }

    public void verifyAttribute(CliClient cliClient, String address, String guiIdentifier,
                                String  modelIdentifier) {

        String value=null;
        String cmd = CliUtils.buildCommand(address, ":read-attribute", new String[]{
                "name=" + modelIdentifier,
                "value=" + value,
        });
    }
}
