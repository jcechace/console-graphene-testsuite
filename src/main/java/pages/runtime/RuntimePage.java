package pages.runtime;

import fragments.navigation.runtime.PlatformSectionFragment;
import fragments.navigation.runtime.ServerSectionFragment;
import org.jboss.arquillian.graphene.page.Location;
import pages.BasePage;
import util.PropUtils;

/**
 * @author jcechace
 */
@Location("http://localhost:9990/console/App.html")
public class RuntimePage extends BasePage {

    public ServerSectionFragment getServerNavigation() {
       return getNavigation().getSection(PropUtils.getNavSectionId("runtime", "server"), ServerSectionFragment.class);
    }

    public PlatformSectionFragment getPlatformNavigation() {
        return getNavigation()
                .getSection(PropUtils.getNavSectionId("runtime", "server"), PlatformSectionFragment.class);
    }


}
