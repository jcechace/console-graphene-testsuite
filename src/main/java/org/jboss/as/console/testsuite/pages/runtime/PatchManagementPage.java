package org.jboss.as.console.testsuite.pages.runtime;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.config.patching.PatchingWizard;
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

    public PatchingWizard applyNewPatch() {
        return addResource(PatchingWizard.class, PropUtils.get("runtime.patching.apply.label"));
    }

    public PatchingWizard rollbackPatch(String patchId) {
        selectByName(patchId);
        return addResource(PatchingWizard.class, PropUtils.get("runtime.patching.rollback.label"));
    }

}
