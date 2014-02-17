package org.jboss.as.console.testsuite.fragments.shared.modals;

import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * Created by jcechace on 21/02/14.
 */
public class ConfirmationWindow extends WindowFragment {
    public void confirm() {
        String label = PropUtils.get("modals.confirmation.confirm.label");
        clickButton(label, true);

        waitUntilClosed();
    }
}
