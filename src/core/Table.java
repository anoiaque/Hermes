package core;

public class Table {

	public static String nameFor(String attribute, Hermes object) {
		String name = Introspector.typeName(object, attribute);
		return Inflector.pluralize(name).toUpperCase();
	}

	public static String joinTableNameFor(Hermes model, String attribute) {
		String parentName = model.getTableName();
		String childName = nameFor(attribute, model);
		return parentName + "_" + childName;
	}

	public static String nameFor(Class<? extends Hermes> model) {
		try {
			return (String) model.newInstance().getTableName();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
