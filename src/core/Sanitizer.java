package core;

import adapters.MySql.TypeCast;

public class Sanitizer {

	public static String sanitize(String conditions, Object[] values) {
		String[] conds = conditions.split("\\?");
		String sanitizedConditions = "";

		for (int i = 0; i < conds.length; i++)
			sanitizedConditions += " " + conds[i] + " " + quote(values[i]);
		return sanitizedConditions;
	}

	private static String quote(Object value) {
		return "'" + TypeCast.toSql(value).toString() + "'";
	}

}
