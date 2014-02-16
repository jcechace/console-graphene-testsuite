package fragments.navigation;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.GrapheneElement;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;

/**
 * Created by jcechace on 15/02/14.
 */
public class NavigationFragment {

    @Root
    private GrapheneElement root;

    public GrapheneElement getRoot() {
        return root;
    }

    public <T extends NavigationSectionFragment> T getSectionBy(By selector, Class<T> clazz) {
        GrapheneElement sectionRoot = root.findElement(selector);
        T section = Graphene.createPageFragment(clazz, sectionRoot);
        return section;
    }

    public <T extends NavigationSectionFragment> T  getSectionByLabel(String label, Class<T> clazz) {
        By selector = By.xpath(".//*[@role='treeitem' and contains(@text, '" + label + "')]/" +
                "ancestor::*[contains(@class, 'tree-section')]");
       return getSectionBy(selector, clazz);
    }

    public NavigationSectionFragment getSectionByLabel(String label) {
        return getSectionByLabel(label, NavigationSectionFragment.class);
    }

    public <T extends NavigationSectionFragment> T  getSection(String id, Class<T> clazz) {
        By selector = By.id(id);
        return getSectionBy(selector, clazz);
    }

    public NavigationSectionFragment getSection(String id) {
        return getSection(id, NavigationSectionFragment.class);
    }
}
