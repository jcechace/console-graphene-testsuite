package fragments.navigation;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by jcechace on 16/02/14.
 */
public class NavigationSectionFragment {
    @Drone
    private WebDriver browser;

    @Root
    private GrapheneElement root;

    public GrapheneElement getRoot() {
        return root;
    }

    /**
     *  Click link in navigation tree based on the value of token attribute
     *
     * @param token
     */
    public void goToItem(String token) {
        ByJQuery selector = ByJQuery.selector(".lhs-tree-item[token='" + token + "']");
        GrapheneElement item = root.findElement(selector);
        item.click();
    }

    /**
     *  CLick link in navigation tree based on its label
     *
     * @param label
     */
    public void goToItemByLabel(String label) {
        ByJQuery selector = ByJQuery.selector(".lhs-tree-item div[role='treeitem']:contains('" + label + "')");
        GrapheneElement item = root.findElement(selector);
        item.click();
    }


}
