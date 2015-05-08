package nl.hsac.fitnesse.fixture.slim.web.xebium;

import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;
import nl.hsac.fitnesse.fixture.slim.web.BrowserTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * BrowserTest with some extra methods to assist HsacSeleniumDriverFixture.
 */
public class HsacXebiumBrowserTest extends BrowserTest {

    public HsacXebiumBrowserTest() {
        waitMilliSecondAfterScroll(250);
    }

    public boolean clickByXPath(String xPath) {
        WebElement element = findByXPath(xPath);
        return clickElement(element);
    }

    public boolean waitForXPathVisible(final String xPath) {
        By by = By.xpath(xPath);
        return waitForVisible(by);
    }

    public boolean waitForVisible(final By by) {
        return waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                Boolean result = Boolean.FALSE;
                WebElement element = getSeleniumHelper().findElement(by);
                if (element != null) {
                    scrollIfNotOnScreen(element);
                    result = element.isDisplayed();
                }
                return result;
            }
        });
    }

    public SlimFixtureException asSlimFixtureException(Throwable t) {
        return  (SlimFixtureException) handleException(null, null, t);
    }
}
