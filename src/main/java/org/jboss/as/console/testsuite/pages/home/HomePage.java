package org.jboss.as.console.testsuite.pages.home;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Location;
import org.jboss.as.console.testsuite.fragments.homepage.HomepageSectionFragment;
import org.jboss.as.console.testsuite.pages.BasePage;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jcechace
 */
@Location("#home")
public class HomePage extends BasePage {
    public HomepageSectionFragment getSection(String identifier) {
        List<HomepageSectionFragment> sections = getAllSections();
        for (HomepageSectionFragment section : sections) {
            String id = section.getHeader().getAttribute("id").toLowerCase();
            if (id.endsWith(identifier.toLowerCase())) {
                return section;
            }
        }
        throw new NoSuchElementException("Unable to found section with identifier: " + identifier);
    }


    public List<HomepageSectionFragment> getAllSections() {
        String headerClass = PropUtils.get("homepage.section.class");
        By selector = By.className(headerClass);

        List<WebElement> elements = browser.findElements(selector);
        List<HomepageSectionFragment> sections = new ArrayList<HomepageSectionFragment>();

        for (WebElement element : elements) {
            sections.add(Graphene.createPageFragment(HomepageSectionFragment.class, element));
        }

        return sections;
    }


    public void getSideBar() {

    }

}
