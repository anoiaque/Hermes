package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inflector {

	public static String pluralize(String word) {
		return (new Inflector()).new Plural(word).get();
	}

	public static String foreignKey(String modelName) {
		return modelName.toLowerCase() + "_id";
	}

	public static String foreignKey(Hermes model) {
		return Introspector.name(model).toLowerCase() + "_id";
	}

	public static String tableize(Hermes model) {
		return pluralize(Introspector.name(model)).toUpperCase();
	}

	public static String tableize(String attribute, Hermes object) {
		String name = Introspector.type(attribute, object);
		return Inflector.pluralize(name).toUpperCase();
	}

	public static String tableize(String attribute, Hermes object, String jointure) {
		String parentName = object.getTableName();
		String childName = tableize(attribute, object);
		return parentName + "_" + childName;
	}

	public static String tableize(Class<? extends Hermes> model) {
		try {
			return model.newInstance().getTableName();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private class Plural {

		private Pattern	pattern;
		private Matcher	matcher;
		private String	plural;

		public String get() {
			return plural;
		}

		public Plural(String word) {
			this.plural = plural(word);
		}

		private String plural(String word) {

			if (uncountable(word)) return word;
			if (word.toLowerCase().equals("ox")) return "oxen";
			if (word.toLowerCase().equals("person")) return "people";
			if (word.toLowerCase().equals("man")) return "men";
			if (word.toLowerCase().equals("woman")) return "women";
			if (word.toLowerCase().equals("sex")) return "sexes";
			if (word.toLowerCase().equals("child")) return "children";

			if (Pattern.matches("(?i)(s$)", word)) return word;
			if (match(word, "(?i)(ax|test)is$")) return matcher.group().replace("is", "es");
			if (match(word, "(?i)(octop|vir)us$")) return matcher.group().replace("us", "i");
			if (match(word, "(?i)(alias|status|bus|tomato|buffalo)$")) return matcher.group() + "es";
			if (match(word, "(?i)(.*)((matr|vert|ind)(?:ix|ex)$)")) return matcher.group(1)
					+ matcher.group(3) + "ices";
			if (match(word, "(?i)(.*)([ti]um$)")) return replacement("um", "a");
			if (match(word, "(?i)(.*)(sis$)")) return replacement("sis", "ses");
			if (match(word, "(?i)(.*)(([lr])f$)")) return replacement("f", "ves");
			if (match(word, "(?i)(.*)(hive$)")) return matcher.group(1) + "hives";
			if (match(word, "(?i)(.*)(([^aeiouy]|qu)y$)")) return replacement("y", "ies");
			if (match(word, "(?i)(.*)((x|ch|ss|sh)$)")) return matcher.group(1) + matcher.group(2) + "es";
			if (match(word, "(?i)(.*)(([m|l])ouse$)")) return replacement("ouse", "ices");
			if (match(word, "(?i)(.*)((quiz)$)")) return replacement("quiz", "quizzes");

			return word + "s";
		}

		private String replacement(String singular, String plural) {
			return matcher.group(1) + matcher.group(2).replace(singular, plural);
		}

		private boolean uncountable(String word) {
			return word.matches("equipment|information|rice|money|species|series|sheep");
		}

		private boolean match(String word, String regexp) {
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(word);
			return matcher.find();
		}
	}
}
