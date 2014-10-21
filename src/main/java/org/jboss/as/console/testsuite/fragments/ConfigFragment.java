package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 01/03/14.
 */
public class ConfigFragment extends BaseFragment {

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

}
