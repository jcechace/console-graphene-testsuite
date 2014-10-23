package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.config.federation.IdentityProviderConfigArea;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class IdpTrustedDomains extends ConfiguredIdpAbstract {

    private static final Logger log = LoggerFactory.getLogger(IdpTrustedDomains.class);

    public static final String DOMAIN = "example.com";
    public static final String DOMAIN_ADDR = IDP_ADDR + "/trust-domain=" + DOMAIN;
    public static final String DOMAIN_GUI = "example-gui.com";
    public static final String DOMAIN_ADDR_GUI = IDP_ADDR + "/trust-domain=" + DOMAIN_GUI;

    @Before
    public void setupTrustedDomain() {
        cliClient.executeCommand(DOMAIN_ADDR + ":add()");
        navigateToFederation();
        federationPage.switchToIdentityProvider();
    }

    @After
    public void tearDownTrustedDomain() {
        cliClient.executeCommand(DOMAIN_ADDR + ":remove()");
    }

    @Test
    public void removeDomain() {
        IdentityProviderConfigArea config = federationPage.getIdpConfig();
        ConfigFragment domainConfig = config.trustedDomainConfig();

        ResourceManager rm = domainConfig.getResourceManager();
        ConfirmationWindow confirmationWindow = rm.removeResource(DOMAIN);
        confirmationWindow.confirm();

        boolean exists = cliClient.executeForSuccess(DOMAIN_ADDR + ":read-resource()");
        Assert.assertFalse("Removed trusted domain should not exist", exists);
    }

    @Test
    public void addDomain() {
        IdentityProviderConfigArea config = federationPage.getIdpConfig();
        ConfigFragment domainConfig = config.trustedDomainConfig();

        ResourceManager rm = domainConfig.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.text("name", DOMAIN_GUI);

        wizard.assertFinish(true);

        boolean exists = cliClient.executeForSuccess(DOMAIN_ADDR_GUI + ":read-resource");
        Assert.assertTrue("Added domain should exist", exists);
    }


    @Test
    public void addDomainEmpty() {
        IdentityProviderConfigArea config = federationPage.getIdpConfig();
        ConfigFragment domainConfig = config.trustedDomainConfig();

        ResourceManager rm = domainConfig.getResourceManager();
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.text("name", "");

        wizard.assertFinish(false);

        wizard.cancel();
    }


}
