package org.jboss.as.console.testsuite.fragments.shared.util;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.fragments.WindowFragment;
import org.jboss.as.console.testsuite.fragments.shared.modals.ConfirmationWindow;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.fragments.shared.tables.ResourceTableFragment;
import org.jboss.as.console.testsuite.fragments.shared.tables.ResourceTableRowFragment;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.jboss.qa.management.cli.CliClient;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Map;

/**
 * @author jcechace
 */
public class ResourceManager extends BaseFragment {

    private String dmrPath;

    public void setDmrPath(String dmrPath) {
        this.dmrPath = dmrPath;
    }

    public ResourceTableFragment getResourceTable() {
        By selector = ByJQuery.selector("#master_detail-master:visible");
        WebElement tableRoot = getRoot().findElement(selector);
        ResourceTableFragment table = Graphene.createPageFragment(ResourceTableFragment.class, tableRoot);

        return table;
    }

    /**
     * Select resource based on its name in firt column of resource table.
     *
     * @param name Name of the resource.
     */
    public ResourceTableRowFragment selectByName(String name) {
        return getResourceTable().selectRowByText(0, name);
    }

    public <T extends WizardWindow> T addResource(Class<T> clazz, String label) {
        clickButton(label);

        T wizard = Console.withBrowser(browser).openedWizard(clazz);

        return wizard;
    }

    public <T extends WizardWindow> T addResource(Class<T> clazz) {
        String label = PropUtils.get("config.shared.add.label");
        return addResource(clazz, label);
    }

    public WizardWindow addResource() {
        return addResource(WizardWindow.class);
    }

    public <T extends WindowFragment> T removeResource(String name, Class<T> clazz) {
        selectByName(name);
        String label = PropUtils.get("config.shared.remove.label");
        clickButton(label);

        T window = Console.withBrowser(browser).openedWindow(clazz);

        return window;
    }

    public ConfirmationWindow removeResource(String name) {
        return removeResource(name, ConfirmationWindow.class);
    }

    /**
     * Select resource based on its name in first column of resource table and then
     * click on view option
     *
     * @param name Name of the resource.
     */
    public void viewByName(String name) {
        ResourceTableRowFragment row = selectByName(name);
        row.view();
    }


    /**
     * Verifies the value of attribute in model.
     * @param name name of the attribute. If the name is camelCase it will be converted to camel-case.
     * @param expectedValue expected value
     * @param cliClient cli client to use
     */
    public void verifyAttribute(String name, String expectedValue, CliClient cliClient) {
        String dmrName = camelToDash(name);
        String actualValue = cliClient.readAttribute(dmrPath, dmrName);

        Assert.assertEquals("Attribute value is different in model.", expectedValue, actualValue);
    }

    /**
     * Verify the value of attributes against model
     * @param pairs Key-Value map of attribute names and values. If name is camelCase it will be coverted to camel-case
     * @param cliClient cli client to use
     */
    public void verifyAttributes(Map<String, String> pairs, CliClient cliClient) {
        for (Map.Entry<String, String> p : pairs.entrySet()) {
            verifyAttribute(p.getKey(), p.getValue(), cliClient);
        }
    }

    /**
     * Converts a camelCaseString to camel-case-string
     *
     * @param input
     * @return
     */
    private static String camelToDash(String input) {
        return input.replaceAll("\\B([A-Z])", "-$1" ).toLowerCase();
    }
}
