package adapters.MySql;

import java.util.HashMap;

import configuration.Configuration;

public class Mapping {

	private static HashMap<String, String>	javaToSql			= null;
	private static int											varcharLength	= Configuration.SqlConverterConfig.varcharLength;

	public static String javaToSql(String type) {
		if (javaToSql == null) mapping();
		return javaToSql.get(type);
	}

	private static void mapping() {
		javaToSql = new HashMap<String, String>();
		javaToSql.put("int", "integer");
		javaToSql.put("Integer", "integer");
		javaToSql.put("String", "varchar(" + varcharLength + ")");
		javaToSql.put("char", "char(1)");
		javaToSql.put("Character", "char(1)");
		javaToSql.put("byte", "tinyint");
		javaToSql.put("Byte", "tinyint");
		javaToSql.put("short", "smallint");
		javaToSql.put("Short", "smallint");
		javaToSql.put("long", "bigint");
		javaToSql.put("Long", "bigint");
		javaToSql.put("float", "float");
		javaToSql.put("Float", "float");
		javaToSql.put("double", "float");
		javaToSql.put("Double", "float");
		javaToSql.put("Boolean", "boolean");
		javaToSql.put("boolean", "boolean");
	}
}
