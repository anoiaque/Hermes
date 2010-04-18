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
		return execute(SqlBuilder.build("insert", null, object), object);
	}

	public boolean update(Hermes object) {
		return execute(SqlBuilder.build("update", null, object), object);
	}

	public boolean delete(Hermes object) {
		return execute(SqlBuilder.build("delete", null, object), object);
	}

	public boolean delete(String conditions, Hermes object) {
		return execute(SqlBuilder.build("delete", conditions, object), object);
	}

	public void save(Hermes object, Connection connexion) throws SQLException {
		execute(SqlBuilder.build("insert", null, object), object, connexion);
	}

	public void update(Hermes object, Connection connexion) throws SQLException {
		execute(SqlBuilder.build("update", null, object), object, connexion);
	}

	public void delete(Hermes object, Connection connexion) throws SQLException {
		execute(SqlBuilder.build("delete", null, object), object, connexion);
	}

	public String javaToSql(String javaType) {
		return Mapping.javaToSql(javaType);
	}

	public Set<Hermes> find(String select, String conditions, Class<? extends Hermes> model) {
		try {
			Hermes object = model.newInstance();
			String sql = SqlBuilder.build("select", select, conditions, object);
			ResultSet rs = finder(sql, object);
			Set<?> objects = ObjectBuilder.toObjects(rs, model);
			return (Set<Hermes>) objects;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Set<Hermes> find(String select, String conditions, Jointure join) {
		String sql = SqlBuilder.build("select", select, conditions, join);

		ResultSet rs = finder(sql, join);
		return ObjectBuilder.toObjects(rs, Jointure.class);
	}

	public Set<Hermes> finder(String sql, Class<? extends Hermes> model) {
		try {
			ResultSet rs = finder(sql, model.newInstance());
			Set<?> objects = ObjectBuilder.toObjects(rs, model);
			return (Set<Hermes>) objects;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResultSet finder(String sql, Hermes model) {
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
