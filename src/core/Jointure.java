package core;

import java.util.HashSet;
import java.util.Set;

import adapters.Adapter;
import adapters.MySql.SqlBuilder;

public class Jointure extends Hermes {

	private long	parentId;
	private long	childId;

	public Jointure() {}

	public Jointure(Hermes model, String attribute) {
		this.tableName = Inflector.tableize(attribute, model, "jointure");
		Adapter.get().execute(SqlBuilder.build("jointure", tableName), this);
	}

	public boolean save(long parentId, long childId) {
		this.parentId = parentId;
		this.childId = childId;
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
		Class<Hermes> klass = Introspector.collectionTypeClass(attribute,object);
		Set<Jointure> jointures = (Set<Jointure>) Finder.find(object.getId(), jointure);

		jointures.remove(null);
		for (Jointure join : jointures) {
			Hermes obj = Finder.find(join.getChildId(), klass);
			objects.add(obj);
		}
		return objects;
	}

	// Getters & setters
	public long getParentId() {
		return parentId;
	}

	public void setParentId(int leftId) {
		this.parentId = leftId;
	}

	public long getChildId() {
		return childId;
	}

	public void setChildId(int rightId) {
		this.childId = rightId;
	}
}
