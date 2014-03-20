package org.jboss.as.console.testsuite.pages.runtime;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.as.console.testsuite.fragments.PatchErrorPanelFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
public class PatchingWizardWindow extends WizardWindow {
    public PatchErrorPanelFragment getErrorPanel() {
        return Graphene.createPageFragment(PatchErrorPanelFragment.class, root.findElement(PatchErrorPanelFragment.SELECTOR));
    }
}
