package core;

import java.util.HashSet;
import java.util.Set;

public class Jointure extends Hermes {

	private int	parentId;
	private int	childId;

	public Jointure() {}

	public Jointure(Hermes model, String attribute) {
		this.tableName = Inflector.tableize(attribute, model, "jointure");
	}

	public boolean save(int parentId, int childId) {
		this.setParentId(parentId);
		this.setChildId(childId);
		return save();
	}

	public void clear(Hermes parent) {
		delete("parentId=" + parent.getId());
	}

	public boolean delete(String conditions) {
		return Updater.delete(conditions, this);
	}

	public static Set<Hermes> load(String attribute, Hermes object) {
		Jointure jointure = object.getManyToManyAssociations().get(attribute).getJointure();
		Set<Hermes> objects = new HashSet<Hermes>();
		Class<Hermes> klass = Introspector.collectionTypeClass(attribute, object);
		Set<Jointure> jointures = (Set<Jointure>) Finder.find(object.getId(), jointure);

		jointures.remove(null);
		for (Jointure join : jointures) {
			Hermes obj = Finder.find(join.getChildId(), klass);
			objects.add(obj);
		}
		return objects;
	}

	// Getters & setters
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setChildId(int childId) {
		this.childId = childId;
	}

	public int getChildId() {
		return childId;
	}
}
