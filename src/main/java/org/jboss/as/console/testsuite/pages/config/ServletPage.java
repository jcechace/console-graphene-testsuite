package org.jboss.as.console.testsuite.pages.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.config.web.servlet.ServletConfigArea;
import org.jboss.as.console.testsuite.pages.ConfigPage;

@Location("#web")
public class ServletPage extends ConfigPage {

    public ServletConfigArea getConfig() {
        return getConfig(ServletConfigArea.class);
    }

}
