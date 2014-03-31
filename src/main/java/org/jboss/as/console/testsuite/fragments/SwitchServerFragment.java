package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvelas on 11.3.2014.
 *
 * Class implements pop-up fragment for switching host/server context in domain mode
 */
public class SwitchServerFragment extends PopUpFragment {
    final static public ByJQuery BUTTON_SELECTOR = ByJQuery.selector(PropUtils.get("topology.server.switch.button"));

    /**
     * @param newContext new context (server/host) to set
     */
    public void changeContext(String newContext) {
        ByJQuery serverSelector = ByJQuery.selector("div.cellListEvenItem:contains('" + newContext + "')");
        root.findElement(serverSelector).click();
    }

    private List<String> getCurrentContext() {
        ByJQuery serverSelector = ByJQuery.selector("div.cellListSelectedItem");
        List<String> context = new ArrayList<String>();
        for(WebElement elem : root.findElements(serverSelector)) {
            context.add(elem.getText().trim());
        }
        return context;
    }

    public String getCurrentHostName() {
        return getCurrentContext().get(0);
    }

    public String getCurrentServerName() {
        return getCurrentContext().get(1);
    }
}
