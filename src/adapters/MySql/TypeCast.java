package adapters.MySql;

import core.Attribute;

public class TypeCast {

	public static Object value(Attribute attribute) {

		if (attribute.getSqlType().equals(Mapping.BOOLEAN)) return booleanToSql(attribute);
		if (attribute.getValue() != null && attribute.getValue().getClass().equals(String.class)) return sanitize((String) attribute
				.getValue());

		return attribute.getValue();
	}

	public static int booleanToSql(Attribute attribute) {
		return (attribute.getValue().equals(new Boolean(true))) ? 1 : 0;
	}

	private static Object sanitize(String string) {
		string = string.replace("'", "''");
		return string;
	}

}
