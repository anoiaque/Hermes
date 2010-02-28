package core;

import java.lang.reflect.Field;
import java.util.Set;

public class Loader {

	public static void loadAssociations(Set<?> objects) {
		for (Object object : objects) {
			loadHasOneAssociations((Hermes) object);
			loadManyToManyAssociations((Hermes) object);
			loadHasManyAssociations((Hermes) object);
		}
	}

	private static void loadHasOneAssociations(Hermes object) {
		String foreignKey;
		Class<Hermes> klass;
		Field field;
		Hermes value;

		for (String attribute : object.getHasOneAssociations().keySet()) {
			foreignKey = Inflector.foreignKeyName(Introspector.className(object));
			klass = Introspector.hermesType(object, attribute);
			field = Introspector.fieldFor(object, attribute);
			value = (Hermes) Finder.findFirst(foreignKey + "=" + object.getId(), klass);
			Introspector.setField(object, value, field);
		}
	}

	private static void loadHasManyAssociations(Hermes object) {
		String foreignKey;
		Class<Hermes> klass;
		Set<Hermes> set;
		Field field;

		for (String attribute : object.getHasManyAssociations().keySet()) {
			foreignKey = Inflector.foreignKeyName(Introspector.className(object));
			klass = Introspector.collectionTypeClass(object, attribute);
			set = (Set<Hermes>) Finder.find(foreignKey + "=" + object.getId(), klass);
			BelongsTo.belongsTo(set, object);
			field = Introspector.fieldFor(object, attribute);
			Introspector.setField(object, set, field);
		}
	}

	private static void loadManyToManyAssociations(Hermes object) {
		Field field;
		Set<Hermes> objects;
		
		for (String attribute : object.getManyToManyAssociations().keySet()) {
			field = Introspector.fieldFor(object, attribute);
			objects = Jointure.objectsFor(attribute, object);
			Introspector.setField(object, objects, field);
		}
	}
}
