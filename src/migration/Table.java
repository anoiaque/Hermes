package migration;

import java.util.Collection;
import java.util.HashMap;

import core.Hermes;
import core.Introspector;
import core.Jointure;

public class Table {

	private String									name;
	private HashMap<String, String>	columns			= new HashMap<String, String>();
	private HashMap<String, String>	foreignKeys	= new HashMap<String, String>();
	private Class<? extends Hermes>	klass;
	private Class<? extends Hermes>	parent;
	private boolean									sti					= false;

	// For Jointure Table
	public Table(String name) {
		this.foreignKeys.put("parentId", "integer");
		this.foreignKeys.put("childId", "integer");
		this.klass = Jointure.class;
		this.sti = false;
		this.name = name;
	}

	public Table(Class<? extends Hermes> klass) {
		this.klass = klass;
		this.name = Introspector.instanciate(klass).getTableName();
		this.parent = (Class<? extends Hermes>) klass.getSuperclass();
		this.sti = !parent.equals(Hermes.class);
	}

	public String sql() {
		String sql;
		sql = "create table " + name + "(";
		sql += columns();
		sql += ")";
		sql += innoDBEngine();
		return sql;
	}

	public static Table withKlass(Class<? extends Hermes> klass, Collection<Table> tables) {
		for (Table table : tables)
			if (table.getKlass().equals(klass)) return table;
		return null;
	}

	public static boolean exists(String name, Collection<Table> tables) {
		for (Table table : tables)
			if (table.getName().equals(name)) return true;
		return false;
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

	public Class<? extends Hermes> getParent() {
		return parent;
	}

	public void setParent(Class<? extends Hermes> parent) {
		this.parent = parent;
	}

	public boolean isSingleTableInheritence() {
		return sti;
	}

	public void setSingleTableInheritence(boolean singleTableInheritence) {
		this.sti = singleTableInheritence;
	}

}
