package adaptors.MySql;

import java.util.HashMap;

import configuration.Configuration;

public class Mapping {

  private static HashMap<String, String> javaToSql = null;

  public static String javaToSql(String javaType) {
    if (javaToSql == null) setjavaToSqlMapping();
    return javaToSql.get(javaType);
  }

  private static void setjavaToSqlMapping() {
    javaToSql = new HashMap<String, String>();
    javaToSql.put("int", "int");
    javaToSql.put("Integer", "int");
    javaToSql.put("String", "varchar(" + Configuration.SqlConverterConfig.varcharLength + ")");
  }
}
