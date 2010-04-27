package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Hermes {

	private int														id						= 0;
	protected String											tableName;
	private List<Attribute>								attributes		= new ArrayList<Attribute>();
	private Associations									associations	= new Associations(this);
	private HashMap<String, List<Error>>	errors				= new HashMap<String, List<Error>>();

	public Hermes() {
		this.tableName = Inflector.tableize(this);
		loadAttributes();
		Associations();
		Callback.beforeValidate(this);
		Validations();
	}

	public boolean save() {
		if (!isValid()) return false;
		return Updater.save(this);
	}

	public boolean saveWithoutValidation() {
		return Updater.save(this);
	}

	public boolean delete() {
		return Updater.delete(this);
	}

	public static boolean delete(String conditions, Class<? extends Hermes> model) {
		Set<Hermes> objects = (Set<Hermes>) find(conditions, model);
		boolean deleted = true;
		for (Hermes object : objects)
			deleted = deleted && Updater.delete(object);
		return deleted;
	}

	public static boolean deleteAll(Class<? extends Hermes> model) {
		return Updater.deleteAll(model);
	}

	public boolean deleteAll() {
		return deleteAll(this.getClass());
	}

	public static Hermes create(HashMap<String, Object> values, Class<? extends Hermes> model) {
		return Updater.create(values, model);
	}

	public Hermes create(HashMap<String, Object> values) {
		return create(values, this.getClass());
	}

	public static int count(Class<? extends Hermes> model) {
		return Finder.count(model);
	}

	public static Hermes find(long id, Class<? extends Hermes> model) {
		return Finder.find(id, model);
	}

	public Hermes find(long id) {
		return Finder.find(id, this.getClass());
	}

	public static Set<?> find(String conditions, Class<? extends Hermes> model) {
		return Finder.find(conditions, model);
	}

	public Set<?> find(String conditions) {
		return Finder.find(conditions, this.getClass());
	}

	public static Set<?> find(String select, String conditions, Class<? extends Hermes> model) {
		return Finder.find(select, conditions, model);
	}

	public Set<?> find(String select, String conditions) {
		return Finder.find(select, conditions, this.getClass());
	}

	public static Set<?> findAll(Class<? extends Hermes> model) {
		return Finder.find("*", null, model);
	}

	public Set<?> findAll() {
		return Finder.find("*", null, this.getClass());
	}

	public static Hermes findFirst(String conditions, Class<? extends Hermes> model) {
		return Finder.findFirst(conditions, model);
	}

	public Hermes findFirst(String conditions) {
		return Finder.findFirst(conditions, this.getClass());
	}

	public static Set<?> findBySql(String sql, Class<? extends Hermes> model) {
		return Finder.findBySql(sql, model);
	}

	public Set<?> findBySql(String sql) {
		return Finder.findBySql(sql, this.getClass());
	}

	public boolean isNewRecord() {
		return (id == 0);
	}

	public Hermes reload() {
		return find(this.id);
	}

	public void hasMany(String attribute, String dependency) {
		associations.hasMany(attribute, dependency);
	}

	public void hasMany(String attribute) {
		hasMany(attribute, "");
	}

	public void hasOne(String attribute, String dependency) {
		associations.hasOne(attribute, dependency);
	}

	public void hasOne(String attribute) {
		hasOne(attribute, "");
	}

	public void manyToMany(String attribute, String dependency) {
		associations.manyToMany(attribute, dependency);
	}

	public void manyToMany(String attribute) {
		manyToMany(attribute, "");
	}

	public void belongsTo(Hermes object) {
		associations.belongsTo(object);
	}

	public void loadAttributes() {
		this.attributes = (ArrayList<Attribute>) Attribute.load(this);
	}

	public static boolean execute(String sql) {
		return Updater.executeSql(sql);
	}

	public Attribute getAttribute(String name) {
		loadAttributes();
		for (Attribute attribute : attributes)
			if (attribute.getName().equals(name)) return attribute;
		return null;
	}

	public void validatePresenceOf(String attribute) {
		if (Validations.validatePresenceOf(attribute, this)) return;
		addError(attribute, Error.Symbol.PRESENCE, null);
	}

	public void validatePresenceOf(String attribute, String message) {
		if (Validations.validatePresenceOf(attribute, this)) return;
		addError(attribute, Error.Symbol.PRESENCE, message);
	}

	public void validateSizeOf(String attribute, int min, int max, boolean allowNull) {
		if (Validations.validateSizeOf(attribute, min, max, allowNull, this)) return;
		addError(attribute, Error.Symbol.SIZE, null);
	}

	public void validateSizeOf(String attribute, int min, int max, boolean allowNull, String message) {
		if (Validations.validateSizeOf(attribute, min, max, false, this)) return;
		addError(attribute, Error.Symbol.SIZE, message);
	}

	public void validateUniquenessOf(String attribute) {
		if (Validations.validateUniquenessOf(attribute, this)) return;
		addError(attribute, Error.Symbol.UNIQUENESS, null);
	}

	public void validateUniquenessOf(String attribute, String message) {
		if (Validations.validateUniquenessOf(attribute, this)) return;
		addError(attribute, Error.Symbol.UNIQUENESS, message);
	}

	public void validateFormatOf(String attribute, Pattern pattern, boolean allowNull) {
		if (Validations.validateFormatOf(attribute, pattern, allowNull, this)) return;
		addError(attribute, Error.Symbol.FORMAT, null);
	}

	public void validateFormatOf(String attribute, Pattern pattern, boolean allowNull, String message) {
		if (Validations.validateFormatOf(attribute, pattern, allowNull, this)) return;
		addError(attribute, Error.Symbol.FORMAT, message);
	}

	public boolean isValid() {
		errors.clear();
		Callback.beforeValidate(this);
		Validations();
		return errors.isEmpty();
	}

	public void addError(String attribute, Error.Symbol symbol, String message) {
		Error error;
		if (message == null) error = new Error(symbol);
		else error = new Error(symbol, message);
		errors.put(attribute, Error.add(error, errors.get(attribute)));
	}

	public void addError(String attribute, String message) {
		Error error = new Error(Error.Symbol.PARTICULAR, message);
		errors.put(attribute, Error.add(error, errors.get(attribute)));
	}

	protected void Validations() {}

	protected void Associations() {}

	protected void beforeValidate() {}

	protected void beforeSave() {}

	protected void afterSave() {}

	protected void beforeUpdate() {}

	protected void afterUpdate() {}

	protected void beforeCreate() {}

	protected void afterCreate() {}

	protected void beforeDelete() {}

	protected void afterDelete() {}

	// Getters & Setters
	public HashMap<String, ManyToMany> getManyToManyAssociations() {
		return associations.getManyToManyAsociations();
	}

	public HashMap<String, HasOne> getHasOneAssociations() {
		return associations.getHasOneAssociations();
	}

	public HashMap<String, HasMany> getHasManyAssociations() {
		return associations.getHasManyAssociations();
	}

	public void setTableName(String table_name) {
		this.tableName = table_name;
	}

	public String getTableName() {
		return this.tableName;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Associations getAssociations() {
		return associations;
	}

	public void setAssociations(Associations relations) {
		this.associations = relations;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public HashMap<String, List<Error>> getErrors() {
		return errors;
	}

	public void setErrors(HashMap<String, List<Error>> errors) {
		this.errors = errors;
	}

}
