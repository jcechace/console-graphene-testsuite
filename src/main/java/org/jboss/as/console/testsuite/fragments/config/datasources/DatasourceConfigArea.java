package org.jboss.as.console.testsuite.fragments.config.datasources;

import org.jboss.as.console.testsuite.fragments.ConfigAreaFragment;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * @author jcechace
 */
public class DatasourceConfigArea extends ConfigAreaFragment {
    public ConnectionConfig connectionConfig() {
        String label = PropUtils.get("config.datasources.configarea.connection.tab.label");
        return switchTo(label, ConnectionConfig.class);
    }
}
