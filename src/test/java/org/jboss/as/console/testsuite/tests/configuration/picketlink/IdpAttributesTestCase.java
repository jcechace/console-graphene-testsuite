package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
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
public class IdpAttributesTestCase extends ConfiguredIdpAbstract {

    private static final Logger log = LoggerFactory.getLogger(IdpAttributesTestCase.class);

    public static final String IDP_URL = "http://example.net/changed-idp/";

    @Before
    public void setup() {
        navigateToFederation();
        federationPage.switchToIdentityProvider();
    }

    @Test
    public void securityDomain() {
        ConfigFragment attrsConfig = getAttrsConfig();

        Editor editor = attrsConfig.edit();
        editor.select("securityDomain", "jboss-web-policy");

        saveAndVerify(attrsConfig, "securityDomain", "jboss-web-policy");
    }

    @Test
    public void url() {
        ConfigFragment attrsConfig = getAttrsConfig();

        Editor editor = attrsConfig.edit();
        editor.text("url", IDP_URL);

        saveAndVerify(attrsConfig, "url", IDP_URL);
    }

    @Test
    public void urlEmpty() {
        ConfigFragment attrsConfig = getAttrsConfig();

        Editor editor = attrsConfig.edit();
        editor.text("url", "");

        Assert.assertFalse("Config should not be saved.", attrsConfig.save());
    }

    private ConfigFragment getAttrsConfig() {
        ConfigFragment attrsConfig = federationPage.getIdpConfig().attrsConfig();
        return attrsConfig;
    }

    private void saveAndVerify(ConfigFragment attrsConfig, String name, String value) {
        Assert.assertTrue("Unable to save config", attrsConfig.save());

        ResourceManager rm = federationPage.getResourceManager();
        rm.setDmrPath(IDP_ADDR);
        rm.verifyAttribute(name, value, cliClient);
    }
}
