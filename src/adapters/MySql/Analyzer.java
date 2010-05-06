package adapters.MySql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Associations;
import core.Hermes;
import core.Introspector;
import core.Table;

public class Analyzer {

	public static HashMap<String, String> tables(String conditions, Hermes object) {
		HashMap<String, String> tables = new HashMap<String, String>();

		for (String attribute : extractAttributes(conditions))
			tables.put(attribute, Introspector.table(attribute, object));

		return tables;
	}

	public static HashMap<String, String> jointures(String conditions, Hermes object) {
		HashMap<String, String> tables = new HashMap<String, String>();

		for (String attribute : extractAttributes(conditions))
			if (Associations.isManyToManyAttribute(attribute, object)) {
				String jointure = Associations.jointure(attribute, object);
				tables.put(attribute, jointure);
			}
		return tables;
	}

	public static String conditions(String conditions, Hermes object) {
		HashMap<String, String> tables = tables(conditions, object);
		String table;

		for (String attribute : tables.keySet()) {
			table = tables.get(attribute);
			conditions = conditions.replaceAll(attribute + ".", table + ".");
			if (Associations.isManyToManyAttribute(attribute, object)) {
				conditions += manyToManyCondition(attribute, object, table);
			}
			else conditions += hasOneOrManyCondition(object, table);
		}
		return doubleQuote(conditions);
	}

	// Private methods

	private static String doubleQuote(String conditions) {
		Pattern pattern = Pattern.compile("=.*?'(.*?)'(.*?)'");
		Matcher matcher = pattern.matcher(conditions);

		while (matcher.find()) 
			if(! (matcher.group(2).contains(" or ") || matcher.group(2).contains(" and ")))
			conditions = conditions.replace("'" + matcher.group(1) + "'" + matcher.group(2) + "'", "'"
					+ matcher.group(1) + "''" + matcher.group(2) + "'");
		
		return conditions;
	}

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
		sql += Table.foreignKeyName(object).toLowerCase() + "=";
		sql += object.getTableName() + ".id)";
		return sql;
	}

	private static String manyToManyCondition(String attribute, Hermes object, String table) {
		String jointure = Associations.jointure(attribute, object);
		String parent = object.getTableName();
		String sql = "";

		sql = " and (" + jointure + ".parentId = " + parent + ".id";
		sql += " and " + jointure + ".childId" + "=" + table + ".id)";
		return sql;
	}

}
