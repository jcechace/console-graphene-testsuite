package org.jboss.as.console.testsuite.fragments;

import org.jboss.as.console.testsuite.util.Console;
import org.jboss.as.console.testsuite.util.PropUtils;

/**
 * Represents notification area with messages.
 * @author jbliznak@redhat.com
 */
public class NotificationCenterFragment extends BaseFragment {

    public static final String CLASS_ROOT_DIV = PropUtils.get("notificationarea.class");

    /**
     * @return visible text of this element
     */
    public String getText() {
        return root.getText();
    }

    /**
     * @return count of messages displayed in this element
     */
    public int getMessagesCount() {
        String[] split = getText().split(" ");
        return Integer.valueOf(split[split.length - 1]);
    }

    /**
     * Open popup window with messages by clicking on this element
     * @return opened popup window with messages
     */
    public MessagesListFragment openMessagesWindow() {
        root.click();
        Console console = Console.withBrowser(browser);
        MessagesListFragment popup = console.openedPopup(MessagesListFragment.class);
        return popup;
    }
}
