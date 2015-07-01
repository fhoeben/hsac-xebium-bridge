package nl.hsac.fitnesse.fixture.slim.web.xebium;

import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;
import nl.hsac.fitnesse.fixture.slim.web.BrowserTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * BrowserTest with some extra methods to assist HsacSeleniumDriverFixture.
 */
public class HsacXebiumBrowserTest extends BrowserTest {
    public SlimFixtureException asSlimFixtureException(Throwable t) {
        return  (SlimFixtureException) handleException(null, null, t);
    }

    @Override
    public boolean click(final String place) {
        // since this method is invoked by HsacSeleniumDriverFixture, and not just Slim
        // using the WaitUntl annotion does not suffice. A call which will always be done
        // is needed.
        return waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return HsacXebiumBrowserTest.super.click(place);
            }
        });
    }

    @Override
    public boolean waitForVisible(final String place) {
        // since this method is invoked by HsacSeleniumDriverFixture, and not just Slim
        // using the WaitUntl annotion does not suffice. A call which will always be done
        // is needed.
        return waitUntilOrStop(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return HsacXebiumBrowserTest.super.waitForVisible(place);
            }
        });
    }
}
