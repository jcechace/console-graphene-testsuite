package org.jboss.as.console.testsuite.tests.configuration.datasources;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.tests.categories.DomainTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.common.DomainManagementCliUtils;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

/**
 * @author jcechace
 */
@Category(DomainTest.class)
@RunWith(Arquillian.class)
public class DomainTestConnectionTestCase extends AbstractTestConnectionTestCase {

    private static String dsNameValid;
    private static String dsSameNameValid;
    private static String dsSameNameInvalid;

    private static CliClient fullHaClient;

    private static Map<String, List<String>> domainState;


    private static final String VALID_URL = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1";
    private static final String INVALID_URL = "invalidUrl";


    // Setup

    @BeforeClass
    public static void setup() {  // create needed datasources
        cliClient = CliProvider.getClient();
        fullHaClient = CliProvider.withProfile("full-ha");

        domainState = DomainManagementCliUtils.listRunningServers(cliClient);

        dsNameValid = DSTestUtils.createDatasource(cliClient, VALID_URL, false);
        dsSameNameValid = DSTestUtils.createDatasource(fullHaClient, VALID_URL, false);
        dsSameNameInvalid = DSTestUtils.createDatasource(cliClient, dsSameNameValid,
                INVALID_URL, false);

    }

    @AfterClass
    public static void tearDown() { // remove datasources when finished
        DSTestUtils.removeDatasource(cliClient, dsNameValid, false);
        DSTestUtils.removeDatasource(fullHaClient, dsSameNameValid, false);
        DSTestUtils.removeDatasource(cliClient, dsSameNameInvalid, false);

        DomainManagementCliUtils.restoreDomainState(cliClient, domainState, true);
    }

    @Before
    public void before() {
        Graphene.goTo(DatasourcesPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
        datasourcesPage.pickProfile(ConfigUtils.getDefaultProfile());
    }

    @After
    public void afer() {
        browser.navigate().refresh();
    }

    @Test
    public void testValidWithNoRunningServer() {
        DomainManagementCliUtils.stopAllServers(cliClient, 10l);
        testConnection(dsNameValid, false);
    }

    @Test
    public void testInvalidWithSameName() {
        DomainManagementCliUtils.startAllServers(cliClient, 10l);

        testConnection(dsSameNameValid, false);
    }

    @Test
    public void testValidWithSameNameInOtherGroup() {
        DomainManagementCliUtils.startAllServers(cliClient, 10l);
        datasourcesPage.pickProfile("full-ha");

        testConnection(dsSameNameValid, true);
    }
}
