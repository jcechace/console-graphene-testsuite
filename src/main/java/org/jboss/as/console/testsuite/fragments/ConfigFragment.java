package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.jboss.qa.management.cli.CliClient;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.Map;

/**
 * Created by jcechace on 01/03/14.
 */
public class ConfigFragment extends BaseFragment {

    private String dmrPath;

    public void setDmrPath(String dmrPath) {
        this.dmrPath = dmrPath;
    }

    public ResourceManager getResourceManager() {
        return Graphene.createPageFragment(ResourceManager.class, root);
    }

    public WebElement getEditButton() {
        By selector = ByJQuery.selector(
                "." + PropUtils.get("configarea.edit.button.class") + ":visible"
        );

        WebElement button = root.findElement(selector);
        return button;
    }

    /**
     * Clicks the edit button in config area and returns the editor
     *
     * @return Editor
     */
    public Editor edit() {
        WebElement button = getEditButton();
        button.click();

        Graphene.waitGui().until().element(button).is().not().visible();

        return getEditor();
    }

    /**
     * Click the save button in read-write (form) mode.
     *
     * @return True if configuration switched into read-only mode. False otherwise
     */
    public boolean save() {
        clickButton(PropUtils.get("configarea.save.button.label"));
        try {
            Graphene.waitModel().until().element(getEditButton()).is().visible();
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }

    /**
     * Click cancel and switch back to read-only mode.
     */
    public void cancel() {
        clickButton(PropUtils.get("configarea.save.button.label"));
        Graphene.waitModel().until().element(getEditButton()).is().visible();
    }

    /**
     * Verifies the value of attribute in model.
     * @param name name of the attribute. If the name is camelCase it will be converted to camel-case.
     * @param expectedValue expected value
     * @param cliClient cli client to use
     */
    public void verifyAttribute(String name, String expectedValue, CliClient cliClient) {
        String dmrName = camelToDash(name);
        String actualValue = cliClient.readAttribute(dmrPath, dmrName);

        Assert.assertEquals("Attribute value is different in model.", expectedValue, actualValue);
    }

    /**
     * Verify the value of attributes against model
     * @param pairs Key-Value map of attribute names and values. If name is camelCase it will be coverted to camel-case
     * @param cliClient cli client to use
     */
    public void verifyAttributes(Map<String, String> pairs, CliClient cliClient) {
        for (Map.Entry<String, String> p : pairs.entrySet()) {
            verifyAttribute(p.getKey(), p.getValue(), cliClient);
        }
    }

    /**
     * Converts a camelCaseString to camel-case-string
     *
     * @param input
     * @return
     */
    private static String camelToDash(String input) {
        return input.replaceAll("\\B([A-Z])", "-$1" ).toLowerCase();
    }
}
