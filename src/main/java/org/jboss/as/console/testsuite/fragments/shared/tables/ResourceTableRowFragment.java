package org.jboss.as.console.testsuite.fragments.shared.tables;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResourceTableRowFragment extends BaseFragment {

    public String getCellValue(int index) {
        WebElement cell = getCell(index);

        return cell.getText();
    }

    public void click() {
        getCell(0).click();
    }

    public WebElement getCell(int index) {
        By selector = By.className("cellTableCell");
        List<WebElement> cells = root.findElements(selector);

        return cells.get(index);
    }

    /**
     * clicks on the view option in this row
     */
    public void view() {
        WebElement link = null;
        try {
            By selector = ByJQuery.selector("." + PropUtils.get("resourcepager.viewlink.class"));
            link = root.findElement(selector);
        } catch (NoSuchElementException e) {
            String label =  PropUtils.get("resourcepager.view.label");
            String cssClass = PropUtils.get("resourcepager.textlink.class");
            By selector = ByJQuery.selector("." + cssClass +  ":contains('" + label + "')");
            link = root.findElement(selector);
        }

        link.click();

        Graphene.waitGui().withTimeout(1500, TimeUnit.MILLISECONDS);
    }
}
