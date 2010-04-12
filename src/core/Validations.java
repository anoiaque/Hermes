package core;

public class Validations {

	public enum Option {
		ALLOW_NULL
	}

	public static boolean validatePresenceOf(String attribute, Hermes object) {
		return Introspector.getObject(attribute, object) != null;
	}

	public static boolean validateSizeOf(String attribute, int min, int max, boolean allowNil,
			Hermes object) {
		String value = (String) object.getAttribute(attribute).getValue();
		if (allowNil && value == null) return true;
		if (value == null) return false;
		return (value.length() >= min && value.length() <= max);
	}

}
