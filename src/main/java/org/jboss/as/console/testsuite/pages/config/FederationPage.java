package org.jboss.as.console.testsuite.pages.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.config.federation.FederationConfigArea;
import org.jboss.as.console.testsuite.fragments.config.federation.IdentityProviderConfigArea;
import org.jboss.as.console.testsuite.fragments.config.federation.KeyStoreConfigArea;
import org.jboss.as.console.testsuite.fragments.config.federation.ServiceProviderConfigArea;
import org.jboss.as.console.testsuite.pages.ConfigPage;
import org.jboss.as.console.testsuite.util.PropUtils;

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

    public ServiceProviderConfigArea getSpConfig() {
        return getConfig(ServiceProviderConfigArea.class);
    }

    public KeyStoreConfigArea getKsConfig() {
        return getConfig(KeyStoreConfigArea.class);
    }

    public void switchToIdentityProvider() {
        String label = PropUtils.get("config.federation.idp.view.label");
        switchView(label);
    }

    public void switchToServiceProvider() {
        String label = PropUtils.get("config.federation.sp.view.label");
        switchView(label);
    }

    public void switchToKeyStore() {
        String label = PropUtils.get("config.federation.ks.view.label");
        switchView(label);
    }
}
