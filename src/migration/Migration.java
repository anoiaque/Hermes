package migration;

import core.Attribute;
import core.Hermes;
import core.Introspector;

public class Migration {

	public static String tableDefinition(Class<? extends Hermes> model) {
		String sql;
		Hermes klass = Introspector.classOf(model);
		sql = "create table " + klass.getTableName() + "(";
		sql += idColumnDefinition();
		for (Attribute attribute : klass.getAttributes())
			sql += ",\n" + columnDefinition(attribute);
		sql += ");";
		return sql;
	}

	public static String idColumnDefinition() {
		return "id bigint primary key auto_increment";
	}

	public static String columnDefinition(Attribute attribute) {
		String sql = attribute.getName() + " ";
		sql += attribute.getSqlType();
		return sql;
	}
}
