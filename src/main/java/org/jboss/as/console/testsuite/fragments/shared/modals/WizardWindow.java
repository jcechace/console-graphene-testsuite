package org.jboss.as.console.testsuite.fragments.shared.modals;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

/**
 * @author jcechace
 */
public class WizardWindow extends WindowFragment {

    public void next() {
        String label = PropUtils.get("modals.wizard.next.label");
        clickButton(label);

        // TODO: possible refactoring to dynamic wait
        Graphene.waitModel().withTimeout(2, TimeUnit.SECONDS);
    }

    public void finish() {
        String label = PropUtils.get("modals.wizard.finish.label");
        clickButton(label);

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
