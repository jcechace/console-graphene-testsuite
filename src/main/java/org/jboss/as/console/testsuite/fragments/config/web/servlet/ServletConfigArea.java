package org.jboss.as.console.testsuite.fragments.config.web.servlet;

import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

public class ServletConfigArea extends ConfigAreaFragment {

    public ConfigFragment global() {
        String label = PropUtils.get("config.web.servlet.configarea.global.tab.label");
        return switchTo(label);
    }
}
