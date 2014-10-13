package org.jboss.as.console.testsuite.fragments.config.federation;

import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * @author jcechace
 */
public class FederationConfigArea extends ConfigAreaFragment {
    public ConfigFragment samlConfig() {
        String label = PropUtils.get("config.federation.configarea.saml.tab.label");
        return switchTo(label, ConfigFragment.class);
    }
}
