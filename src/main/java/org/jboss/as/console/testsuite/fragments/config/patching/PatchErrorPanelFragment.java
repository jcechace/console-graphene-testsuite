package org.jboss.as.console.testsuite.fragments.config.patching;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;

/**
 * Represents panel with error message in patch wizard.
 * @author jbliznak@redhat.com
 */
public class PatchErrorPanelFragment extends BaseFragment {

    /**
     * Selector for this fragment from WizardFragment root
     */
    public static final By SELECTOR =
            ByJQuery.selector("." + PropUtils.get("runtime.patching.wizard.error.fragment.class") + ":visible");
    private static final By DETAILS_SELECTOR =
            By.className(PropUtils.get("runtime.patching.wizard.error.details.class"));
    private static final By DETAILS_DISPLAYED_SELECTOR =
            ByJQuery.selector("." + PropUtils.get("runtime.patching.wizard.error.details.class")
                              + "." + PropUtils.get("runtime.patching.wizard.error.details.opened.class"));
    private static final By DETAILS_HEADER_SELECTOR =
            By.className(PropUtils.get("runtime.patching.wizard.error.hideshowdetails.class"));
    private static final By ERROR_PANEL_SELECTOR =
            By.className(PropUtils.get("runtime.patching.wizard.error.message.class"));

    /**
     * Show error details
     */
    public void showDetails() {
        if (isDetailsHidden()) {
            root.findElement(DETAILS_HEADER_SELECTOR).click();
        }
    }

    /**
     * Hide error details
     */
    public void hideDetails() {
        if (!isDetailsHidden()) {
            root.findElement(DETAILS_HEADER_SELECTOR).click();
        }
    }

    /**
     * @return whether error details are hidden
     */
    public boolean isDetailsHidden() {
        return root.findElements(DETAILS_DISPLAYED_SELECTOR).isEmpty();
    }

    /**
     * @return Error panel title
     */
    public String getPanelTitle() {
        return root.findElement(By.tagName("h3")).getText();
    }

    /**
     * @return visible error message
     */
    public String getErrorMessage() {
        return root.findElement(ERROR_PANEL_SELECTOR).getText();
    }

    /**
     * @return inner HTML content of error details node
     */
    public String getErrorDetails() {
        return root.findElement(DETAILS_SELECTOR)
                .findElement(By.tagName("pre")).getAttribute("innerHTML");
    }

    /**
     * @return error details content as plain text (what you see)
     */
    public String getErrorDetailsAsPlainText() {
        return root.findElement(DETAILS_SELECTOR)
                .findElement(By.tagName("pre")).getText();
    }
}
