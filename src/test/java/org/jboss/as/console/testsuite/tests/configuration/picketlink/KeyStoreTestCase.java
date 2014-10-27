package org.jboss.as.console.testsuite.tests.configuration.picketlink;

import java.io.File;
import java.io.IOException;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.fragments.ConfigFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.util.ResourceManager;
import org.jboss.as.console.testsuite.pages.config.FederationPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.util.formeditor.Editor;
import org.jboss.qa.management.cli.CliClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
@Category(SharedTest.class)
public class KeyStoreTestCase extends ConfiguredFederationAbstract {

    public static final String KEYSTORE_ADDR = FEDERATION_ADDR + "/key-store=key-store";
    public static final String KEYSTORE_URL = "url";
    public static final String KEYSTORE_URL_ATTR = "file";
    public static final String KEYSTORE_PASS = "passwd";
    public static final String KEYSTORE_PASS_ATTR = "password";
    public static final String KEYSTORE_PASS_VALUE = "passwd_value";
    public static final String KEYSTORE_SIGNED_ALIAS = "signKeyAlias";
    public static final String KEYSTORE_SIGNED_ALIAS_ATTR = "sign-key-alias";
    public static final String KEYSTORE_SIGNED_ALIAS_VALUE = "signKeyAlias_value";
    public static final String KEYSTORE_SIGNED_PASS = "signKeyPasswd";
    public static final String KEYSTORE_SIGNED_PASS_ATTR = "sign-key-password";
    public static final String KEYSTORE_SIGNED_PASS_VALUE = "signKeyPasswd_value";
    public static final String HOSTKEY_NAME = "name";
    public static final String HOSTKEY_NAME_VALUE = "secret_key";
    public static final String HOSTKEY_ADDR = KEYSTORE_ADDR + "/key=" + HOSTKEY_NAME_VALUE;
    public static final String HOSTKEY_HOST = "host";
    public static final String HOSTKEY_HOST_VALUE = "localhost";
    public static final String EMPTY = "";

    private static final Logger log = LoggerFactory.getLogger(KeyStoreTestCase.class);
    private static CliClient cliClient = CliProvider.getClient();

    @Drone
    private WebDriver browser;
    @Page
    private FederationPage federationPage;

    @Before
    public void setup() {
        refreshFederation();
    }

    @After
    public void tearDown() {
        cliClient.executeCommand(KEYSTORE_ADDR + ":remove()");
    }

    @Test
    public void addKeyStoreTest() throws IOException {
        String ksPath = createFakeKeyStore();
        ResourceManager rm = federationPage.getResourceManager(KEYSTORE_ADDR, cliClient);
        WizardWindow wizard = rm.addResource();

        Editor editor = wizard.getEditor();
        editor.text(KEYSTORE_URL, ksPath);
        editor.password(KEYSTORE_PASS, KEYSTORE_PASS_VALUE);
        editor.text(KEYSTORE_SIGNED_ALIAS, KEYSTORE_SIGNED_ALIAS_VALUE);
        editor.password(KEYSTORE_SIGNED_PASS, KEYSTORE_SIGNED_PASS_VALUE);
        wizard.assertFinish(true);

        rm.verifyResource(true);
        
        rm.verifyAttribute(KEYSTORE_URL_ATTR, ksPath);
        rm.verifyAttribute(KEYSTORE_PASS_ATTR, KEYSTORE_PASS_VALUE);
        rm.verifyAttribute(KEYSTORE_SIGNED_ALIAS_ATTR, KEYSTORE_SIGNED_ALIAS_VALUE);
        rm.verifyAttribute(KEYSTORE_SIGNED_PASS_ATTR, KEYSTORE_SIGNED_PASS_VALUE);
    }

    @Test
    public void removeKeyStoreTest() throws IOException {
        String ksPath = addKeyStoreAndRefresh();
        ResourceManager rm = federationPage.getResourceManager(KEYSTORE_ADDR, cliClient);
        rm.removeResourceAndConfirm(ksPath);

        rm.verifyResource(false);
    }

    @Test
    public void addHostKeyTest() throws Exception {
        addKeyStoreAndRefresh();
        ConfigFragment config = federationPage.getKsConfig().hostkeys();
        ResourceManager rm = config.getResourceManager(HOSTKEY_ADDR, cliClient);
        WizardWindow wizard = rm.addResource();
        Editor editor = wizard.getEditor();

        editor.text(HOSTKEY_NAME, EMPTY);
        editor.text(HOSTKEY_HOST, HOSTKEY_HOST_VALUE);
        wizard.assertFinish(false);

        editor.text(HOSTKEY_NAME, HOSTKEY_NAME_VALUE);
        editor.text(HOSTKEY_HOST, EMPTY);
        wizard.assertFinish(false);

        editor.text(HOSTKEY_NAME, HOSTKEY_NAME_VALUE);
        editor.text(HOSTKEY_HOST, HOSTKEY_HOST_VALUE);
        wizard.assertFinish(true);
        
        rm.verifyResource(true);
        rm.verifyAttribute(HOSTKEY_HOST, HOSTKEY_HOST_VALUE);
    }

    @Test
    public void removeHostKeyTest() throws Exception {
        addKeyStore();
        cliClient.executeCommand(HOSTKEY_ADDR + ":add(host=" + HOSTKEY_HOST_VALUE + ")");
        refreshFederation();
        ConfigFragment config = federationPage.getKsConfig().hostkeys();
        ResourceManager rm = config.getResourceManager(HOSTKEY_ADDR, cliClient);
        rm.removeResourceAndConfirm(HOSTKEY_NAME_VALUE);

        rm.verifyResource(false);
    }
    private String createFakeKeyStore() throws IOException {
        File ks = File.createTempFile("fakeKS_", null);
        ks.deleteOnExit();
        return ks.getAbsolutePath();
    }

    private String addKeyStoreAndRefresh() throws IOException {
        String ksPath = addKeyStore();
        refreshFederation();
        return ksPath;
    }

    private void refreshFederation() {
        navigateToFederation();
        federationPage.switchToKeyStore();
    }

    private String addKeyStore() throws IOException {
        String ksPath = createFakeKeyStore();
        cliClient.executeCommand(KEYSTORE_ADDR + ":add(file=" + ksPath
                + ",password=pass,sign-key-alias=ska,sign-key-password=skpass)");
        return ksPath;
    }
}
