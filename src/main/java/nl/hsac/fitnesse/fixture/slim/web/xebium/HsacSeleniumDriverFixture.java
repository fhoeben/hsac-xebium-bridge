package nl.hsac.fitnesse.fixture.slim.web.xebium;

import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.Keys;
import java.util.HashMap;
import java.util.Map;

/**
 * Extension on basic Xebium wrapper, augmenting some of the commands.
 */
public class HsacSeleniumDriverFixture extends HsacBasicSeleniumDriverFixture {

    /**
     * Creates new.
     */
    public HsacSeleniumDriverFixture() {
        super();
        initSpecialKeysMapping();
    }

    /**
     * Creates new.
     * @param browserTest browser test instance that will be used to use HSAC features.
     */
    public HsacSeleniumDriverFixture(HsacXebiumBrowserTest browserTest) {
        super(browserTest);
        initSpecialKeysMapping();
    }

    @Override
    public boolean doOn(String command, String target) {
        target = unalias(target);
        boolean result;
        if ("waitForVisible".equals(command)) {
            result = ensureTargetVisible(target);
        } else if ("click".equals(command)
                    || "clickAndWait".equals(command)) {
            By by = targetToBy(target);
            WebElement element = getSeleniumHelper().findElement(by);
            return getBrowserTest().clickElement(element);
        } else {
            ensureReadyForDo(command, target);
            result = super.doOn(command, target);
        }
        return result;
    }

    @Override
    public boolean doOnWith(String command, String target, String value) {
        target = unalias(target);
        value = unalias(value);
        ensureReadyForDo(command, target);
        return super.doOnWith(command, target, value);
    }

    protected void ensureReadyForDo(String command, String target) {
        target = unalias(target);
        if (!"verifyTextPresent".equals(command)
                && !"open".equals(command)
                && !"openAndWait".equals(command)
                && !command.contains("Not")) {
            try {
                ensureTargetVisible(target);
            } catch (UnrecognizedXebiumTarget e) {
                // ignore and just try to use super's behavior
            }
        }
    }

    protected boolean ensureTargetVisible(String target) {
        target = unalias (target);
        // we can not act on elements that are not visible
        By by = targetToBy(target);
        return getBrowserTest().waitForVisible(by);
    }

    protected By targetToBy(String target) {
        target = unalias (target);
        By result;
        if (target.startsWith("/")) {
            result = By.xpath(target);
        } else if (target.startsWith("id=")) {
            result = By.id(target.substring(3));
        } else if (target.startsWith("css=")) {
            result = By.cssSelector(target.substring(4));
        } else if (target.startsWith("name=")) {
            result = By.name(target.substring(5));
        } else if (target.startsWith("link=")) {
            result = By.linkText(target.substring(5));
        } else if (target.startsWith("xpath=")) {
            result = By.xpath(target.substring(6));
        } else {
            throw new UnrecognizedXebiumTarget("Unable to convert target to Selenium By: " + target);
        }
        return result;
    }

    @Override
    public String isOn(String command, String target) {
        target = unalias (target);
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

    private static final String ALIAS_PREFIX = "%";
    private Map<String, String> aliases = new HashMap<String, String>();

    public void addAliasForLocator(String alias, String locator) {
        aliases.put(alias, locator);
    }

    public void clearAliases() {
        aliases.clear();
    }

    private String unalias(String value) {
        String result = value;
        if (value != null && value.startsWith(ALIAS_PREFIX)) {
            String alias = value.substring(ALIAS_PREFIX.length());
            String subst = aliases.get(alias);
            if (subst != null) {
                result = subst;
            }
        }
        return result;
    }

    private void initSpecialKeysMapping() {
        aliases.put("$KEY_BACKSPACE", Keys.chord(Keys.BACK_SPACE));
        aliases.put("$KEY_TAB", Keys.chord(Keys.TAB));
        aliases.put("$KEY_ENTER", Keys.chord(Keys.ENTER));
        aliases.put("$KEY_RETURN", Keys.chord(Keys.RETURN));
    }
}
