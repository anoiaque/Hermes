package core;

import java.util.Iterator;
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

	public static Hermes find(int id, Class<? extends Hermes> model) {
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

	public static Set<?> find(int parentId, Jointure join) {
		return adaptor.find("*", "parentId = " + parentId, join);
	}
}
