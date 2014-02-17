package org.jboss.as.console.testsuite.fragments;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by jcechace on 19/02/14.
 */
public class BaseFragment {
    @Drone
    protected WebDriver browser;

    @Root
    protected WebElement root;

    public WebElement getRoot() {
        return root;
    }
}
