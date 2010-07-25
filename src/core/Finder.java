package core;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adapters.Adapter;

public class Finder {

	private static Adapter	adaptor	= Adapter.get();

	public static Hermes findFirst(String select, String conditions, Class<? extends Hermes> model) {
		Iterator<?> set = find("*", conditions, "limit => 1", model).iterator();
		if (!set.hasNext()) return null;
		return (Hermes) set.next();
	}

	public static Hermes findLast(String select, String conditions, Class<? extends Hermes> model) {
		int offset = count(conditions, model) - 1;
		Iterator<?> set = find("*", conditions, "limit => 1, offset => " + offset, model).iterator();
		if (!set.hasNext()) return null;
		return (Hermes) set.next();
	}

	public static Hermes find(int id, Class<? extends Hermes> model) {
		return findFirst("*", "id = " + id, model);
	}

	public static Set<?> find(String select, String conditions, String options,
			Class<? extends Hermes> model) {

		conditions = STIzeCondition(conditions, model);
		Set<?> objects = adaptor.find(select, conditions, model, options);
		Loader.loadAssociations(objects);
		return objects;
	}

	private static String STIzeCondition(String conditions, Class<? extends Hermes> model) {
		String stiCondition = "klass = '" + model.getSimpleName() + "'";;

		if (!Introspector.instanciate(model).isSTIModel()) return conditions;
		if (conditions == null) return stiCondition;
		return conditions += " and " + stiCondition;
	}

	public static Set<?> find(int parentId, Jointure join) {
		return adaptor.find("*", "parentId = " + parentId, join);
	}

	public static Set<?> find(List<Integer> ids, Class<? extends Hermes> model) {
		String condition = "id in (";
		for (Integer id : ids) {
			condition += id;
			if (ids.indexOf(id) < ids.size() - 1) condition += ",";
		}
		condition += ")";
		return find("*", condition, null, model);
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
