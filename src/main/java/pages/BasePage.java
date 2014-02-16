package pages;

import fragments.navigation.HeaderNavigationFragment;
import fragments.navigation.NavigationFragment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.openqa.selenium.WebDriver;

/**
 * Created by jcechace on 15/02/14.
 */
public abstract class BasePage {

    @Drone
    private WebDriver browser;

    @FindByJQuery("#header-links-section")
    private HeaderNavigationFragment headerNavigation;

    @FindByJQuery("#main-content-area div[role='navigation'] .gwt-Tree")
    private NavigationFragment navigation;

    public HeaderNavigationFragment getHeaderNavigation() {
        return headerNavigation;
    }

    public NavigationFragment getNavigation() {
        return navigation;
    }
}
