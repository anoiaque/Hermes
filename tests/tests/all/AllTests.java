package tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;
import tests.unit.hermes.HermesBasicTests;
import tests.unit.hermes.HermesFindersTests;
import tests.unit.hermes.HermesRelationalTests;
import tests.unit.mysqladaptor.MySqlAdaptorTests;
import tests.unit.pool.PoolTests;

public class AllTests extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("");
        suite.addTest(new TestSuite(HermesBasicTests.class));
        suite.addTest(new TestSuite(HermesRelationalTests.class));
        suite.addTest(new TestSuite(HermesFindersTests.class));
        suite.addTest(new TestSuite(PoolTests.class));
        suite.addTest(new TestSuite(MySqlAdaptorTests.class));
        return suite;
    }
}
