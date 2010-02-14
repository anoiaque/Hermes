package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public class Introspector {

	public static Field fieldFor(Hermes klass, Attribute attribute) {
		try {
			return klass.getClass().getDeclaredField(attribute.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field[] fieldsOf(Hermes klass) {
		return klass.getClass().getDeclaredFields();
	}

	public static String className(Hermes klass) {
		return klass.getClass().getSimpleName();
	}

	public static String typeName(Hermes klass, String attribute) {
		try {
			Field field = klass.getClass().getDeclaredField(attribute);
			Class<?> type = field.getType();
			if (!type.equals(Set.class)) return type.getSimpleName();
			else return Introspector.collectionTypeName(klass, attribute);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String collectionTypeName(Hermes klass, String attribute) {
		ParameterizedType pType = collectionType(klass, attribute);
		if (pType == null) return null;
		return ((Class<?>) pType.getActualTypeArguments()[0]).getSimpleName();
	}

	public static Class<Hermes> collectionTypeClass(Hermes klass, String attribute) {
		ParameterizedType pType = collectionType(klass, attribute);
		if (pType == null) return null;
		return ((Class<Hermes>) pType.getActualTypeArguments()[0]);
	}

	public static Object getObject(String attribute, Hermes klass) {
		Field field;
		try {
			field = klass.getClass().getDeclaredField(attribute);
			field.setAccessible(true);
			return field.get(klass);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ParameterizedType collectionType(Hermes klass, String attribute) {
		ParameterizedType ptype = null;
		try {
			Field field = klass.getClass().getDeclaredField(attribute);
			ptype = (ParameterizedType) field.getGenericType();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ptype;
	}
}