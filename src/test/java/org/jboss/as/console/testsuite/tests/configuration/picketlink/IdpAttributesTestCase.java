package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
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
public class IdpAttributesTestCase extends IdpConfigAbstract {

    private static final Logger log = LoggerFactory.getLogger(IdpAttributesTestCase.class);

    @Before
    public void setup() {
        super.setup();
        setupNavigate();
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
        editor.text("url", "http://example.net/idp");

        saveAndVerify(attrsConfig, "url", "http://example.net/idp");
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
        attrsConfig.cancel();       // TODO: remove once issue is resolved  (read-write mode by default)

        return attrsConfig;
    }

    private void saveAndVerify(ConfigFragment attrsConfig, String name, String value) {
        Assert.assertTrue("Unable to save config", attrsConfig.save());

        attrsConfig.setDmrPath(IDP_ADDR);
        attrsConfig.verifyAttribute(name, value, cliClient);
    }
}
