package org.jboss.as.console.testsuite.pages.config;

import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceConfigArea;
import org.jboss.as.console.testsuite.fragments.config.datasources.DatasourceWizard;
import org.jboss.as.console.testsuite.fragments.shared.modals.WizardWindow;
import org.jboss.as.console.testsuite.pages.ConfigPage;
import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * Created by jcechace on 22/02/14.
 */

@Location("#datasources")
public class DatasourcesPage extends ConfigPage {
    public void selectByName(String name) {
        getResourceTable().selectRowByText(0, name);
    }

    @Override
    public DatasourceConfigArea getConfig() {
        return getConfig(DatasourceConfigArea.class);
    }

    public DatasourceWizard addResource() {
        String label = PropUtils.get("config.shared.add.label");
        clickButton(label);

        DatasourceWizard wizard = Console.withBrowser(browser).openedWizard(DatasourceWizard.class);

        return wizard;
    }

}
