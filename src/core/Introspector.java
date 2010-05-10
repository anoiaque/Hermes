package core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Introspector {

	public static Class<?> parent(Hermes object) {
		return object.getClass().getSuperclass();
	}

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

	public static String name(Class<?> parent) {
		return parent.getSimpleName();
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
		Field field = getField(attribute, object);
		Class<?> type = field.getType();

		if (!type.equals(Set.class)) return hermesClass(attribute, object);
		else return collectionTypeClass(attribute, object);
	}

	public static Class<Hermes> hermesClass(String attribute, Hermes klass) {
		Field field = field(attribute, klass);
		return (Class<Hermes>) field.getType();
	}

	public static Object get(String attribute, Hermes object) {
		try {
			Field field = getField(attribute, object);
			field.setAccessible(true);
			return field.get(object);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
		return getField(attribute.getName(), klass);
	}

	public static Field field(String attribute, Hermes klass) {
		return getField(attribute, klass);
	}

	public static List<Field> fields(Hermes klass) {
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(klass.getClass().getDeclaredFields()));
		fields.addAll(inheritedFields(klass));
		return fields;
	}

	public static boolean isDate(Object value){
		return value.getClass().getSimpleName().equals("Date");
	}
	
	public static boolean isTimestamp(Object value){
		return value.getClass().getSimpleName().equals("Timestamp");
	}


	// Just one level of inheritence for now . No recursive find
	public static List<Field> inheritedFields(Hermes klass) {
		Field[] inheriteds = {};
		Class<?> superclass = klass.getClass().getSuperclass();
		if (!superclass.equals(Hermes.class)) inheriteds = superclass.getDeclaredFields();
		return Arrays.asList(inheriteds);
	}

	public static String type(String attribute, Hermes klass) {
		Field field = getField(attribute, klass);
		Class<?> type = field.getType();
		if (!type.equals(Set.class)) return type.getSimpleName();
		else return Introspector.collectionTypeName(attribute, klass);
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

	public static Object invokeMethod(String name, Object param, Class<?> klass) {
		try {
			Method method = klass.getMethod(name, param.getClass());
			return method.invoke(klass, param);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// search for field in class and superclass
	private static Field getField(String attribute, Hermes object) {
		Field field = null;
		try {
			field = object.getClass().getDeclaredField(attribute);
		}
		catch (NoSuchFieldException e) {
			try {
				field = object.getClass().getSuperclass().getDeclaredField(attribute);
			}
			catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
		return field;
	}

	private static ParameterizedType collectionType(String attribute, Hermes klass) {
		ParameterizedType ptype = null;
		try {
			Field field = getField(attribute, klass);
			ptype = (ParameterizedType) field.getGenericType();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ptype;
	}

}
