package org.jboss.as.console.testsuite.fragments.config.patching;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
public class PatchingWizard extends WizardWindow {
    public PatchErrorPanelFragment getErrorPanel() {
        return Graphene.createPageFragment(PatchErrorPanelFragment.class, root.findElement(PatchErrorPanelFragment.SELECTOR));
    }
}
