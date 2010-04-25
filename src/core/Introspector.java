package core;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public class Introspector {

	public static Hermes classOf(Class<? extends Hermes> model) {
		try {
			return model.newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String className(Hermes klass) {
		return klass.getClass().getSimpleName();
	}

	public static Object getObject(String attribute, Hermes object) {
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

	public static void setField(Hermes object, Hermes fieldValue, Field field) {
		try {
			field.setAccessible(true);
			field.set(object, fieldValue);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setField(Hermes object, Set<?> fieldValue, Field field) {
		try {
			field.setAccessible(true);
			field.set(object, fieldValue);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Field fieldFor(Hermes klass, Attribute attribute) {
		try {
			return klass.getClass().getDeclaredField(attribute.getName());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field fieldFor(Hermes klass, String attribute) {
		try {
			return klass.getClass().getDeclaredField(attribute);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field[] fieldsOf(Hermes klass) {
		return klass.getClass().getDeclaredFields();
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

	public static Class<Hermes> klass(Hermes object, String attribute) {
		try {
			Field field = object.getClass().getDeclaredField(attribute);
			Class<?> type = field.getType();
			if (!type.equals(Set.class)) return hermesType(object, attribute);
			else return collectionTypeClass(object, attribute);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String table(Hermes object, String attribute) {
		try {
			return klass(object, attribute).newInstance().getTableName();
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

	public static Class<Hermes> hermesType(Hermes klass, String attribute) {
		Field field = fieldFor(klass, attribute);
		return (Class<Hermes>) field.getType();
	}

	// Private Methods
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
