package fragments.navigation;

import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;

import static util.PropUtils.getHeaderNavId;

/**
 * Created by jcechace on 15/02/14.
 */
public class HeaderNavigationFragment {
    @Root
    private GrapheneElement root;

    public GrapheneElement getRoot() {
        return root;
    }

    public GrapheneElement getLinkByLabel(String label) {
        ByJQuery selector = ByJQuery.selector("*[#role='menuitem']:contains('" + label + "')");
        GrapheneElement link = root.findElement(selector);
        return link;
    }

    public GrapheneElement getLink(String id) {
        By selector = By.id(id);
        GrapheneElement link = root.findElement(selector);
        return link;
    }

    public void clickItemByLabel(String label) {
        GrapheneElement link = getLinkByLabel(label);
        link.click();
    }

    public void clickItem(String id) {
        GrapheneElement link = getLink(id);
        link.click();
    }


    // Convenience methods

    public void goToConfiguration() {
        clickItem(getHeaderNavId("config"));
    }

    public void goToRuntime() {
        clickItem(getHeaderNavId("runtime"));
    }

    public void goToAdministration() {
        clickItem(getHeaderNavId("admin"));
    }
}
