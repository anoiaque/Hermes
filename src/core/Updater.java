package core;

import java.sql.Connection;
import java.sql.SQLException;

import adapters.Adapter;

public class Updater {

	private static Adapter	adapter	= Adapter.get();

	public static boolean save(Hermes object) {
		if (!object.isNewRecord()) return update(object);
		object.loadAttributes();
		boolean saved = adapter.save(object);
		saved = saved && object.getAssociations().save();
		return saved;
	}

	public static boolean update(Hermes object) {
		object.loadAttributes();
		boolean updated = adapter.update(object);
		updated = updated && object.getAssociations().save();
		return updated;
	}

	public static boolean delete(Hermes object) {
		return delete(null, object);
	}

	public static boolean delete(String conditions, Hermes object) {
		object.getAssociations().delete();
		boolean deleted = adapter.delete(conditions, object);
		if (deleted) object.setId(0);
		return deleted;
	}

	public static boolean executeSql(String sql) {
		return adapter.execute(sql, null);
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
