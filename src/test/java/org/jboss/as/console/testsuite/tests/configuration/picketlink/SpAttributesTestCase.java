package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jcechace
 */
public class SpAttributesTestCase extends ConfiguredSpAbstract {

    private static final Logger log = LoggerFactory.getLogger(SpAttributesTestCase.class);

    public static final String DEFAULT_ERROR_PAGE = "/error.jsp";
    public static final String DEFAULT_LOUGOUT_PAGE = "/logout.jsp";

    @Before
    public void setup() {
        navigateToFederation();
        federationPage.switchToServiceProvider();
    }

    @Test
    public void securityDomain() {
        ResourceManager rm = federationPage.getResourceManager(SP_ADDR, cliClient);
        ConfigFragment attrsConfig = this.getAttrsConfig();

        attrsConfig.selectAttribute(rm, "securityDomain", "other", true);
    }

    @Test
    public void url() {
        ResourceManager rm = federationPage.getResourceManager(SP_ADDR, cliClient);
        ConfigFragment attrsConfig = this.getAttrsConfig();

        attrsConfig.textAttribute(rm, "url", "foo", true);
        attrsConfig.textAttribute(rm, "url", "", "foo", false);

    }

    @Test
    public void errorPage() {
        ResourceManager rm = federationPage.getResourceManager(SP_ADDR, cliClient);
        ConfigFragment attrsConfig = this.getAttrsConfig();

        attrsConfig.textAttribute(rm, "errorPage", "myerror.jsp", true);
        attrsConfig.textAttribute(rm, "errorPage", "", DEFAULT_ERROR_PAGE, true);
    }

    @Test
    public void logoutPage() {
        ResourceManager rm = federationPage.getResourceManager(SP_ADDR, cliClient);
        ConfigFragment attrsConfig = this.getAttrsConfig();

        attrsConfig.textAttribute(rm, "logoutPage", "mylogout.jsp", true);
        attrsConfig.textAttribute(rm, "logoutPage",  "", DEFAULT_LOUGOUT_PAGE, true);
    }

    @Test
    public void httpPostBinding() {
        ResourceManager rm = federationPage.getResourceManager(SP_ADDR, cliClient);
        ConfigFragment attrsConfig = this.getAttrsConfig();

        attrsConfig.checkboxAttribute(rm, "postBinding", true, true);
        attrsConfig.checkboxAttribute(rm, "postBinding", false, true);
    }

    @Test
    public void strictPostBinding() {
        ResourceManager rm = federationPage.getResourceManager(SP_ADDR, cliClient);
        ConfigFragment attrsConfig = this.getAttrsConfig();

        attrsConfig.checkboxAttribute(rm, "strictPostBinding", true, true);
        attrsConfig.checkboxAttribute(rm, "strictPostBinding", false, true);
    }


    private ConfigFragment getAttrsConfig() {
        ConfigFragment attrsConfig = federationPage.getSpConfig().attrsConfig();
        return attrsConfig;
    }

}
