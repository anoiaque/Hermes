package adapters.MySql;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Hermes;
import core.Jointure;
import core.Table;

public class Analyser {

	public static HashMap<String, String> tables(String conditions, Hermes object) {
		HashMap<String, String> tables = new HashMap<String, String>();
		String attribute;
		Pattern pattern = Pattern.compile("'(.)*?'");;
		conditions = pattern.matcher(conditions).replaceAll("");
		pattern = Pattern.compile("([\\w]*\\.)");
		Matcher matcher = pattern.matcher(conditions);

		while (matcher.find()) {
			attribute = matcher.group().replace(".", "");
			tables.put(attribute, Table.nameFor(attribute, object));
		}
		return tables;
	}

	public static String conditions(String conditions, Hermes object) {
		HashMap<String, String> tables = tables(conditions, object);
		Pattern pattern;
		conditions=" "+conditions+" ";
		for (String attribute : tables.keySet()) {
			if (isManyToManyAttribute(attribute, object)) {
				pattern = Pattern.compile("\\s+" + attribute + ".*?\\s*=\\s*.*?\\s");
				Matcher matcher = pattern.matcher(conditions);
				matcher.find();
				String condition = matcher.group();
			//	conditions = conditions.replaceAll(condition, "");
				pattern = Pattern.compile(attribute + ".");
				condition = pattern.matcher(condition).replaceAll(tables.get(attribute) + ".");

				String table = tables.get(attribute);
				if (!conditions.equals("")) conditions += " and ";
				conditions += object.getTableName()+".id in " + joinedConditions(attribute, object, condition, table);
			} else {
				
				pattern = Pattern.compile("\\s+" + attribute + ".*?\\s*=\\s*('.*?')*(.)*\\s");
				Matcher matcher = pattern.matcher(conditions);
				matcher.find();
				String condition = matcher.group();
				pattern = Pattern.compile(attribute + ".");
				conditions = conditions.replaceAll(condition, "");

				condition = pattern.matcher(condition).replaceAll(tables.get(attribute) + ".");

				conditions += " ("+condition;
				conditions += " and " + tables.get(attribute) + ".";
				conditions += object.getClass().getSimpleName().toLowerCase() + "_id" + "=";
				conditions += object.getTableName() + ".id)";
			}
		}
		return conditions;
	}

	private static String joinedConditions(String attribute, Hermes object, String condition,
			String table) {
		Jointure jointure = object.getAssociations().getManyToManyAsociations().get(attribute)
				.getJointure();
		String sql = "(select parentId from " + jointure.getTableName();
		sql += " where childId in (select id from " + table + " where " + condition +"))";
		return sql;
	}


	private static boolean isManyToManyAttribute(String attribute, Hermes object) {
		return object.getAssociations().getManyToManyAsociations().containsKey(attribute);
	}

}
