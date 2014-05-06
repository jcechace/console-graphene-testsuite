package org.jboss.as.console.testsuite.tests.util;

import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.DomainCliClient;

/**
 * @author jcechace
 */
public class CliProvider {

    private CliProvider() {

    }

    public  static CliClient withProfile(String profile) {
        return new DomainCliClient(profile);
    }

    public static CliClient getClient() {
        CliClient cliClient = null;
        if (ConfigUtils.isDomain()) {
            String profile = ConfigUtils.getDefaultProfile();
            String host =   ConfigUtils.getDefaultHost();
            cliClient = new DomainCliClient(profile, host);
        } else {
            cliClient = new CliClient();
        }

        return cliClient;
    }

}
