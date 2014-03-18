package org.jboss.as.console.testsuite.fragments.shared.modals;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

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

        // TODO: possible refactoring to dynamic wait
        Graphene.waitModel().withTimeout(2, TimeUnit.SECONDS);
    }

    public void finish() {
        String label = PropUtils.get("modals.wizard.finish.label");
        clickButton(label);

        // TODO: possible refactoring to dynamic wait
        Graphene.waitModel().withTimeout(2, TimeUnit.SECONDS);
    }


}
