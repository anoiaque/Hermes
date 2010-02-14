package core;

import adaptors.Adaptor;
import adaptors.MySql.ObjectBuilder;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Set;

public class Finder {

	private static Adaptor	adaptor	= Adaptor.get();

	public static Hermes find(int id, Class<? extends Hermes> model) {
		return findFirst("*", "id = " + id, model);
	}

	public static Hermes findFirst(String select, String conditions, Class<? extends Hermes> model) {
		Iterator<?> set = find(conditions, model).iterator();
		if (!set.hasNext()) return null;
		return (Hermes) set.next();
	}

	public static Hermes findFirst(String conditions, Class<? extends Hermes> model) {
		return findFirst("*", conditions, model);
	}

	public static Set<?> find(String conditions, Class<? extends Hermes> model) {
		return find("*", conditions, model);
	}

	public static Set<?> find(String select, String conditions, Class<? extends Hermes> model) {
		try {
			ResultSet rs = adaptor.find(select, conditions, model.newInstance());
			return ObjectBuilder.toObjects(rs, model);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Finder for Jointure models.
	// The reason for this finder is that instances of this model have not the
	// same table name.
	public static Set<?> joinFind(int parentId, Jointure join) {
		return ObjectBuilder.toObjects(adaptor.find("*", "parentId = " + parentId, join),
				Jointure.class);
	}
}
