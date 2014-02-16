package fragments.navigation.runtime;

import fragments.navigation.NavigationSectionFragment;
import util.PropUtils;

/**
 * Created by jcechace on 16/02/14.
 */
public class ServerSectionFragment extends NavigationSectionFragment {

    private String getNavPropKey(String itemKey) {
        return "navigation.runtime.server." + itemKey + ".token";
    }
    public void goToOverview() {
        String key = getNavPropKey("overview");
        goToItem(PropUtils.get(key));
    }

    public void goToDeployments() {
        String key = getNavPropKey("deployments");
        goToItem(PropUtils.get(key));
    }
}
