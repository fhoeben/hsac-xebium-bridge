package nl.hsac.fitnesse.fixture.slim.web.xebium;

import nl.hsac.fitnesse.fixture.slim.SlimFixtureException;

/**
 * Exception to indicate we attempted to interpret a Xebium target/locator, but were unable to do so.
 */
public class UnrecognizedXebiumTarget extends SlimFixtureException {
    public UnrecognizedXebiumTarget(String message) {
        super(false, message);
    }
}
