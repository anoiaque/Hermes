package adapters.MySql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import core.Attribute;
import core.BelongsTo;
import core.Hermes;
import core.Options;

public class Sql {

	public enum Request {
		INSERT, UPDATE, DELETE, SELECT, JOINTURE
	}

	private Request	request;
	private String	conditions;
	private String	select;
	private String	options;
	private String	name;
	private Hermes	object;

	public Sql(Request request, String name) {
		this.request = request;
		this.name = name;
	}

	public Sql(Request request, Hermes object) {
		this.request = request;
		this.object = object;
	}

	public Sql(Request request, String conditions, Hermes object) {
		this.request = request;
		this.conditions = conditions;
		this.object = object;
	}

	public Sql(Request request, String select, String conditions, String options, Hermes object) {
		this.request = request;
		this.select = select;
		this.conditions = conditions;
		this.object = object;
		this.options = options;
	}

	public String request() {
		switch (request) {
		case INSERT:
			return insert(object);
		case UPDATE:
			return update(object);
		case DELETE:
			if (conditions == null) return delete(object);
			return delete(object, conditions);
		case SELECT:
			return select(select, conditions, object, options);
		case JOINTURE:
			return jointure(name);
		}
		return "";
	}

	// Private methods
	private static String jointure(String name) {
		String sql = "create  table if not exists " + name;
		sql += "(parentId int default null,childId int default null);";
		return sql;
	}

	private static String insert(Hermes object) {
		HashMap<String, Object> columnsValues = columnsValues(object);
		String columns = epureColumns(columnsValues.keySet().toString());
		String values = epureValues(columnsValues.values().toString());
		return "insert into " + object.getTableName() + "(" + columns + ")" + "values (" + values + ")";
	}

	private static String update(Hermes object) {
		String sql = "update " + object.getTableName() + " set ";
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

	private static String select(String select, String conditions, Hermes object, String options) {
		String sql = "select distinct " + selectClause(select, object);
		sql += " from " + fromClause(conditions, object);
		if (conditions == null) return sql + order(options) + limit(options);
		sql += " where " + Analyzer.conditions(conditions, object);
		sql += order(options);
		sql += limit(options);
		return sql;
	}

	private static String limit(String options) {
		Options opts = new Options(options);

		String limit = opts.limit();
		String offset = opts.offset();
		if (limit == null) return "";
		if (offset == null) return " limit " + limit;
		return " limit " + offset + "," + limit;
	}

	private static String order(String options) {
		;
		Options opts = new Options(options);
		String order = opts.order();
		if (order == null) return "";
		return " order by " + order;
	}

	private static String selectClause(String select, Hermes object) {
		select = " " + select.trim();
		select = select.replaceAll("\\s((.)*?)", " " + object.getTableName() + ".$1");
		return select;
	}

	private static String fromClause(String conditions, Hermes object) {
		String from = object.getTableName();
		if (conditions == null) return from;

		for (String table : Analyzer.tables(conditions, object).values())
			from += "," + table;

		for (String table : Analyzer.jointures(conditions, object).values())
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

	private static String epureColumns(String columns) {
		return columns.replace("[", "").replace("]", "");
	}

	private static String epureValues(String fields) {
		String values = fields.replace("[", "'").replace("]", "'");
		return values.replace(", ", "','").replace("'null'", "null");
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
			attributes.put(attribute.getName(), TypeCast.toSql(attribute));
		return attributes;
	}

	private static HashMap<String, Object> foreignKeysValues(Hermes object) {
		List<BelongsTo> belongsTo = object.getAssociations().getBelongsToAssociations();
		HashMap<String, Object> foreignKeys = new HashMap<String, Object>();

		for (BelongsTo attribute : belongsTo)
			foreignKeys.put(attribute.getFkName(), attribute.getFkValue());
		return foreignKeys;
	}

}
