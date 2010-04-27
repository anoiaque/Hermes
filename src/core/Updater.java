package core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import adapters.Adapter;

public class Updater {

	private static Adapter	adapter	= Adapter.get();

	public static boolean save(Hermes object) {
		if (!object.isNewRecord()) return update(object);
		beforeCallbacks(object);
		object.loadAttributes();
		boolean saved = adapter.save(object);
		saved = saved && object.getAssociations().save();
		if (saved) afterCallbacks(object);
		return saved;
	}

	public static boolean update(Hermes object) {
		Callback.beforeUpdate(object);
		object.loadAttributes();
		boolean updated = adapter.update(object);
		updated = updated && object.getAssociations().save();
		if (updated) Callback.afterUpdate(object);
		return updated;
	}

	public static boolean delete(Hermes object) {
		Callback.beforeDelete(object);
		boolean deleted = delete(null, object);
		if (deleted) Callback.afterDelete(object);
		return deleted;
	}

	public static boolean delete(String conditions, Hermes object) {
		Callback.beforeDelete(object);
		object.getAssociations().delete();
		boolean deleted = adapter.delete(conditions, object);
		if (deleted) Callback.afterDelete(object);
		return deleted;
	}

	public static Hermes create(HashMap<String, Object> values, Class<? extends Hermes> model) {
		Hermes object = Introspector.instanciate(model);
		for (String attribute : values.keySet())
			Introspector.set(attribute, values.get(attribute), object);
		if (object.save()) return object;
		return null;
	}

	public static boolean deleteAll(Class<? extends Hermes> model) {
		return adapter.deleteAll(model);
	}

	public static boolean executeSql(String sql) {
		return adapter.execute(sql, null);
	}

	private static void beforeCallbacks(Hermes object) {
		Callback.beforeSave(object);
		Callback.beforeCreate(object);
	}

	private static void afterCallbacks(Hermes object) {
		Callback.afterSave(object);
		Callback.afterCreate(object);
	}

	// For Transactions

	public static void save(Hermes object, Connection connexion) throws SQLException {
		if (!object.isNewRecord()) update(object, connexion);
		else {
			object.loadAttributes();
			adapter.save(object, connexion);
			object.getAssociations().save();
		}
	}

	public static void update(Hermes object, Connection connexion) throws SQLException {
		object.loadAttributes();
		adapter.update(object, connexion);
		object.getAssociations().save();
	}

	public static void executeSql(String sql, Hermes object, Connection connexion)
			throws SQLException {
		adapter.execute(sql, object, connexion);
	}

	public static void delete(Hermes object, Connection connexion) throws SQLException {
		object.getAssociations().delete();
		adapter.delete(object, connexion);
	}

}
