package adaptors.MySql;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Hermes;
import core.Table;

public class Analyser {

	public static String joinedConditions(String conditions, Hermes object) {
		if (conditions == null) return null;
		return Analyser.conditions(conditions, object);
	}

	public static HashMap<String, String> joinedTables(String conditions, Hermes object) {
		HashMap<String, String> tables = new HashMap<String, String>();
		String attribute;
		Pattern pattern = Pattern.compile("'(.)*?'");;
		conditions = pattern.matcher(conditions).replaceAll("");
		pattern = Pattern.compile("([\\w]*\\.)");
		Matcher matcher = pattern.matcher(conditions);
		while (matcher.find()) {
			attribute = matcher.group().replace(".", "");
			if (tables.containsKey(attribute)) continue;
			tables.put(attribute, Table.nameFor(attribute, object));
		}
		return tables;
	}

	public static String conditions(String conditions, Hermes object) {
		HashMap<String, String> tables = joinedTables(conditions, object);
		Pattern pattern;

		for (String attribute : tables.keySet()) {
			pattern = Pattern.compile(attribute + ".");
			conditions = pattern.matcher(conditions).replaceAll(tables.get(attribute) + ".");
			conditions += " and " + tables.get(attribute) + ".";
			conditions += object.getClass().getSimpleName().toLowerCase() + "_id" + "=";
			conditions += object.getTableName() + ".id";
		}
		return conditions;
	}

}
