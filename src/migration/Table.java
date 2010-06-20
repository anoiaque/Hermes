package migration;

import java.util.HashMap;

import core.Hermes;
import core.Introspector;

public class Table {

	private String									name;
	private HashMap<String, String>	columns			= new HashMap<String, String>();
	private HashMap<String, String>	foreignKeys	= new HashMap<String, String>();
	private Class<? extends Hermes>	klass;

	public Table(Class<? extends Hermes> klass) {
		this.klass = klass;
	}

	public String sql() {
		String sql;
		String tableName = Introspector.instanciate(klass).getTableName();
		sql = "create table " + tableName + "(";
		sql += columns();
		sql += ")";
		sql += innoDBEngine();
		return sql;
	}

	private String columns() {
		String sql = "";
		for (String column : columns.keySet())
			sql += column + " " + columns.get(column) + ",";
		for (String foreigKey : foreignKeys.keySet())
			sql += foreigKey + " " + foreignKeys.get(foreigKey) + ",";
		return sql.substring(0, sql.length() - 1);
	}

	private static String innoDBEngine() {
		return "ENGINE = InnoDB;";
	}

	// Getters & Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, String> getColumns() {
		return columns;
	}

	public void setColumns(HashMap<String, String> columns) {
		this.columns = columns;
	}

	public HashMap<String, String> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(HashMap<String, String> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public Class<? extends Hermes> getKlass() {
		return klass;
	}

	public void setKlass(Class<? extends Hermes> klass) {
		this.klass = klass;
	}

}
