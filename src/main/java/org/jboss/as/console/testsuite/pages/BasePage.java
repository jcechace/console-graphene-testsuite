package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.shared.layout.Footer;
import org.jboss.as.console.testsuite.fragments.shared.layout.HeaderTabs;
import org.jboss.as.console.testsuite.fragments.PopUpFragment;
import org.jboss.as.console.testsuite.fragments.NavigationFragment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created by jcechace on 15/02/14.
 */
public abstract class BasePage {

    @Drone
    protected WebDriver browser;

    @FindByJQuery("#header-links-section")
    private HeaderTabs headerNavigation;

    @FindByJQuery("#main-content-area div[role='navigation'] .gwt-Tree")
    private NavigationFragment navigation;

    @FindBy(className = "footer-panel")
    private Footer footer;

    public HeaderTabs getHeaderNavigation() {
        return headerNavigation;
    }

    public NavigationFragment getNavigation() {
        return navigation;
    }

    public Footer getFooter() {
        return footer;
    }

    public PopUpFragment getPopup() {
        WebElement popupRoot =  browser.findElement(PopUpFragment.ROOT_SELECTOR);
        PopUpFragment popup = Graphene.createPageFragment(PopUpFragment.class, popupRoot);

        return popup;
    }

    public void clickButton(String label) {
        By selector = ByJQuery.selector("button#" + label + ", button:contains('" + label + ")");
        WebElement button = browser.findElement(selector);
        button.click();
    }
}
