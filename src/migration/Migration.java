package migration;

import adapters.MySql.Mapping;
import core.Attribute;
import core.Hermes;
import core.Introspector;

public class Migration {

	public static String tableDefinition(Class<? extends Hermes> model) {
		String sql;
		Hermes klass = Introspector.instanciate(model);
		klass.loadAttributes();
		sql = "create table " + klass.getTableName() + "(";
		sql += idColumnDefinition();
		for (Attribute attribute : klass.getAttributes())
			sql += ",\n" + columnDefinition(attribute);
		sql += ");";
		return sql;
	}

	public static String idColumnDefinition() {
		return "id int primary key auto_increment";
	}

	public static String columnDefinition(Attribute attribute) {
		String sql = attribute.getName() + " ";
		sql += attribute.getSqlType();
		return sql;
	}

	public static String foreignKeyDefinition(String fkName) {
		return fkName + " " + Mapping.INTEGER;
	}
}
