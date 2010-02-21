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

	public static HashMap<String, String> tables(String conditions, Hermes object) {
		HashMap<String, String> joinedTables = new HashMap<String, String>();
		Pattern pattern;
		pattern = Pattern.compile("'(.)*?'");
		String cleaned = pattern.matcher(conditions).replaceAll("");
		pattern = Pattern.compile("([\\w]*\\.)");
		Matcher matcher = pattern.matcher(cleaned);
		while (matcher.find()) {
			String attr = matcher.group().replace(".", "");
			if (!joinedTables.containsKey(attr)) joinedTables.put(attr, Table.nameFor(attr, object));
		}
		return joinedTables;
	}

	public static String conditions(String conditions, Hermes object) {
		HashMap<String, String> joinedTables = tables(conditions, object);

		Pattern pattern;
		for (String attribute : joinedTables.keySet()) {
			pattern = Pattern.compile(attribute + ".");
			conditions = pattern.matcher(conditions).replaceAll(joinedTables.get(attribute) + ".");
			conditions += " and " + joinedTables.get(attribute) + "."
					+ object.getClass().getSimpleName().toLowerCase() + "_id" + "="
					+ Table.nameFor(object.getClass()) + ".id";
		}
		return conditions;
	}

}
