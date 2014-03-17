package org.jboss.as.console.testsuite.pages.runtime;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.pages.ConfigPage;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@Location("#patching")
public class PatchManagementPage extends ConfigPage {

    public void selectByName(String name) {
        getResourceTable().selectRowByText(0, name);
    }

    public WizardWindow applyNewPatch() {
        return addResource(WizardWindow.class, PropUtils.get("runtime.patching.apply.label"));
    }

    public WizardWindow rollbackPatch(String patchId) {
        selectByName(patchId);
        return addResource(WizardWindow.class, PropUtils.get("runtime.patching.rollback.label"));
    }

}
