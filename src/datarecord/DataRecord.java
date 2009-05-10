package datarecord;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.PreparedStatement;

import com.mysql.jdbc.Statement;

public class DataRecord {

	private static Connection connexion = null;
	private String table_name;
	private HashMap<String, String> fieldsType = new HashMap<String, String>();
	private HashMap<String, Object> fieldsValue = new HashMap<String, Object>();
	private int id = 0;

	public boolean save() {
		setFieldsType();
		setFieldsValue();
		String fields = fieldsType.keySet().toString().replace("[", "")
				.replace("]", "");
		String values = getSQLValues();
		String sql = "insert into  " + table_name + "(" + fields + ")"
				+ "values (" + values + ")";
		boolean saved = false;
		ResultSet rs = null;

		try {
			PreparedStatement statement = connexion.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			saved = statement.execute();
			rs = statement.getGeneratedKeys();
			rs.next();
			this.id = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
		return saved;
	}

	public boolean delete(int id) {
		boolean deleted = false;
		String sql = "delete from " + table_name + " where id =" + id;
		try {
			PreparedStatement statement = connexion.prepareStatement(sql);
			statement.execute();
			deleted = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleted;
	}

	public boolean find(int id) {
		String sql = "select * from " + table_name + " where id =" + id;
		boolean found = false;
		ResultSet rs = null;
		try {
			PreparedStatement statement = connexion.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);
			rs = statement.executeQuery();
			if (rs.next()) {
				found = true;
				for (Field field : this.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					field.set(this, rs.getObject(field.getName()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return found;
	}

	// Constructeurs

	public DataRecord() {
		if (connexion == null)
			getConnexion();
		setTable_name(this.getClass().getSimpleName());
		setFieldsType();
		setFieldsValue();
	}

	// Private methods
	private void getConnexion() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connexion = DriverManager.getConnection(
					"jdbc:mysql://localhost/test", "root", "");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String SQLType(String simplename) {
		if (simplename.equals("int"))
			return "int";
		else if (simplename.equals("String"))
			return "varchar(250)";
		else
			return null;

	}

	private String getSQLValues() {
		Object[] values = fieldsValue.values().toArray();
		String valuesForSQL = "";
		for (int i = 0; i < values.length; i++) {
			valuesForSQL += "'" + values[i] + "'";
			if (i < values.length - 1)
				valuesForSQL += ",";
		}
		return valuesForSQL;
	}

	// Getters & Setters
	private void setFieldsType() {
		for (Field field : this.getClass().getDeclaredFields())
			fieldsType.put(field.getName(), SQLType(field.getType()
					.getSimpleName()));
	}

	private void setFieldsValue() {
		for (Field field : this.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				fieldsValue.put(field.getName(), field.get(this));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public HashMap<String, String> getDatabaseFields() {
		return fieldsType;
	}

	public String getTable_name() {
		return table_name;
	}

	public int getId() {
		return id;
	}

}
