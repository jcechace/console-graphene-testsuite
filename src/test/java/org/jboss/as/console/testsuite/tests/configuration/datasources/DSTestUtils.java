package org.jboss.as.console.testsuite.tests.configuration.datasources;

import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.DSUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author jcechace
 */
public class DSTestUtils {
    public static String createDatasource(CliClient cliClient, String url, boolean xa) {
        return createDatasource(cliClient, null, url, xa);
    }

    public static String createDatasource(CliClient cliClient, String name, String url, boolean xa) {
        if (name == null) {
            name = RandomStringUtils.randomAlphanumeric(5);
        }

        if (xa) {   // create XA datasource
            Map<String, String> props =  Collections.singletonMap("URL", url);
            DSUtils.addXaDS(cliClient, name, "java:/xa-datasources/" + name, "h2",
                    null, null, props);
            DSUtils.enableXaDS(cliClient, name);
        } else {    // create regular datasource
            DSUtils.addDS(cliClient, name, "java:/datasources/" + name, "h2", url);
            DSUtils.enableDS(cliClient, name);
        }

        return name;
    }

    public static void removeDatasource(CliClient cliClient, String name, boolean xa) {
        if (xa) {   // remove XA datasource
            DSUtils.removeXaDS(cliClient, name);
        } else {    // remove regular detasource
            DSUtils.removeDS(cliClient, name);
        }
    }

    public static void assertNotExists(CliClient cliClient, String name, boolean xa) {
        boolean result;
        if (xa) {   // XA datasource
            result = DSUtils.isXaDsDefined(cliClient, name);
        } else {    // regular datasource
            result = DSUtils.isDsDefined(cliClient, name);
        }

        Assert.assertFalse("Datasource should not exist", result);
    }
}
