package adapters.MySql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import core.Attribute;
import core.BelongsTo;
import core.Hermes;

public class SqlBuilder {

	static String	sql	= "";

	public static String build(String request, String conditions, Hermes object) {
		if (request.equals("insert")) return insert(object);
		if (request.equals("update")) return update(object);
		if (request.equals("delete") && conditions != null) return delete(object, conditions);
		if (request.equals("delete")) return delete(object);
		return "";
	}

	public static String build(String request, String select, String conditions, Hermes object) {
		if (request.equals("select")) return select(select, conditions, object);
		return "";
	}

	public static String build(String request, String name) {
		if (request.equals("jointure")) return createJoinTable(name);
		return "";
	}

	// Private methods
	private static String createJoinTable(String name) {
		sql = "create  table if not exists " + name;
		sql += "(parentId int default null,childId int default null);";
		return sql;
	}

	private static String insert(Hermes object) {
		HashMap<String, Object> columnsValues = columnsValues(object);
		String columns = epureColumns(columnsValues.keySet().toString());
		String values = epureValues(columnsValues.values().toString());
		sql = "insert into  " + object.getTableName() + "(" + columns + ")" + "values (" + values + ")";
		return sql;
	}

	private static String update(Hermes object) {
		sql = "update " + object.getTableName() + " set ";
		sql += setClause(columnsValues(object)).replace("'null'", "null");
		sql += " where id =" + object.getId();
		return sql;
	}

	private static String delete(Hermes object) {
		return "delete from " + object.getTableName() + " where id =" + object.getId();
	}

	private static String delete(Hermes object, String conditions) {
		return "delete from " + object.getTableName() + " where " + conditions;
	}

	private static String select(String select, String conditions, Hermes object) {
		String sql = "select distinct " + selectClause(select, object);
		sql += " from " + fromClause(conditions, object);;
		if (conditions == null) return sql;
		return sql + " where " + Analyser.conditions(conditions, object);
	}

	private static String selectClause(String select, Hermes object) {
		select = " " + select.trim();
		select = select.replaceAll("\\s((.)*?)", " " + object.getTableName() + ".$1");
		return select;
	}

	private static String fromClause(String conditions, Hermes object) {
		String from = object.getTableName();
		if (conditions == null) return from;

		for (String table : Analyser.tables(conditions, object).values())
			from += "," + table;
		return from;
	}

	private static String setClause(HashMap<String, Object> columns) {
		Iterator<String> attributes = columns.keySet().iterator();
		String setClause = "";
		String attribute;

		while (attributes.hasNext()) {
			attribute = attributes.next();
			setClause += attribute + "='" + columns.get(attribute) + "'";
			if (attributes.hasNext()) setClause += ",";
		}
		return setClause;
	}

	private static HashMap<String, Object> columnsValues(Hermes object) {
		HashMap<String, Object> columnsValues = new HashMap<String, Object>();
		columnsValues.putAll(attributesValues(object));
		columnsValues.putAll(foreignKeysValues(object));
		return columnsValues;
	}

	private static HashMap<String, Object> attributesValues(Hermes object) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for (Attribute attribute : object.getAttributes())
			attributes.put(attribute.getName(), attribute.getValue());
		return attributes;
	}

	private static HashMap<String, Object> foreignKeysValues(Hermes object) {
		List<BelongsTo> belongsTo = object.getAssociations().getBelongsToAssociations();
		HashMap<String, Object> foreignKeys = new HashMap<String, Object>();

		for (BelongsTo attribute : belongsTo)
			foreignKeys.put(attribute.getFkName(), attribute.getFkValue());
		return foreignKeys;
	}

	private static String epureColumns(String columns) {
		return columns.replace("[", "").replace("]", "");
	}

	private static String epureValues(String fields) {
		String values = fields.replace("[", "'").replace("]", "'");
		return values.replace(", ", "','").replace("'null'", "null");
	}
}
