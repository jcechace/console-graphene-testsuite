package org.jboss.as.console.testsuite.pages;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.jboss.as.console.testsuite.fragments.NavigationFragment;
import org.jboss.as.console.testsuite.fragments.shared.layout.Footer;
import org.jboss.as.console.testsuite.fragments.shared.layout.HeaderTabs;
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


    public void clickButton(String label) {
        By selector = ByJQuery.selector("button#" + label + ", button:contains('" + label + "')");
        WebElement button = getContentRoot().findElement(selector);
        button.click();
    }

    public void switchTab(String identifier) {
        String idSelector = ".gwt-TabLayoutPanelTab[id$='" + identifier + "']";
        String labelSelector = ".gwt-TabLayoutPanelTab:contains('" + identifier + "')";
        By selector = ByJQuery.selector(idSelector + ", " + labelSelector);

        WebElement tab = browser.findElement(selector);

        tab.click();

        String idSelectorSelected = ".gwt-TabLayoutPanelTab-selected[id$='" + identifier + "']";
        String labelSelectorSelected = ".gwt-TabLayoutPanelTab-selected:contains('" + identifier + "')";

        By selectorCurrent = ByJQuery.selector(idSelectorSelected + ", " + labelSelectorSelected);

        Graphene.waitAjax().until().element(selectorCurrent).is().present();

    }

    public WebElement getContentRoot() {
        String cssClass = "gwt-TabLayoutPanelContent";
        String cssClass2 = "split-center";
        By selector = ByJQuery.selector("." + cssClass + ":visible, ." + cssClass2);
        WebElement contentRoot = browser.findElement(selector);

        return contentRoot;
    }
}
