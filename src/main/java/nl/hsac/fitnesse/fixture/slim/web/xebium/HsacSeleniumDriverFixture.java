package nl.hsac.fitnesse.fixture.slim.web.xebium;

import fitnesse.slim.fixtureInteraction.FixtureInteraction;
import nl.hsac.fitnesse.fixture.Environment;
import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;
import nl.hsac.fitnesse.fixture.util.SeleniumHelper;
import nl.hsac.fitnesse.slim.interaction.ExceptionHelper;
import nl.hsac.fitnesse.slim.interaction.InteractionAwareFixture;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Our subclass of Xebium to allow combination with HSAC fixtures.
 */
public class HsacSeleniumDriverFixture extends com.xebia.incubator.xebium.SeleniumDriverFixture
        implements InteractionAwareFixture {
    private final Environment environment = Environment.getInstance();
    private final HsacXebiumBrowserTest browserTest;

    public HsacSeleniumDriverFixture() {
        this(new HsacXebiumBrowserTest());
    }

    public HsacSeleniumDriverFixture(HsacXebiumBrowserTest browserTest) {
        this.browserTest = browserTest;
        setTimeoutToSeconds(getSeleniumHelper().getDefaultTimeoutSeconds());
        try {
            String baseDir = environment.getFitNesseRootDir() + "/files/screenshots/xebium/";
            saveScreenshotAfterInFolder("assertion", baseDir.replace("/", File.separator));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startBrowserOnUrl(String browser, String browserUrl) {
        cleanBrowserOnUrl(browserUrl);
        // we use the driver configured in SuiteSetUp
        WebDriver driver = getSeleniumHelper().driver();
        this.startDriverOnUrl(driver, browserUrl);
    }

    @Deprecated
    @Override
    public void startBrowserOnUrlUsingRemoteServerOnHostOnPort(final String browser, final String browserUrl, final String serverHost, final int serverPort) {
        startBrowserOnUrl(browser, browserUrl);
    }

    protected void cleanBrowserOnUrl(String browserUrl) {
        HsacXebiumBrowserTest browserTest = this.browserTest;
        browserTest.open(browserUrl);
        cleanBrowser();
        browserTest.refresh();
    }

    protected void cleanBrowser() {
        HsacXebiumBrowserTest browserTest = this.browserTest;
        browserTest.deleteAllCookies();
        browserTest.clearLocalStorage();
    }

    @Override
    public void stopBrowser() {
        cleanBrowser();
        // stopping browser is done in SuiteTearDown
    }

    @Override
    public void setTimeoutTo(long timeout) {
        getBrowserTest().secondsBeforeTimeout((int) (timeout / 1000));
        super.setTimeoutTo(timeout);
    }

    /*
     * To support 'storyboard' tables.
     * @param baseName base name for screenshot
     * @return html image
     */
    public String takeScreenshot(String baseName) {
        return getBrowserTest().takeScreenshot(baseName);
    }

    @Override
    public boolean doOn(String command, String target) {
        boolean result;
        if ("waitForVisible".equals(command)) {
            result = ensureTargetVisible(target);
        } else if ("click".equals(command)
                    || "clickAndWait".equals(command)) {
            By by = targetToBy(target);
            WebElement element = getSeleniumHelper().findElement(by);
            return getBrowserTest().clickElement(element);
        } else {
            if (!command.contains("Not")) {
                ensureTargetVisible(target);
            }
            result = super.doOn(command, target);
        }
        return result;
    }

    @Override
    public boolean doOnWith(String command, String target, String value) {
        if (!command.contains("Not")) {
            ensureTargetVisible(target);
        }
        return super.doOnWith(command, target, value);
    }

    protected boolean ensureTargetVisible(String target) {
        // we can not act on elements that are not visible
        By by = targetToBy(target);
        return getBrowserTest().waitForVisible(by);
    }

    protected By targetToBy(String target) {
        By result;
        if (target.startsWith("/")) {
            result = By.xpath(target);
        } else if (target.startsWith("id=")) {
            result = By.id(target.substring(3));
        } else if (target.startsWith("css=")) {
            result = By.cssSelector(target.substring(4));
        } else if (target.startsWith("name=")) {
            result = By.name(target.substring(5));
        } else if (target.startsWith("xpath=")) {
            result = By.xpath(target.substring(6));
        } else {
            throw new SlimFixtureException(false, "Unable to convert target to Selenium By: " + target);
        }
        return result;
    }

    @Override
    public String isOn(String command, String target) {
        String result = super.isOn(command, target);
        if (result != null && result.startsWith("Execution of command failed:")) {
            try {
                String screenshotHtml = takeScreenshot("isOn");
                String msg = String.format("<div>%s. Page content:%s</div>", result, screenshotHtml);
                throw new SlimFixtureException(false, msg);
            } catch (SlimFixtureException e) {
                throw e;
            } catch (Exception e) {
                // ignore
            }
        }

        return result;
    }

    protected SeleniumHelper getSeleniumHelper() {
        return environment.getSeleniumHelper();
    }

    public HsacXebiumBrowserTest getBrowserTest() {
        return browserTest;
    }

    @Override
    public Object aroundSlimInvoke(FixtureInteraction interaction, Method method, Object... arguments) {
        try {
            return interaction.methodInvoke(method, this, arguments);
        } catch (Throwable t) {
            Throwable realEx = ExceptionHelper.stripReflectionException(t);
            throw getBrowserTest().asSlimFixtureException(realEx);
        }
    }
}
