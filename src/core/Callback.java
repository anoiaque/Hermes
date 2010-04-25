package core;

public class Callback {

	public static void beforeValidate(Hermes object) {
		object.beforeValidate();
	}

	public static void beforeSave(Hermes object) {
		object.beforeSave();
	}

	public static void afterSave(Hermes object) {
		object.afterSave();
	}

	public static void afterUpdate(Hermes object) {
		object.afterUpdate();
	}

	public static void beforeUpdate(Hermes object) {
		object.beforeUpdate();
	}

	public static void beforeCreate(Hermes object) {
		object.beforeCreate();
	}

	public static void afterCreate(Hermes object) {
		object.afterCreate();
	}

	public static void beforeDelete(Hermes object) {
		object.beforeDelete();
	}

	public static void afterDelete(Hermes object) {
		object.setId(0);
		object.afterDelete();
	}

}
