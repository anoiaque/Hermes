package all;

import helpers.Database;
import junit.framework.Test;
import junit.framework.TestSuite;
import unit.hermes.BasicTest;
import unit.hermes.FindersTest;
import unit.hermes.HasManyTest;
import unit.hermes.HasOneTest;
import unit.hermes.HermesRelationalTest;
import unit.hermes.JointureTest;
import unit.hermes.UpdaterTest;
import unit.mysqladaptor.SqlBuilderTest;
import unit.pool.PoolTest;

public class AllTest extends TestSuite {

	public static Test suite() {
		Database.clear();
		TestSuite suite = new TestSuite("all");
		suite.addTest(new TestSuite(FindersTest.class));
		suite.addTest(new TestSuite(BasicTest.class));
		suite.addTest(new TestSuite(HermesRelationalTest.class));
		suite.addTest(new TestSuite(SqlBuilderTest.class));
		suite.addTest(new TestSuite(HasManyTest.class));
		suite.addTest(new TestSuite(HasOneTest.class));
		suite.addTest(new TestSuite(JointureTest.class));
		suite.addTest(new TestSuite(UpdaterTest.class));
		suite.addTest(new TestSuite(PoolTest.class));
		return suite;
	}
}
