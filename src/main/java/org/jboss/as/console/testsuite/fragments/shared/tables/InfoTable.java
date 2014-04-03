package org.jboss.as.console.testsuite.fragments.shared.tables;

import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.fragments.BaseFragment;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author jcechace
 */
public class InfoTable extends BaseFragment {
    private static String KEY_CLASS = PropUtils.get("infotable.key.class");

    public String get(String key) {
        By keySelector = ByJQuery.selector("." + KEY_CLASS + ":contains('" + key + "')");
        WebElement keyCell = root.findElement(keySelector);

        WebElement valCell = keyCell.findElement(By.xpath("./following::td"));

        return valCell.getText();
    }
}
