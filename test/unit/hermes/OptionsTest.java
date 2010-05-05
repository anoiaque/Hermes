package unit.hermes;

import junit.framework.TestCase;
import core.Options;

public class OptionsTest extends TestCase {

	private String	options	= "limit => 2, offset => 10, order => 'name asc'";

	public void testLimit() {
		assertEquals("2", new Options(options).limit());
	}

	public void testOffset() {
		assertEquals("10", new Options(options).offset());
	}

	public void testOrder() {
		assertEquals("'name asc'", new Options(options).order());
	}

}
