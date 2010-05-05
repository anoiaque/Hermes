package core;

import java.util.Set;
import java.util.regex.Pattern;

import adapters.Adapter;
import adapters.MySql.ObjectBuilder;
import adapters.MySql.SqlBuilder;

public class Validations {

	public enum Option {
		ALLOW_NULL
	}

	public static boolean validatePresenceOf(String attribute, Hermes object) {
		return Introspector.get(attribute, object) != null;
	}

	public static boolean validateSizeOf(String attribute, int min, int max, boolean allowNull,
			Hermes object) {
		String value = (String) object.getAttribute(attribute).getValue();
		if (allowNull && value == null) return true;
		if (value == null) return false;
		return (value.length() >= min && value.length() <= max);
	}

	// Warn : Can't use Hermes finder here cause of Invalid access of stack red
	// zone on mac OS X JRE 6
	public static boolean validateUniquenessOf(String attribute, Hermes object) {
		Object value = Introspector.get(attribute, object);
		return !find(attribute, value, object);
	}

	public static boolean validateFormatOf(String attribute, Pattern pattern, boolean allowNull,
			Hermes object) {
		String value = (String) object.getAttribute(attribute).getValue();
		if (allowNull && value == null) return true;
		return pattern.matcher(value).find();
	}

	private static boolean find(String attribute, Object value, Hermes object) {
		Adapter adaptor = Adapter.get();
		String conditions = attribute + "='" + value + "'";
		String sql = SqlBuilder.build("select", "*", conditions, object, null);
		Set<?> objects = ObjectBuilder.toObjects(adaptor.finder(sql), object.getClass());
		return objects.size() > 0;
	}

}
