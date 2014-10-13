package org.jboss.as.console.testsuite.fragments.shared.modals;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author jcechace
 */
public class WizardWindow extends WindowFragment {

    private static final Logger log = LoggerFactory.getLogger(WizardWindow.class);

    public void next() {
        String label = PropUtils.get("modals.wizard.next.label");
        clickButton(label);

        // TODO: possible refactoring to dynamic wait
        Graphene.waitModel().withTimeout(2, TimeUnit.SECONDS);
    }

    /**
     * Clicks either Finish or Save button and wait's unit the wizard window is closed
     */
    public void finish() {
        String label = PropUtils.get("modals.wizard.finish.label");
        try {
            clickButton(label);
        } catch (WebDriverException e) {
            log.debug("Button with label \"" + label + "\" not found");
            label = PropUtils.get("modals.window.save.label");
            clickButton(label);
        }

        Graphene.waitGui().withTimeout(30, TimeUnit.SECONDS).until().element(root).is().not().present();

        closed = true;
    }

    /**
     *  Waits until operation is finished (spinner circle is hidden)
     */
    public void waitUntilFinished() {
        By selector = By.className(PropUtils.get("modals.window.spinner.class"));
        Graphene.waitGui().withTimeout(1200, TimeUnit.MILLISECONDS);
        Graphene.waitModel().until().element(root, selector).is().not().visible();
    }
}
