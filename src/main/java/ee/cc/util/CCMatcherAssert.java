package ee.cc.util;

import org.apache.logging.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.testng.Reporter;

public class CCMatcherAssert {

    public static <T> void assertThat(Logger logger, String reason, T actual, Matcher<? super T> matcher) {
        String msg = reason + "got: " + actual + " expecting " + matcher;
        logger.info(msg);
        Reporter.log(msg);
        MatcherAssert.assertThat(actual, matcher);
    }
}
