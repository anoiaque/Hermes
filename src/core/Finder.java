package core;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adapters.Adapter;

public class Finder {

	private static Adapter	adaptor	= Adapter.get();

	public static Hermes findFirst(String conditions, Class<? extends Hermes> model) {
		return findFirst("*", conditions, model);
	}

	public static Hermes findFirst(String select, String conditions, Class<? extends Hermes> model) {
		Iterator<?> set = find(conditions, model).iterator();
		if (!set.hasNext()) return null;
		return (Hermes) set.next();
	}

	public static Hermes find(long id, Class<? extends Hermes> model) {
		return findFirst("*", "id = " + id, model);
	}

	public static Set<?> find(String conditions, Class<? extends Hermes> model) {
		return find("*", conditions, model);
	}

	public static Set<?> find(String select, String conditions, Class<? extends Hermes> model) {
		Set<?> objects = adaptor.find(select, conditions, model);
		Loader.loadAssociations(objects);
		return objects;
	}

	public static Set<?> find(long parentId, Jointure join) {
		return adaptor.find("*", "parentId = " + parentId, join);
	}

	public static Set<?> find(List<Integer> ids, Class<? extends Hermes> model) {
		String condition = "id in (";
		for (Integer id : ids) {
			condition += id;
			if (ids.indexOf(id) < ids.size() - 1) condition += ",";
		}
		condition += ")";
		return find(condition, model);
	}

	public static Set<?> findBySql(String sql, Class<? extends Hermes> model) {
		return adaptor.finder(sql, model);
	}

	public static int count(String conditions, Class<? extends Hermes> model) {
		return adaptor.count(conditions, Introspector.instanciate(model).getTableName());

	}

	public static int count(Class<? extends Hermes> model) {
		return adaptor.count(null, Introspector.instanciate(model).getTableName());

	}

}
