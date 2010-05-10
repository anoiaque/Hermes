package adapters.MySql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import pool.Pool;
import adapters.Adapter;

import com.mysql.jdbc.Statement;

import core.Hermes;
import core.Jointure;

public class MySql extends Adapter {

	public boolean save(Hermes object) {
		return execute(new Sql(Sql.Request.INSERT, null, object).request(), object);
	}

	public boolean update(Hermes object) {
		return execute(new Sql(Sql.Request.UPDATE, null, object).request(), object);
	}

	public boolean delete(Hermes object) {
		return execute(new Sql(Sql.Request.DELETE, null, object).request(), object);
	}

	public boolean deleteAll(Class<? extends Hermes> model) {
		try {
			return execute("delete from " + model.newInstance().getTableName(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(String conditions, Hermes object) {
		return execute(new Sql(Sql.Request.DELETE, conditions, object).request(), object);
	}

	public void save(Hermes object, Connection connexion) throws SQLException {
		execute(new Sql(Sql.Request.INSERT, null, object).request(), object, connexion);
	}

	public void update(Hermes object, Connection connexion) throws SQLException {
		execute(new Sql(Sql.Request.UPDATE, null, object).request(), object, connexion);
	}

	public void delete(Hermes object, Connection connexion) throws SQLException {
		execute(new Sql(Sql.Request.DELETE, null, object).request(), object, connexion);
	}

	public String javaToSql(String javaType) {
		return Mapping.javaToSql(javaType);
	}

	public Set<Hermes> find(String select, String conditions, Class<?> model, String options) {
		try {
			Hermes object = (Hermes) model.newInstance();
			String sql = new Sql(Sql.Request.SELECT, select, conditions, options, object).request();
			ResultSet rs = finder(sql);
			Set<?> objects = Objects.get(rs, model);
			return (Set<Hermes>) objects;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean find(String attribute, Object value, Hermes object) {
		String conditions = attribute + "='" + value + "'";
		String sql = new Sql(Sql.Request.SELECT, "*", conditions, null, object).request();
		Set<?> objects = Objects.get(finder(sql), object.getClass());
		return objects.size() > 0;
	}

	public Set<Hermes> find(String select, String conditions, Jointure join) {
		String sql = new Sql(Sql.Request.SELECT, select, conditions, null, join).request();

		ResultSet rs = finder(sql);
		return Objects.get(rs, Jointure.class);
	}

	public Set<Hermes> finder(String sql, Class<? extends Hermes> model) {
		try {
			ResultSet rs = finder(sql);
			Set<?> objects = Objects.get(rs, model);
			return (Set<Hermes>) objects;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int count(String conditions, String table) {
		String sql = "select count(*) from " + table;
		if (conditions != null) sql += " where " + conditions;
		ResultSet rs = finder(sql);
		try {
			rs.next();
			return rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return 0;

		}
	}

	public ResultSet finder(String sql) {
		// System.out.println(sql);
		Connection connexion = null;
		Pool pool = Pool.getInstance();
		try {
			connexion = pool.getConnexion();
			PreparedStatement statement = connexion.prepareStatement(sql);
			return statement.executeQuery();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			pool.release(connexion);
		}
	}

	public boolean execute(String sql, Hermes object) {
		Connection connexion = null;
		Pool pool = Pool.getInstance();
		ResultSet rs = null;
		PreparedStatement statement;
		// System.out.println(sql);
		try {
			connexion = pool.getConnexion();
			statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.execute();
			rs = statement.getGeneratedKeys();
			if (rs.next() && object != null) object.setId(rs.getInt(1));
		}
		catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
		finally {
			pool.release(connexion);
			try {
				if (rs != null) rs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void execute(String sql, Hermes object, Connection connexion) throws SQLException {
		ResultSet rs = null;
		PreparedStatement statement;
		statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		statement.execute();
		rs = statement.getGeneratedKeys();
		if (rs.next() && object != null) object.setId(rs.getInt(1));
	}

}
