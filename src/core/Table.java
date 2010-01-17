package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public class Table {

	public static String nameFor(Class<? extends Hermes> model) {
		String tableName = "";
		try {
			tableName = (String) model.getMethod("getTableName").invoke(model.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableName;
	}

	public static String nameFor(String attribute, Hermes object) {
		try {
			Field field = object.getClass().getDeclaredField(attribute);
			Class<?> type = field.getType();

			if (!type.equals(Set.class)) {
				return Inflector.pluralize(type.getSimpleName()).toUpperCase();
			} else {
				ParameterizedType set = (ParameterizedType) field.getGenericType();
				String setType = (((Class<?>) set.getActualTypeArguments()[0]).getSimpleName());
				return Inflector.pluralize(setType.toUpperCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
