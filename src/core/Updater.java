package core;

import adaptors.Adaptor;

public class Updater {

	public static boolean save(Hermes object) {
		if (!object.isNewRecord()) return update(object);
		object.loadAttributes();
		
		boolean saved = Adaptor.get().save(object);
		object.getAssociations().saveHasOneAssociations();
		object.getAssociations().saveHasManyAssociations();
		object.getAssociations().saveManyToManyAssociations();
		return saved;
	}

	public static boolean update(Hermes object) {
		object.loadAttributes();
	
		boolean updated = Adaptor.get().update(object);
		object.getAssociations().saveHasOneAssociations();
		object.getAssociations().saveHasManyAssociations();
		object.getAssociations().saveManyToManyAssociations();
		return updated;
	}

	public static boolean delete(Hermes object) {
		object.getAssociations().cascadeDelete();
		boolean deleted = Adaptor.get().delete(object);
		if (deleted) object.setId(0);
		return deleted;
	}

	public static boolean delete(Hermes object, String conditions) {
		object.getAssociations().cascadeDelete();
		boolean deleted = Adaptor.get().delete(object, conditions);
		if (deleted) object.setId(0);
		return deleted;
	}
}
