package nl.hsac.fitnesse.fixture.slim.web.xebium;

import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;
import nl.hsac.fitnesse.fixture.slim.web.BrowserTest;

/**
 * BrowserTest with some extra methods to assist HsacSeleniumDriverFixture.
 */
public class HsacXebiumBrowserTest extends BrowserTest {
    public SlimFixtureException asSlimFixtureException(Throwable t) {
        return  (SlimFixtureException) handleException(null, null, t);
    }
}
