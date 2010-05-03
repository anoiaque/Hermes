package adapters.MySql;

import core.Attribute;

public class TypeCast {

	public static Object value(Attribute attribute) {
		if (attribute.getSqlType().equals(Mapping.BOOLEAN)) return booleanToSql(attribute);
		return attribute.getValue();
	}

	public static int booleanToSql(Attribute attribute) {
		return (attribute.getValue().equals(new Boolean(true))) ? 1 : 0;
	}
}
