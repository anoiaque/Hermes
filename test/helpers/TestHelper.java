package helpers;

import junit.framework.TestCase;
import core.Finder;
import core.Hermes;
import core.Jointure;

public class TestHelper extends TestCase {

	public static int jointureSizeFor(Hermes object, String attribute) {
		Jointure jointure;
		jointure = object.getManyToManyAssociations().get(attribute).getJointure();
		return Finder.find(object.getId(), jointure).size();
	}

}
