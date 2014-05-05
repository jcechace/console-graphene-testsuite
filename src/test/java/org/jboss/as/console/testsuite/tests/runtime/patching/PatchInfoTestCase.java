package org.jboss.as.console.testsuite.tests.runtime.patching;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.as.console.testsuite.fragments.shared.tables.InfoTable;
import org.jboss.as.console.testsuite.pages.runtime.PatchManagementPage;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.qa.management.cli.PatchManager;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by mvelas on 15.4.2014.
 */
public class PatchInfoTestCase extends PatchTestCaseAbstract {

    private static final Logger log = LoggerFactory.getLogger(PatchInfoTestCase.class);

    final String oneOffPatchName = "dummy-oneoff-patch";
    File oneOffPatchSimple = initPatchFile("console-test-oneoff-patch.zip.template", oneOffPatchName);

    @Before
    public void before() {
        Graphene.goTo(PatchManagementPage.class);
        Console.withBrowser(browser).waitUntilLoaded();
    }

    @Test
    @InSequence(0)
    public void verifyInfoNoPatchInstalled() {
        Assume.assumeTrue("Tested EAP has already some patch installed.", !patchCliManager.isAnyPatchInstalled());
        InfoTable infoTable = patchManagementPage.getInfoTable();
        String patchId = infoTable.get("Latest Applied Patch");
        Assert.assertEquals("No patch installed but webconsole presents some.", "", patchId);
    }

    @Test
    @InSequence(1)
    public void verifyPatchInfo() {

        try {
            prepareAndApplyPatchViaCli(basicPatchFile, BASIC_PATCH_NAME);
            verifyLastPatchInfo(BASIC_PATCH_NAME);
            prepareAndApplyPatchViaCli(cumulativePatchFile, CUMULATIVE_PATCH_NAME);
            verifyLastPatchInfo(CUMULATIVE_PATCH_NAME);
            prepareAndApplyPatchViaCli(oneOffPatchSimple, oneOffPatchName);
            verifyLastPatchInfo(oneOffPatchName);
        } finally {
            removePatchViaCliUsingRollback(oneOffPatchName);
            removePatchViaCliUsingRollback(CUMULATIVE_PATCH_NAME);
            removePatchViaCliUsingRollback(BASIC_PATCH_NAME);
        }
    }


    @Test
    @InSequence(2)
    public void verifyOnlyOneOffPatchInfo() {
        try {
            prepareAndApplyPatchViaCli(oneOffPatchSimple, oneOffPatchName);
            verifyLastPatchInfo(oneOffPatchName);
        } finally {
            removePatchViaCliUsingRollback(oneOffPatchName);
        }
    }

    void verifyLastPatchInfo(String patchId) {
        log.info("Verification of the most recently installed patch information");

        // expected (from CLI):
        PatchManager.PatchInfo patchInfo = patchCliManager.getPatchInfo();
        PatchManager.PatchHistoryEntry mostRecentPatch = patchCliManager.getHistoryEntry(patchId);

        // found in Management Console:
        InfoTable infoTable = patchManagementPage.getInfoTable();

        Assert.assertEquals("Displayed latest patch ID doesn't match the most recently installed ones.", patchId, infoTable.get("Latest Applied Patch"));
        Assert.assertEquals("Invalid version of the latest patch.", patchInfo.getVersion(), infoTable.get("Version"));
        Assert.assertEquals("Invalid date of the latest patch.", mostRecentPatch.getAppliedAt(), infoTable.get("Date Applied"));
        Assert.assertEquals("Invalid type of the latest patch.", mostRecentPatch.getType(), infoTable.get("Type"));
    }
}
