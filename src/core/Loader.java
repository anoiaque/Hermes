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
		for (String attribute : object.getHasOneAssociations().keySet()) {
			String foreignKey = Inflector.foreignKeyName(Introspector.className(object));
			Class<Hermes> klass = Introspector.hermesType(object, attribute);
			Field field = Introspector.fieldFor(object, attribute);
			Hermes value = (Hermes) Finder.findFirst(foreignKey + "=" + object.getId(), klass);
			Introspector.setField(object, value, field);
		}
	}

	private static void loadHasManyAssociations(Hermes object) {
		for (String attribute : object.getHasManyAssociations().keySet()) {
			String fk = Inflector.foreignKeyName(Introspector.className(object));
			Class<Hermes> klass = Introspector.collectionTypeClass(object, attribute);
			Set<Hermes> set = (Set<Hermes>) Finder.find(fk + "=" + object.getId(), klass);
			BelongsTo.belongsTo(set, object);
			Field field = Introspector.fieldFor(object, attribute);
			Introspector.setField(object, set, field);
		}
	}

	private static void loadManyToManyAssociations(Hermes object) {
		for (String attribute : object.getManyToManyAssociations().keySet()) {
			Field field = Introspector.fieldFor(object, attribute);
			Set<Hermes> objects = Jointure.objectsFor(attribute, object);
			Introspector.setField(object, objects, field);
		}
	}
}
