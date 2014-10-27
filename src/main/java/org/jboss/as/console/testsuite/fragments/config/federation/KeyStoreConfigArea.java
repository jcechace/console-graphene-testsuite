package org.jboss.as.console.testsuite.fragments.config.federation;

import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

public class KeyStoreConfigArea extends ConfigAreaFragment {

    public ConfigFragment hostkeys() {
        String label = PropUtils.get("config.federation.ks.configarea.hostkeys.tab.label");
        return switchTo(label);
    }
}
