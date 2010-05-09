package adapters.MySql;

import java.util.HashMap;

import configuration.Configuration;

public class Mapping {

	private static HashMap<String, String>	javaToSql			= null;
	private static int											varcharLength	= Configuration.SqlConverterConfig.varcharLength;

	public static final String							INTEGER				= "integer";
	public static final String							BOOLEAN				= "boolean";
	public static final String							STRING				= "varchar(" + varcharLength + ")";
	public static final String							CHAR					= "char(1)";
	public static final String							BYTE					= "tinyint";
	public static final String							SHORT					= "smallint";
	public static final String							LONG					= "bigint";
	public static final String							FLOAT					= "float";
	public static final String							DATE					= "date";
	public static final String							DATETIME			= "datetime";

	public static String javaToSql(String type) {
		if (javaToSql == null) mapping();
		return javaToSql.get(type);
	}

	private static void mapping() {
		javaToSql = new HashMap<String, String>();
		javaToSql.put("int", INTEGER);
		javaToSql.put("Integer", INTEGER);
		javaToSql.put("String", STRING);
		javaToSql.put("char", CHAR);
		javaToSql.put("Character", CHAR);
		javaToSql.put("byte", BYTE);
		javaToSql.put("Byte", BYTE);
		javaToSql.put("short", SHORT);
		javaToSql.put("Short", SHORT);
		javaToSql.put("long", LONG);
		javaToSql.put("Long", LONG);
		javaToSql.put("float", FLOAT);
		javaToSql.put("Float", FLOAT);
		javaToSql.put("double", FLOAT);
		javaToSql.put("Double", FLOAT);
		javaToSql.put("Boolean", BOOLEAN);
		javaToSql.put("boolean", BOOLEAN);
		javaToSql.put("Date", DATE);
		javaToSql.put("Calendar", DATE);

	}
}
