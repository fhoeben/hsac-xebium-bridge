package nl.hsac.fitnesse.fixture.slim.web.xebium;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import nl.hsac.fitnesse.fixture.Environment;
import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;
import nl.hsac.fitnesse.fixture.util.SeleniumHelper;
import nl.hsac.fitnesse.slim.interaction.ExceptionHelper;
import nl.hsac.fitnesse.slim.interaction.InteractionAwareFixture;

import org.openqa.selenium.WebDriver;

import fitnesse.slim.fixtureInteraction.FixtureInteraction;

/**
 * Our subclass of Xebium to allow combination with HSAC fixtures and support the 'storyboard' Slim table.
 * This will use the driver configured using SeleniumDriverSetup, instead of creating its own.
 * Furthermore any exceptions thrown will be processed to attempt to add a screenshot.
 */
public class HsacBasicSeleniumDriverFixture extends com.xebia.incubator.xebium.SeleniumDriverFixture implements InteractionAwareFixture {
    protected final Environment environment = Environment.getInstance();
    protected final HsacXebiumBrowserTest browserTest;

    /**
     * Creates new.
     * @param browserTest browser test instance that will be used to use HSAC features.
     */
    public HsacBasicSeleniumDriverFixture(HsacXebiumBrowserTest browserTest) {
        this.browserTest = browserTest;
        setTimeoutToSeconds(getSeleniumHelper().getDefaultTimeoutSeconds());
        try {
            String baseDir = environment.getFitNesseRootDir() + "/files/screenshots/xebium/";
            saveScreenshotAfterInFolder("assertion", baseDir.replace("/", File.separator));
        } catch (IOException e) {
            throw new SlimFixtureException("Error configuring default screenshot directory for Xebium", e);
        }
    }

    @Override
    public void startBrowserOnUrl(String browser, String browserUrl) {
        cleanBrowserOnUrl(browserUrl);
        // we use the driver configured in SuiteSetUp
        WebDriver driver = getSeleniumHelper().driver();
        startDriverOnUrl(driver, browserUrl);
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