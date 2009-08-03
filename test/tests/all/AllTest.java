package tests.all;



import junit.framework.Test;
import junit.framework.TestSuite;
import tests.unit.hermes.HermesBasicTest;
import tests.unit.hermes.HermesFindersTest;
import tests.unit.hermes.HermesRelationalTest;
import tests.unit.mysqladaptor.MySqlAdaptorTest;
import tests.unit.pool.PoolTest;

public class AllTest extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("");
        suite.addTest(new TestSuite(HermesBasicTest.class));
        suite.addTest(new TestSuite(HermesRelationalTest.class));
        suite.addTest(new TestSuite(HermesFindersTest.class));
        suite.addTest(new TestSuite(PoolTest.class));
        suite.addTest(new TestSuite(MySqlAdaptorTest.class));
        return suite;
    }
}
