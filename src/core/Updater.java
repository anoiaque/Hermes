package core;

import adapters.Adapter;

public class Updater {

	private static Adapter	adapter	= Adapter.get();

	public static boolean save(Hermes object) {
		if (!object.isNewRecord()) return update(object);
		object.loadAttributes();
		boolean saved = adapter.save(object);
		object.getAssociations().save();
		return saved;
	}

	public static boolean update(Hermes object) {
		object.loadAttributes();
		boolean updated = adapter.update(object);
		object.getAssociations().save();
		return updated;
	}

	public static boolean delete(Hermes object) {
		object.getAssociations().cascadeDelete();
		boolean deleted = adapter.delete(object);
		if (deleted) object.setId(0);
		return deleted;
	}

	public static boolean delete(Hermes object, String conditions) {
		object.getAssociations().cascadeDelete();
		boolean deleted = adapter.delete(object, conditions);
		if (deleted) object.setId(0);
		return deleted;
	}
}
