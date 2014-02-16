import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import pages.runtime.RuntimePage;

/**
 * @author jcechace
 */
@RunWith(Arquillian.class)
public class FooTest {
    @Drone
    private WebDriver browser;


    @Test
    public void fooTest(@InitialPage RuntimePage runtimePage) {
        runtimePage.getHeaderNavigation().goToRuntime();
        runtimePage.getServerNavigation().goToDeployments();
        System.out.println("aaa");
    }

}
