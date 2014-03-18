package org.jboss.as.console.testsuite.fragments;

import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.jboss.as.console.testsuite.util.PropUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Represents messages popup window which is opened by clicking on notification center area.
 * <p/>
 * @author jbliznak@redhat.com
 */
public class MessagesListFragment extends PopUpFragment {

    public static final String CLASS_MSG_DIV = PropUtils.get("messagelist.list.class");
    public static final String CLEAR_BUTTON_LABEL = PropUtils.get("messagelist.clear.label");

    /**
     * @return total count of displayed messages
     */
    public int getCount() {
        return getMessageTables().size();
    }

    /**
     * @return messages entries
     */
    public List<MessageListEntryFragment> getMessages() {
        List<MessageListEntryFragment> msgs = new ArrayList<MessageListEntryFragment>();
        for (WebElement table : getMessageTables()) {
            msgs.add(Graphene.createPageFragment(MessageListEntryFragment.class, table));
        }
        return msgs;
    }

    /**
     * Clear popup list by clicking on Clear button
     */
    public void clear() {
        clickLinkByLabel(CLEAR_BUTTON_LABEL);
    }

    private List<WebElement> getMessageTables() {
        return root
                .findElement(By.className(CLASS_MSG_DIV))
                .findElements(ByJQuery.selector("div:first div table"));
    }
}
