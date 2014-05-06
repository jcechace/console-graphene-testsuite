package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.tests.categories.SharedTest;
import org.jboss.as.console.testsuite.tests.categories.StandaloneTest;
import org.jboss.as.console.testsuite.tests.util.CliProvider;
import org.jboss.as.console.testsuite.tests.util.ConfigUtils;
import org.jboss.qa.management.cli.CliClient;
import org.jboss.qa.management.cli.PatchManager;
import org.jboss.qa.management.common.*;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rhatlapa (rhatlapa@redhat.com)
 */
@RunWith(Arquillian.class)
@Category(SharedTest.class)
public abstract class PatchTestCaseAbstract {

    private static final Logger log = LoggerFactory.getLogger(PatchTestCaseAbstract.class);

    public static final String BASIC_PATCH_ZIP_TEMPLATE_NAME = "console-test-patch-basic.zip.template";
    public static final String CUMULATIVE_PATCH_ZIP_TEMPLATE_NAME = "console-test-patch-cumulated.zip.template";
    public static final String PATCHES_ROOT_ELEMENT = "<patches xmlns=\"urn:jboss:patch:bundle:1.0\">\n";
    public static final String BASIC_PATCH_NAME = "basic-console-test-patch"; // simple cumulative patch
    public static final String CUMULATIVE_PATCH_NAME = "cumulative-console-test-patch"; // patch dependent on BASIC_PATCH_NAME

    protected File basicPatchFile;
    protected File cumulativePatchFile;
    protected CliClient cliClient = CliProvider.getClient();
    protected ServerManager serverManager = ServerManager.getInstance(cliClient);
    protected PatchManager patchCliManager = new PatchManager(cliClient, true);

    @Drone
    protected WebDriver browser;

    @Page
    protected PatchManagementPage patchManagementPage;


    {
        basicPatchFile = initPatchFile(PatchTestCaseAbstract.BASIC_PATCH_ZIP_TEMPLATE_NAME, BASIC_PATCH_NAME);
        cumulativePatchFile = initPatchFile(PatchTestCaseAbstract.CUMULATIVE_PATCH_ZIP_TEMPLATE_NAME, CUMULATIVE_PATCH_NAME);
    }

    public File initPatchFile(String patchFileTemplateName, String patchId) {
        String pathToPatchTemplate = getClass().getResource("/patching/"+patchFileTemplateName).getPath();
        String serverVersion = serverManager.getVersion();
        return createPatchZipFromTemplate(new File(pathToPatchTemplate), serverVersion, serverVersion, patchId);
    }


    public void prepareAndApplyPatchViaCli(File patchFile, String patchName) {
        patchCliManager.apply(patchFile.getAbsolutePath());
        log.debug("patch {} applied", patchName);
        Assert.assertTrue("Patch wasn't successfully applied via CLI",
                patchCliManager.isPatchInstalled(patchName));
        browser.navigate().refresh();
    }

    public void removePatchViaCliUsingRollback(String patchName) {
        patchCliManager.rollback(patchName, false, true);
    }


    public static File createPatchZipFromTemplate(File srcPatchZip, String origPatchServerVersion,
                                                  String newPatchServerVersion, String patchName) {
        try {
            File newPatchZip = File.createTempFile("newPatch", ".zip");
            newPatchZip.delete();
            newPatchZip.deleteOnExit();
            FileUtils.copyFile(srcPatchZip, newPatchZip);
            File patchXml = File.createTempFile("patch", ".xml");
            patchXml.delete();
            if (!ZipUtils.extractOneFileFromZip(newPatchZip, "patch.xml", patchXml)) {
                throw new RuntimeException("patch.xml not found in " + newPatchZip.getAbsolutePath());
            }
            Map<String, String> substitutions = new HashMap<String, String>();
            substitutions.put("%EAP_VERSION_ORIG%", origPatchServerVersion);
            substitutions.put("%EAP_VERSION_NEW%", newPatchServerVersion);
            substitutions.put("%PATCH_NAME%", patchName);
            Library.replaceStringsInFile(substitutions, patchXml);
            ZipUtils.updateOneFileInZip(newPatchZip, patchXml, "patch.xml");
            patchXml.delete();
            return newPatchZip;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
            return null;
        }
    }


    public void assertRestartDoneByConsole(boolean expectServerAsRestarted) {
        if (expectServerAsRestarted) {
            serverManager.waitUntilAvailable();
            Assert.assertFalse("Restart is required by the server, should be already done by console",
                    cliClient.restartRequired());
        } else {
            Assert.assertTrue("Restart shouldn't be done by console => should be still required by server",
                    cliClient.restartRequired());
        }
    }

    public String generatePatchesXml(File[] patches) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Unable to create document builder");
        }

        //root elements
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("patches");
        rootElement.setAttribute("xmlns", "urn:jboss:patch:bundle:1.0");
        for (int i = 0; i < patches.length; i++) {
            Element patchElement = doc.createElement("element");
            patchElement.setAttribute("id", "patch_" + String.valueOf(i));
            patchElement.setAttribute("path", patches[i].getName());
            rootElement.appendChild(patchElement);
        }
        doc.appendChild(rootElement);
        String patchesXml = XmlConverter.xmlToString(doc);
        log.debug("Generated patches.xml: {}", patchesXml);
        return patchesXml;
    }

    public File createBundlePatch(File[] patches) {
        File bundlePatch;
        try {
            bundlePatch = File.createTempFile("bundle-patch", ".zip");
            bundlePatch.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create tmp file for bungle patch");
        }
        JavaArchive zip = ShrinkWrap.create(JavaArchive.class, bundlePatch.getName());
        for (File patchFile : patches) {
            zip.add(new FileAsset(patchFile), patchFile.getName());
        }

        zip.add(new StringAsset(generatePatchesXml(patches)), "patches.xml");
        zip.as(ZipExporter.class).exportTo(bundlePatch, true);
        return bundlePatch;
    }


}
