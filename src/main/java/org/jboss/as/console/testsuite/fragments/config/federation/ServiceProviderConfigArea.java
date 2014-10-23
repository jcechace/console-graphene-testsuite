package org.jboss.as.console.testsuite.fragments.config.federation;

import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * @author jcechace
 */
public class ServiceProviderConfigArea extends ConfigAreaFragment {
    public ConfigFragment attrsConfig() {
        String label = PropUtils.get("config.federation.sp.configarea.attrs.tab.label");
        return switchTo(label, ConfigFragment.class);
    }
}
