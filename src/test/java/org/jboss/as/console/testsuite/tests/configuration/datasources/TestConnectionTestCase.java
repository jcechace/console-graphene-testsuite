package org.jboss.as.console.testsuite.tests.configuration.datasources;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.config.DatasourcesPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * Created by jcechace on 21/02/14.
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class TestConnectionTestCase extends AbstractTestConnectionTestCase{

    private static final Logger log = LoggerFactory.getLogger(TestConnectionTestCase.class);


    private static String dsNameValid;
    private static String xaDsNameValid;
    private static String dsNameInvalid;
    private static String xaDsNameInvalid;

    private static final String VALID_URL = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1";
    private static final String INVALID_URL = "invalidUrl";


    // Setup

    @BeforeClass
    public static void setup() {  // create needed datasources
        cliClient = CliProvider.getClient();

        dsNameValid = DSTestUtils.createDatasource(cliClient, VALID_URL, false);
        dsNameInvalid = DSTestUtils.createDatasource(cliClient, INVALID_URL, false);
        xaDsNameInvalid = DSTestUtils.createDatasource(cliClient, INVALID_URL, true);
        xaDsNameValid = DSTestUtils.createDatasource(cliClient, VALID_URL, true);;
    }

    @AfterClass
    public static void tearDown() { // remove datasources when finished
        DSTestUtils.removeDatasource(cliClient,  dsNameInvalid, false);
        DSTestUtils.removeDatasource(cliClient, xaDsNameValid, true);
        DSTestUtils.removeDatasource(cliClient, xaDsNameInvalid, true);
    }

    @Before
    public void before() {
        Graphene.goTo(DatasourcesPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @After
    public void afer() {
        browser.navigate().refresh();
    }

    // Regular DS tests
    @Test
    public void validDatasource() {
        testConnection(dsNameValid, true);
    }

    @Test
    public void invalidDatasource() {
        testConnection(dsNameInvalid, false);
    }

    @Test
    public void validInWizard() {
        String name = RandomStringUtils.randomAlphabetic(6);
        testConnectionInWizard(name, VALID_URL, true);
    }

    @Test
    public void invalidInWizard() {
        String name = RandomStringUtils.randomAlphabetic(6);
        testConnectionInWizard(name, INVALID_URL, false);
    }


    // XA DS tests
    @Test
    public void validXADatasource() {
        datasourcesPage.switchTab("XA Datasources");

        String name = createValidXADatasource();
        testConnection(name, true);
    }


    private String createInvalidDatasource() {
        String name = RandomStringUtils.randomAlphanumeric(5);
        log.debug(name);
        return "testds";
    }

    private void removeInvalidDatasource(String name, boolean xa) {

        datasourcesPage.switchToXA();
        testConnection(xaDsNameValid, true);
    }

    @Test
    public void invalidXADatasource() {
        datasourcesPage.switchToXA();
        testConnection(xaDsNameInvalid, false
        );
    }

    @Test
    public void validXAInWizard() {
        datasourcesPage.switchToXA();

        String name = RandomStringUtils.randomAlphabetic(6);
        testXAConnectionInWizard(name, VALID_URL, true);
    }

    @Test
    public void invalidXAInWizard() {
        datasourcesPage.switchToXA();

        String name = RandomStringUtils.randomAlphabetic(6);
        testXAConnectionInWizard(name, "invalidUrl", false);
    }
}
