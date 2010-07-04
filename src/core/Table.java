package core;

public class Table {

	public static String name(Hermes object) {
		Class<?> parent = object.getClass().getSuperclass();
		
		if (parent.equals(Hermes.class)) return Inflector.tableize(object);
		return Inflector.tableize(Introspector.instanciate((Class<? extends Hermes>) parent));
	}
	
	public static String foreignKeyName(Hermes object){
		Class<?> parent = object.getClass().getSuperclass();
		
		if (parent.equals(Hermes.class)) return Inflector.foreignKey(object);
		return Inflector.foreignKey(Introspector.name(parent));
	}

}
