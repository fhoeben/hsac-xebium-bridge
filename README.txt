This projects allows the use of HSAC's FitNesse fixtures (https://github.com/fhoeben/hsac-fitnesse-fixtures) and Xebia's Xebium (http://xebia.github.io/Xebium/) together in a single FitNesse installation. Both fixture classes will connect to the same browser instance using Selenium.

It is mostly targeted to allow organisation's having made an investment in tests using Xebium to migrate to HSAC's fixtures, which we believe will allow them to have a more understandable and maintainable test set.

The setup for this bridge is as follows:
Create a Selenium webdriver is set up using HSAC's SeleniumDriverSetup fixture (in a SuiteSetUp page) and
use either nl.hsac.fitnesse.fixture.slim.web.xebium.HsacSeleniumDriverFixture or nl.hsac.fitnesse.fixture.slim.web.xebium.HsacBasicSeleniumDriverFixture instead of com.xebia.incubator.xebium.SeleniumDriverFixture (which will also (like BrowserTest) use the Selenium browser created in the SuiteSetUp).

Features of HsacBasicSeleniumDriverFixture:
- Allows a build server run to control the Selenium setup as per usual for HSAC's fixtures (see https://github.com/fhoeben/hsac-fitnesse-fixtures/wiki/3.1.-Build-Server-Selenium-Configuration).
- Screenshots taken using Xebium's standard screenshot feature are stored in 'files/screenshots/xebium' which is included in FitNesse's results for a run by a build server (i.e. target/fitnesse-results).
- HsacSeleniumDriverFixture also supports taking of screenshots (so can be used in 'storyboard' tables, and supports |show|take screenshot|myPage|).
- On any exception thrown by HsacSeleniumDriverFixture an attempt is made to include an screenshot.

On top of the features above HsacSeleniumDriverFixture adds:
- When 'isOn' returns 'Execution of command failed' this is transformed to an exception containing a screenshot.
- 'doOn' and 'doOnWith' with a target based on id, css, link or xPath now first wait (for at most the specified timeout) until that target is visible, and will throw an exception when it is not.
- 'click' and 'clickAndWait' selecting using xPath will use BrowserTest's click, which ensures the element is visible (possibly first scrolling it into view).
- 'waitForVisible' (with an xPath target) throws an exception containing screenshot if the element is not visible after the maximum timeout.


Please note:
When 'startBrowserOnUrl' is invoked no new Selenium WebDriver session is created, but it opens the specified URL, after deleting the existing localStorage and cookies for that site.
'stopBrowser' does not end the WebDriver session (that must be done, like starting it, via nl.hsac.fitnesse.fixture.slim.web.SeleniumDriverSetup), it only clears the cookies and localStorage of the current site.
To use have screenshots be included with every exception it is important to configure the 'Slim Interaction' used (by adding '!define slim.flags {-i nl.hsac.fitnesse.slim.interaction.InterceptingInteraction}' to the root page of your Slim tests).
