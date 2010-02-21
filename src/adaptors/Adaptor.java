package adaptors;

import adaptors.MySql.MySql;
import configuration.Configuration;
import configuration.Configuration.DBMSConfig;
import core.Hermes;
import core.Jointure;

import java.sql.ResultSet;
import java.util.Set;

public abstract class Adaptor {

	private static Adaptor	adaptor	= null;

	public static Adaptor get() {
		if (adaptor == null) {
			synchronized (Adaptor.class) {
				DBMSConfig config = Configuration.DBMSConfig.get();
				adaptor = adaptor(config.getAdapter());
			}
		}
		return adaptor;
	}

	private static Adaptor adaptor(String adapter) {
		if (adapter.equalsIgnoreCase("MySql")) { return new MySql(); }
		return null;
	}

	public abstract boolean save(Hermes object);

	public abstract boolean update(Hermes object);

	public abstract boolean delete(Hermes object);

	public abstract boolean delete(Hermes object, String conditions);

	public abstract boolean execute(String sql, Hermes object);

	public abstract Set<Hermes> find(String select, String conditions, Class<? extends Hermes> model);

	public abstract Set<Hermes> find(String select, String conditions, Jointure join);

	public abstract ResultSet finder(String select, String conditions, Hermes model);

	public abstract String javaToSql(String javaType);
}
