package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public class Introspector {

	public static Hermes instanciate(Class<? extends Hermes> model) {
		try {
			return model.newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String name(Hermes klass) {
		return klass.getClass().getSimpleName();
	}

	public static String table(String attribute, Hermes object) {
		try {
			return klass(attribute, object).newInstance().getTableName();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<Hermes> klass(String attribute, Hermes object) {
		try {
			Field field = object.getClass().getDeclaredField(attribute);
			Class<?> type = field.getType();
			if (!type.equals(Set.class)) return hermesClass(attribute, object);
			else return collectionTypeClass(attribute, object);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<Hermes> hermesClass(String attribute, Hermes klass) {
		Field field = field(attribute, klass);
		return (Class<Hermes>) field.getType();
	}

	public static Object get(String attribute, Hermes object) {
		Field field;
		try {
			field = object.getClass().getDeclaredField(attribute);
			field.setAccessible(true);
			return field.get(object);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void set(String attribute, Object value, Hermes object) {
		try {
			Field field = field(attribute, object);
			field.setAccessible(true);
			field.set(object, value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void set(Attribute attribute, Object value, Hermes object) {
		try {
			Field field = field(attribute, object);
			field.setAccessible(true);
			field.set(object, value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Field field(Attribute attribute, Hermes klass) {
		try {
			return klass.getClass().getDeclaredField(attribute.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field field(String attribute, Hermes klass) {
		try {
			return klass.getClass().getDeclaredField(attribute);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field[] fields(Hermes klass) {
		return klass.getClass().getDeclaredFields();
	}

	public static String type(String attribute, Hermes klass) {
		try {
			Field field = klass.getClass().getDeclaredField(attribute);
			Class<?> type = field.getType();
			if (!type.equals(Set.class)) return type.getSimpleName();
			else return Introspector.collectionTypeName(attribute, klass);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String collectionTypeName(String attribute, Hermes klass) {
		ParameterizedType pType = collectionType(attribute, klass);
		if (pType == null) return null;
		return ((Class<?>) pType.getActualTypeArguments()[0]).getSimpleName();
	}

	public static Class<Hermes> collectionTypeClass(String attribute, Hermes klass) {
		ParameterizedType pType = collectionType(attribute, klass);
		if (pType == null) return null;
		return ((Class<Hermes>) pType.getActualTypeArguments()[0]);
	}

	private static ParameterizedType collectionType(String attribute, Hermes klass) {
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
