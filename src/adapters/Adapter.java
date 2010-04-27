package adapters;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import adapters.MySql.MySql;
import configuration.Configuration;
import configuration.Configuration.DBMSConfig;
import core.Hermes;
import core.Jointure;

public abstract class Adapter {

	private static Adapter	adaptor	= null;

	public static Adapter get() {
		if (adaptor != null) return adaptor;
		synchronized (Adapter.class) {
			DBMSConfig config = Configuration.DBMSConfig.get();
			adaptor = adaptor(config.getAdapter());
		}
		return adaptor;
	}

	private static Adapter adaptor(String adapter) {
		if (adapter.equalsIgnoreCase("MySql")) { return new MySql(); }
		return null;
	}

	public abstract boolean save(Hermes object);

	public abstract boolean update(Hermes object);

	public abstract boolean delete(Hermes object);

	public abstract boolean delete(String conditions, Hermes object);

	public abstract boolean deleteAll(Class<? extends Hermes> model);

	public abstract boolean execute(String sql, Hermes object);

	public abstract void save(Hermes object, Connection connexion) throws SQLException;

	public abstract void update(Hermes object, Connection connexion) throws SQLException;

	public abstract void delete(Hermes object, Connection connexion) throws SQLException;

	public abstract void execute(String sql, Hermes object, Connection connexion) throws SQLException;

	public abstract Set<Hermes> find(String select, String conditions, Class<? extends Hermes> model);

	public abstract Set<Hermes> find(String select, String conditions, Jointure join);

	public abstract Set<Hermes> finder(String sql, Class<? extends Hermes> model);

	public abstract ResultSet finder(String sql);

	public abstract int count(String table);

	public abstract String javaToSql(String javaType);
}
