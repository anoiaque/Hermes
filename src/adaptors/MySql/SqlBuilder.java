package adaptors.MySql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Attribute;
import core.Hermes;
import core.Table;

public class SqlBuilder {

	public static String getSqlInsertFor(Hermes object) {
		HashMap<String, Object> attributes_values = getAttributesValuesAndForeignKeys(object);
		String fields = attributes_values.keySet().toString().replace("[", "").replace("]", "");
		String values = epure(attributes_values.values().toString());
		
		return "insert into  " + object.getTableName() + "(" + fields + ")" + "values (" + values
				+ ")";
	}

	public static String getSqlUpdateFor(Hermes object) {
		HashMap<String, Object> attributes_values = getAttributesValuesAndForeignKeys(object);
		String setClause = "";
		Iterator<String> attrs = attributes_values.keySet().iterator();
		while (attrs.hasNext()) {
			String attr = attrs.next();
			setClause += attr + "='" + attributes_values.get(attr) + "'";
			if (attrs.hasNext()) {
				setClause += ",";
			}
		}
		return "update " + object.getTableName() + " set " + setClause.replace("'null'", "null")
				+ " where id =" + object.getId();
	}

	public static String getSqlDeleteFor(Hermes object) {
		return "delete from " + object.getTableName() + " where id =" + object.getId();
	}

	public static String getSqlDeleteFor(Hermes object, String conditions) {
		return "delete from " + object.getTableName() + " where " + conditions;
	}

	public static String select(String select_clause, String where_clause, Hermes object) {
		HashMap<String, String> joinedTables = new HashMap<String, String>();

		if (where_clause != null) {
			joinedTables = joinedTables(where_clause, object);
			where_clause = attributeNameToTableName(joinedTables, where_clause, object);
		}

		String from_clause = sqlFrom(joinedTables, object);
		String sqlSelect = "select " + select_clause + " from " + from_clause;

		return (where_clause == null) ? sqlSelect : sqlSelect + " where " + where_clause;

	}

	public static String sqlFrom(HashMap<String, String> joinedTables, Hermes object) {
		String sqlFrom = object.getTableName();

		for (String table : joinedTables.values()) {
			sqlFrom += "," + table;
		}

		return sqlFrom;
	}

	public static HashMap<String, String> joinedTables(String where_clause, Hermes object) {
		HashMap<String, String> joinedTables = new HashMap<String, String>();
		Pattern pattern;

		pattern = Pattern.compile("'(.)*?'");
		String cleaned = pattern.matcher(where_clause).replaceAll("");

		pattern = Pattern.compile("([\\w]*\\.)");
		Matcher matcher = pattern.matcher(cleaned);

		while (matcher.find()) {
			String attr = matcher.group().replace(".", "");
			if (!joinedTables.containsKey(attr))
				joinedTables.put(attr, Table.nameFor(attr, object));
		}
		return joinedTables;
	}

	

	private static String attributeNameToTableName(HashMap<String, String> joinedTables,
			String where_clause, Hermes object) {
		String sqlWhere = where_clause;
		Pattern pattern;

		for (String attribute : joinedTables.keySet()) {
			pattern = Pattern.compile(attribute + ".");
			sqlWhere = pattern.matcher(sqlWhere).replaceAll(joinedTables.get(attribute) + ".");
			sqlWhere += " and " + object.getHasOneAssociations().get(attribute).getFkName() + "="
					+ joinedTables.get(attribute) + ".id";
		}

		return sqlWhere;
	}

	private static String epure(String fields) {
		return fields.replace("[", "'").replace("]", "'").replace(", ", "','").replace("'null'",
				"null");
	}

	private static HashMap<String, Object> getAttributesValuesAndForeignKeys(Hermes object) {
		HashMap<String, Object> attributesValues = new HashMap<String, Object>();

		for (Attribute attribute : object.getAttributes()) {
			attributesValues.put(attribute.getName(), attribute.getValue());
		}
		attributesValues.putAll(object.getAssociations().foreignKeys());

		return attributesValues;
	}
}
