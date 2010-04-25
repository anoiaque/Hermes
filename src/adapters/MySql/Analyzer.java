package adapters.MySql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Hermes;
import core.Inflector;

public class Analyzer {

	public static HashMap<String, String> tables(String conditions, Hermes object) {
		HashMap<String, String> tables = new HashMap<String, String>();

		for (String attribute : extractAttributes(conditions))
			tables.put(attribute, Inflector.tableize(attribute, object, false));
		return tables;
	}

	public static HashMap<String, String> jointuresTables(String conditions, Hermes object) {
		HashMap<String, String> tables = new HashMap<String, String>();

		for (String attribute : extractAttributes(conditions))
			if (isManyToManyAttribute(attribute, object)) {
				tables.put(attribute, Inflector.tableize(attribute, object, true));
			}
		return tables;
	}

	public static String conditions(String conditions, Hermes object) {
		HashMap<String, String> tables = tables(conditions, object);
		String table;

		for (String attribute : tables.keySet()) {
			table = tables.get(attribute);
			conditions = conditions.replaceAll(attribute + ".", table + ".");
			if (isManyToManyAttribute(attribute, object)) {
				conditions += manyToManyCondition(attribute, object, table);
			}
			else conditions += hasOneOrManyCondition(object, table);
		}
		return conditions;
	}

	// Private methods

	private static List<String> extractAttributes(String conditions) {
		List<String> attributes = new ArrayList<String>();
		Pattern pattern = Pattern.compile("'(.)*?'");;
		conditions = pattern.matcher(conditions).replaceAll("");
		pattern = Pattern.compile("([\\w]*\\.)");
		Matcher matcher = pattern.matcher(conditions);

		while (matcher.find())
			attributes.add(matcher.group().replace(".", ""));
		return attributes;
	}

	private static String hasOneOrManyCondition(Hermes object, String table) {
		String sql = "";
		sql += " and (" + table + ".";
		sql += object.getClass().getSimpleName().toLowerCase() + "_id" + "=";
		sql += object.getTableName() + ".id)";
		return sql;
	}

	private static String manyToManyCondition(String attribute, Hermes object, String table) {
		String joinedTable = Inflector.tableize(attribute, object, true);
		String parentTable = object.getTableName();
		String sql = "";

		sql = " and (" + joinedTable + ".parentId = " + parentTable + ".id";
		sql += " and " + joinedTable + ".childId" + "=" + table + ".id)";
		return sql;
	}

	private static boolean isManyToManyAttribute(String attribute, Hermes object) {
		return object.getAssociations().getManyToManyAsociations().containsKey(attribute);
	}

}
