package nl.hsac.fitnesse.fixture.slim.web.xebium;

import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;
import nl.hsac.fitnesse.fixture.slim.web.BrowserTest;
import org.openqa.selenium.By;

/**
 * BrowserTest with some extra methods to assist HsacSeleniumDriverFixture.
 */
public class HsacXebiumBrowserTest extends BrowserTest {
    // override to make publicly visible
    @Override
    public boolean waitForVisible(final By by) {
        return super.waitForVisible(by);
    }

    // override to make publicly visible
    public SlimFixtureException asSlimFixtureException(Throwable t) {
        return  (SlimFixtureException) handleException(null, null, t);
    }
}
