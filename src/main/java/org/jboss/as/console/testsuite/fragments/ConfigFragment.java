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

/**
 * Created by jcechace on 01/03/14.
 */
public class ConfigFragment extends BaseFragment {

    public ResourceManager getResourceManager() {
        return getResourceManager(null, null);
    }

    public ResourceManager getResourceManager(String dmrPath, CliClient cliClient) {
        ResourceManager rm = Graphene.createPageFragment(ResourceManager.class, root);
        rm.setDmrPath(dmrPath);
        rm.setCliClient(cliClient);

        return rm;
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
     * Calls  {@link #save()}  save} method and asserts the output
     *
     * @param expected <code>true</code>if wizard is expected to finish, <code>false</code> otherwise
     */
    public void saveAndAssert(boolean expected) {
        boolean finished = this.save();

        if (expected) {
            Assert.assertTrue("Config was supposed to be saved successfully, read view should be active.", finished);
        } else {
            Assert.assertFalse("Config wasn't supposed to be saved, read-write view should be active.", finished);
        }
    }

    public void textAttribute(ResourceManager rm, String name, String value, boolean save) {
        textAttribute(rm, name, value, value, save);
    }

    public void textAttribute(ResourceManager rm, String name, String value, String expectedValue, boolean save) {
        Editor editor = this.edit();
        editor.text(name, value);
        this.saveAndAssert(save);

        rm.verifyAttribute(name, expectedValue);
    }

    public void selectAttribute(ResourceManager rm, String name, String value, boolean save) {
        selectAttribute(rm, name, value, value, save);
    }

    public void selectAttribute(ResourceManager rm, String name, String value, String expectedValue, boolean save) {
        Editor editor = this.edit();
        editor.select(name, value);
        this.saveAndAssert(save);

        rm.verifyAttribute(name, expectedValue);
    }

    public void checkboxAttribute(ResourceManager rm, String name, boolean value, boolean save) {
        checkboxAttribute(rm, name, value, value, save);
    }

    public void checkboxAttribute(ResourceManager rm, String name, boolean value, boolean expectedValue, boolean save) {
        Editor editor = this.edit();
        editor.checkbox(name, value);
        this.saveAndAssert(save);

        rm.verifyAttribute(name, String.valueOf(expectedValue));
    }
}
