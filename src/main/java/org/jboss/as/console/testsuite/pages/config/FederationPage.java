package org.jboss.as.console.testsuite.pages.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.config.federation.FederationConfigArea;
import org.jboss.as.console.testsuite.fragments.config.federation.IdentityProviderConfigArea;
import org.jboss.as.console.testsuite.pages.ConfigPage;

/**
 * @author jcechace
 */
@Location("#picletlink-federation")
public class FederationPage extends ConfigPage {
    @Override
    public FederationConfigArea getConfig() {
        return getConfig(FederationConfigArea.class);
    }

    public IdentityProviderConfigArea getIdpConfig() {
        return getConfig(IdentityProviderConfigArea.class);
    }
}
