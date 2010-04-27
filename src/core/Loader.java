package core;

import java.util.Set;

public class Loader {

	public static void loadAssociations(Set<?> objects) {
		for (Object object : objects) {
			loadHasOneAssociations((Hermes) object);
			loadManyToManyAssociations((Hermes) object);
			loadHasManyAssociations((Hermes) object);
		}
	}

	// Private methods
	private static void loadHasOneAssociations(Hermes object) {
		String foreignKey;
		Class<Hermes> klass;
		Hermes value;

		for (String attribute : object.getHasOneAssociations().keySet()) {
			foreignKey = Inflector.foreignKey(Introspector.name(object));
			klass = Introspector.hermesClass(attribute, object);
			value = (Hermes) Finder.findFirst(foreignKey + "=" + object.getId(), klass);
			Introspector.set(attribute, value, object);
		}
	}

	private static void loadHasManyAssociations(Hermes object) {
		String foreignKey;
		Class<Hermes> klass;
		Set<Hermes> set;

		for (String attribute : object.getHasManyAssociations().keySet()) {
			foreignKey = Inflector.foreignKey(Introspector.name(object));
			klass = Introspector.collectionTypeClass(attribute, object);
			set = (Set<Hermes>) Finder.find(foreignKey + "=" + object.getId(), klass);
			BelongsTo.belongsTo(set, object);
			Introspector.set(attribute, set, object);
		}
	}

	private static void loadManyToManyAssociations(Hermes object) {
		Set<Hermes> objects;

		for (String attribute : object.getManyToManyAssociations().keySet()) {
			objects = Jointure.load(attribute, object);
			Introspector.set(attribute, objects, object);
		}
	}
}
