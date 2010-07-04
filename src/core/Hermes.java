package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Philippe Cantin
 * @version 1.0 BŽta
 * @since 2010 Copyright Cantin Philippe under MIT-License
 */
public class Hermes {

	private int														id						= 0;
	protected String											tableName;
	private List<Attribute>								attributes		= new ArrayList<Attribute>();
	private Associations									associations	= new Associations(this);
	private HashMap<String, List<Error>>	errors				= new HashMap<String, List<Error>>();

	/**
	 * Primary constructor of any class which inherit from Hermes Actions : -
	 * infer table name - load attributes - call Associations constructor - call
	 * Validations constructor
	 */
	public Hermes() {
		this.tableName = Table.name(this);
		Associations();
		loadAttributes();
		Callback.beforeValidate(this);
		Validations();
	}

	/**
	 * Save object in database if and only if no validation fail
	 * 
	 * @return True if object is saved.False if error occured
	 */
	public boolean save() {
		if (!isValid()) return false;
		return Updater.save(this);
	}

	/**
	 * Save object in database without calling validations
	 * 
	 * @return True if object is saved.False if error occured
	 */
	public boolean saveWithoutValidation() {
		return Updater.save(this);
	}

	/**
	 * Delete object in database. Object have now his id set to 0.
	 * 
	 * @return True if deleted, else False.
	 */
	public boolean delete() {
		return Updater.delete(this);
	}

	/**
	 * Delete objects of a class from database with conditions
	 * 
	 * @param conditions
	 *          : conditions on record(s) to delete
	 * @param model
	 *          : Class extending Hermes of the objects to delete.
	 * @return True if all deleted, else False.
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * Hermes.delete("name = 'anonymous' and address.street='rue de Brest'",Person.class)
	 * /code>
	 *</pre>
	 */
	public static boolean delete(String conditions, Class<? extends Hermes> model) {
		Set<Hermes> objects = (Set<Hermes>) find(conditions, model);
		boolean deleted = true;

		for (Hermes object : objects)
			deleted = deleted && Updater.delete(object);
		return deleted;
	}

	/**
	 * Delete all objects of a given class.
	 * 
	 * @param model
	 *          : Class extending Hermes of the objects to delete.
	 * @return True if all deleted, else False.
	 */
	public static boolean deleteAll(Class<? extends Hermes> model) {
		return Updater.deleteAll(model);
	}

	/**
	 * Instance method . Delete all objects of the class from which this method is
	 * called
	 * 
	 * @return True if all deleted, else False.
	 */
	public boolean deleteAll() {
		return deleteAll(this.getClass());
	}

	/**
	 * Create object from hashmap of attribute-values. Perform an new with
	 * attribute-values given and save it in the database.
	 * 
	 * @param values
	 *          : hashMap of attribute_name => value
	 * @param model
	 *          : class of object
	 * @return The object created. Null if error(s) occur.
	 */
	public static Hermes create(HashMap<String, Object> values, Class<? extends Hermes> model) {
		return Updater.create(values, model);
	}

	/**
	 * Instance method. Create object from hashmap of attribute-values. Perform an
	 * new with attribute-values given and save it in the database.
	 * 
	 * @param values
	 *          : hashMap of attribute_name => value
	 * @return The object created. Null if error(s) occur.
	 */
	public Hermes create(HashMap<String, Object> values) {
		return create(values, this.getClass());
	}

	/**
	 * Count all objects of the given class in the database. Must be used instead
	 * of findAll().size() for quicker response.
	 * 
	 * @param model
	 *          : class of objects extending Hermes
	 * @return number of objects.
	 */
	public static int count(Class<? extends Hermes> model) {
		return Finder.count(model);
	}

	/**
	 * Instance method for count.Count all objects of this class in the database.
	 * 
	 * @return number of objects.
	 */
	public int count() {
		return Finder.count(this.getClass());
	}

	/**
	 * Count all objects of the given class with conditions in the database. Must
	 * be used instead of findAll(conditons).size() for quicker response.
	 * 
	 * @param model
	 *          : class of objects extending Hermes
	 * @return number of objects.
	 */
	public static int count(String conditions, Class<? extends Hermes> model) {
		return Finder.count(conditions, model);
	}

	/**
	 * Instance method for count.Count all objects of the class in the database
	 * with conditions on objects
	 * 
	 * @return number of objects.
	 */
	public int count(String conditions) {
		return Finder.count(conditions, this.getClass());
	}

	// TODO check for changes in associations attributes
	/**
	 * Check if an object is different from persistance state
	 * 
	 * @return True/False
	 */
	public boolean isChanged() {
		Hermes old = find(this.getId());
		for (Attribute attribute : this.getAttributes()) {
			Object newValue = Introspector.get(attribute.getName(), this);
			Object oldValue = Introspector.get(attribute.getName(), old);
			if (newValue != null && !newValue.equals(oldValue)) return true;
		}
		return false;
	}

	/**
	 * Toggle value of a boolean type attribute.
	 * 
	 * @param attribute
	 *          : Name of the attribute
	 */
	public void toggle(String attribute) {
		boolean value = !Introspector.get(attribute, this).equals(new Boolean(true));
		Introspector.set(attribute, value, this);
	}

	/**
	 * Check if object exists in database
	 * 
	 * @return True/False
	 */
	public boolean exists() {
		return find(this.getId()) != null;
	}

	/**
	 * Check if object exists in database with given conditions on object
	 * 
	 * @param conditions
	 * @return True/False
	 */
	public boolean exists(String conditions) {
		return find(conditions) != null;
	}

	/**
	 * Find object by id
	 * 
	 * @param id
	 * @param model
	 *          : Class of object
	 * @return Object found or null
	 */
	public static Hermes find(int id, Class<? extends Hermes> model) {
		return Finder.find(id, model);
	}

	/**
	 * Find objects by list of ids
	 * 
	 * @param ids
	 *          : List of ids
	 * @param model
	 *          : Class of object
	 * @return Set of objects found or null
	 */
	public static Set<?> find(List<Integer> ids, Class<? extends Hermes> model) {
		return Finder.find(ids, model);
	}

	/**
	 * Find object by id
	 * 
	 * @param id
	 * @return Object found or null
	 */
	public Hermes find(int id) {
		return Finder.find(id, this.getClass());
	}

	/**
	 * Instance method for find objects by list of ids on instance class.
	 * 
	 * @param ids
	 *          : List of ids
	 * @return Set of objects found or null
	 */
	public Set<?> find(List<Integer> ids) {
		return Finder.find(ids, this.getClass());
	}

	/**
	 * Find objects with conditions. Sanitize values to prevent from sql
	 * injections
	 * 
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param model
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * Object[] values = {"Job","Medor"}
	 * find("name=? and pets.name= ?",values,Person.class)
	 * </code>
	 * </pre>
	 */
	public static Set<?> find(String conditions, Object[] values, Class<? extends Hermes> model) {
		return Finder.find("*", Sanitizer.sanitize(conditions, values), null, model);
	}

	/**
	 * Find objects with conditions.
	 * 
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param model
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name='Job' and pets.name='Medor'",Person.class)
	 * </code>
	 * </pre>
	 */
	public static Set<?> find(String conditions, Class<? extends Hermes> model) {
		return Finder.find("*", conditions, null, model);
	}

	/**
	 * Find objects with conditions, and options
	 * 
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param model
	 * @param options
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name='Job' and pets.name='Medor'",Person.class)
	 * </code>
	 *</pre>
	 */
	public static Set<?> find(String conditions, Class<? extends Hermes> model, String options) {
		return Finder.find("*", conditions, options, model);
	}

	/**
	 * Find objects with conditions.
	 * 
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name='Job' and pets.name='Medor'")
	 * </code>
	 *</pre>
	 */
	public Set<?> find(String conditions) {
		return Finder.find("*", conditions, null, this.getClass());
	}

	/**
	 * Find objects with conditions, and options
	 * 
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param options
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name='Job' and pets.name='Medor'","limit => 10")
	 * </code>
	 *</pre>
	 */
	public Set<?> find(String conditions, String options) {
		return Finder.find("*", conditions, options, this.getClass());
	}

	/**
	 * Find objects with conditions and with specified columns (sql select clause)
	 * 
	 * @param select
	 *          : string of columns to retrieve (ie : "name,age").
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param model
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name,age","name='Job' and pets.name='Medor'",Person.class)
	 * </code>
	 *</pre>
	 */
	public static Set<?> find(String select, String conditions, Class<? extends Hermes> model) {
		return Finder.find(select, conditions, null, model);
	}

	/**
	 * Find objects with conditions and with specified columns (sql select clause)
	 * with options : limit, offset, order ...
	 * 
	 * @param select
	 *          : string of columns to retrieve (ie : "name,age").
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param model
	 * @param options
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name,age","name='Job' and pets.name='Medor'",Person.class,"order => 'name desc'")
	 * </code>
	 * </pre>
	 */
	public static Set<?> find(String select, String conditions, Class<? extends Hermes> model,
			String options) {
		return Finder.find(select, conditions, options, model);
	}

	/**
	 * Find objects with conditions and with specified columns (sql select clause)
	 * 
	 * @param select
	 *          : string of columns to retrieve (ie : "name,age").
	 * @param conditions
	 *          : string of conditions where attributes associations can be used.
	 * @param options
	 *          : string of options on limit, offset, order ...
	 * @return Set of founded objects
	 * 
	 *         <pre>
	 * <strong>Example</strong> 
	 * <code>
	 * find("name,age","name='Job' and pets.name='Medor'","limit => 12")
	 * </code>
	 * </pre>
	 */
	public Set<?> find(String select, String conditions, String options) {
		return Finder.find(select, conditions, options, this.getClass());
	}

	/**
	 * Find all objects of the given class
	 * 
	 * @param model
	 * @return Set of all objects
	 */
	public static Set<?> all(Class<? extends Hermes> model) {
		return Finder.find("*", null, null, model);
	}

	/**
	 * Find all objects of the given class
	 * 
	 * @param model
	 * @param options
	 *          : string of options on limit, offset, order
	 * @return Set of Hermes objects
	 */
	public static Set<?> all(Class<? extends Hermes> model, String options) {
		return Finder.find("*", null, options, model);
	}

	/**
	 * Get all objects of the class
	 * 
	 * @return Set of all objects
	 */
	public Set<?> all() {
		return Finder.find("*", null, null, this.getClass());
	}

	/**
	 * Get all objects of the class with options
	 * 
	 * @param string
	 *          of options on limit, offset, order ...
	 * @return Set of all objects
	 */
	public Set<?> all(String options) {
		return Finder.find("*", null, options, this.getClass());
	}

	/**
	 * Get the first object matching given conditions, on the given class
	 * 
	 * @param conditions
	 * @param model
	 * @return Hermes object
	 */
	public static Hermes first(String conditions, Class<? extends Hermes> model) {
		return Finder.findFirst("*", conditions, model);
	}

	/**
	 * Get the first object matching given conditions on this class.
	 * 
	 * @param conditions
	 * @return Hermes object
	 */
	public Hermes first(String conditions) {
		return Finder.findFirst("*", conditions, this.getClass());
	}

	/**
	 * Get the first object matching given conditions, on the given class.
	 * Sanitize values to prevent from sql injections.
	 * 
	 * @param conditions
	 * @param model
	 * @return Hermes object
	 */
	public static Hermes first(String conditions, Object[] values, Class<? extends Hermes> model) {
		return Finder.findFirst("*", Sanitizer.sanitize(conditions, values), model);
	}

	/**
	 * Get the first object matching given conditions on this class. Sanitize
	 * values to prevent from sql injections.
	 * 
	 * @param conditions
	 * @return Hermes object
	 */
	public Hermes first(String conditions, Object[] values) {
		return Finder.findFirst("*", Sanitizer.sanitize(conditions, values), this.getClass());
	}

	/**
	 * Get the first object by id on the given class
	 * 
	 * @param model
	 * @return Hermes object
	 */
	public static Hermes first(Class<? extends Hermes> model) {
		return Finder.findFirst("*", null, model);
	}

	/**
	 * Get the first object by id.
	 * 
	 * @return Hermes object
	 */
	public Hermes first() {
		return Finder.findFirst("*", null, this.getClass());
	}

	/**
	 * Get the last object by id on the given class
	 * 
	 * @param model
	 * @return Hermes object
	 */
	public static Hermes last(Class<? extends Hermes> model) {
		return Finder.findLast("*", null, model);
	}

	/**
	 * Get the last object by id.
	 * 
	 * @return Hermes object
	 */
	public Hermes last() {
		return Finder.findLast("*", null, this.getClass());
	}

	/**
	 * Find objects with an sql query on the given class.
	 * 
	 * @param sql
	 *          : Sql query
	 * @param model
	 * @return Set of Hermes objects matching the sql query
	 */
	public static Set<?> findBySql(String sql, Class<? extends Hermes> model) {
		return Finder.findBySql(sql, model);
	}

	/**
	 * Find objects with an sql query on this class
	 * 
	 * @param sql
	 *          : Sql query
	 * @return Set of Hermes objects matching the sql query
	 */
	public Set<?> findBySql(String sql) {
		return Finder.findBySql(sql, this.getClass());
	}

	/**
	 * Match if this object is new record, ie it has not yet been saved into
	 * database.
	 * 
	 * @return True/False
	 */
	public boolean isNewRecord() {
		return (id == 0);
	}

	/**
	 * Reload object from database.
	 * 
	 * @return Fresh! Hermes object.
	 */
	public Hermes reload() {
		return find(this.id);
	}

	/**
	 * Used to declare an hasMany association (1,n association) with cascade
	 * deleting.
	 * 
	 * @param attribute
	 *          : Attribute name of association
	 * @param dependency
	 *          : "dependent:destroy" if cascade delete is required
	 */
	public void hasMany(String attribute, String dependency) {
		associations.hasMany(attribute, dependency);
	}

	/**
	 * Used to declare an hasMany association (1,n association). No cascade delete
	 * is performed when father of association is erased.
	 * 
	 * @param attribute
	 *          : Attribute name of association
	 */
	public void hasMany(String attribute) {
		hasMany(attribute, "");
	}

	/**
	 * Used to declare an hasOne association (1,1 association) with cascade
	 * deleting.
	 * 
	 * @param attribute
	 *          : Attribute name of association
	 * @param dependency
	 *          : "dependent:destroy" if cascade delete is required
	 */
	public void hasOne(String attribute, String dependency) {
		associations.hasOne(attribute, dependency);
	}

	/**
	 * Used to declare an hasOne association (1,1 association). No cascade delete
	 * is performed when father of association is erased.
	 * 
	 * @param attribute
	 *          : Attribute name of association
	 */
	public void hasOne(String attribute) {
		hasOne(attribute, "");
	}

	/**
	 * Used to declare an ManyToMany association (n,n association) with cascade
	 * deleting.
	 * 
	 * @param attribute
	 *          : Attribute name of association
	 * @param dependency
	 *          : "dependent:destroy" if cascade delete is required
	 */
	public void manyToMany(String attribute, String dependency) {
		associations.manyToMany(attribute, dependency);
	}

	/**
	 * Used to declare an ManyToMany association (n,n association) . No cascade
	 * delete is performed when father of association is erased.
	 * 
	 * @param attribute
	 *          : Attribute name of association
	 */
	public void manyToMany(String attribute) {
		manyToMany(attribute, "");
	}

	public void belongsTo(Hermes object) {
		associations.belongsTo(object);
	}

	/**
	 * Load all attributes (Attribute type) of an object. Basic attributes, not
	 * associations attributes
	 */
	public void loadAttributes() {
		this.attributes = (ArrayList<Attribute>) Attribute.load(this);
	}

	/**
	 * Execute an sql query
	 * 
	 * @param sql
	 *          : Sql query
	 * @return True if execution of query succeed
	 */
	public static boolean execute(String sql) {
		return Updater.executeSql(sql);
	}

	/**
	 * Get the attribute object(Attribute class) of the given named attribute
	 * 
	 * @param name
	 *          : Attribute name
	 * @return Attribute object corresponding to attribute name
	 */
	public Attribute getAttribute(String name) {
		loadAttributes();
		for (Attribute attribute : attributes)
			if (attribute.getName().equals(name)) return attribute;
		return null;
	}

	/**
	 * "Presence of" validator. Object is not saved if attribute has null value.
	 * 
	 * @param attribute
	 *          : attribute name
	 */
	public void validatePresenceOf(String attribute) {
		if (Validations.validatePresenceOf(attribute, this)) return;
		addError(attribute, Error.Symbol.PRESENCE, null);
	}

	/**
	 * "Presence of" validator. Object is not saved if attribute has null value. A
	 * personalized error message can be used if validation fails.
	 * 
	 * @param attribute
	 *          : attribute name
	 * @param message
	 *          : Message of error(Error class) if validation fails.
	 */
	public void validatePresenceOf(String attribute, String message) {
		if (Validations.validatePresenceOf(attribute, this)) return;
		addError(attribute, Error.Symbol.PRESENCE, message);
	}

	/**
	 * "Size of" validator. Validate the size of the given attribute between min
	 * and max. Validation is skipped if allowNull is true and the attribute value
	 * is null.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param min
	 *          : Min size
	 * @param max
	 *          : Max size
	 * @param allowNull
	 *          : Allow null or no
	 */
	public void validateSizeOf(String attribute, int min, int max, boolean allowNull) {
		if (Validations.validateSizeOf(attribute, min, max, allowNull, this)) return;
		addError(attribute, Error.Symbol.SIZE, null);
	}

	/**
	 * "Size of" validator. Validate the size of the given attribute between min
	 * and max. Validation is skipped if allowNull is true and the attribute value
	 * is null. A personalized error message can be used if validation fails.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param min
	 *          : Min size
	 * @param max
	 *          : Max size
	 * @param allowNull
	 *          : Allow null or no
	 * @param message
	 *          : Message of error(Error class) if validation fails.
	 */
	public void validateSizeOf(String attribute, int min, int max, boolean allowNull, String message) {
		if (Validations.validateSizeOf(attribute, min, max, false, this)) return;
		addError(attribute, Error.Symbol.SIZE, message);
	}

	/**
	 * Validate the uniqueness value of the given attribute.
	 * 
	 * @param attribute
	 *          : Attribute name
	 */
	public void validateUniquenessOf(String attribute) {
		if (Validations.validateUniquenessOf(attribute, this)) return;
		addError(attribute, Error.Symbol.UNIQUENESS, null);
	}

	/**
	 * Validate the uniqueness value of the given attribute.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param message
	 *          : Error message
	 */
	public void validateUniquenessOf(String attribute, String message) {
		if (Validations.validateUniquenessOf(attribute, this)) return;
		addError(attribute, Error.Symbol.UNIQUENESS, message);
	}

	/**
	 * Validate the format of an attribute with the given pattern.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param pattern
	 *          : Regexp pattern
	 * @param allowNull
	 *          : Skip validation if allowNull is true and value is null.
	 */
	public void validateFormatOf(String attribute, Pattern pattern, boolean allowNull) {
		if (Validations.validateFormatOf(attribute, pattern, allowNull, this)) return;
		addError(attribute, Error.Symbol.FORMAT, null);
	}

	/**
	 * Validate the format of an attribute with the given pattern.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param pattern
	 *          : Regexp pattern
	 * @param allowNull
	 *          : Skip validation if allowNull is true and value is null.
	 * @param message
	 *          : Error message
	 */
	public void validateFormatOf(String attribute, Pattern pattern, boolean allowNull, String message) {
		if (Validations.validateFormatOf(attribute, pattern, allowNull, this)) return;
		addError(attribute, Error.Symbol.FORMAT, message);
	}

	/**
	 * Check if a object is valid, ie none of its validations fail.
	 * 
	 * @return True/ False
	 */
	public boolean isValid() {
		errors.clear();
		Callback.beforeValidate(this);
		Validations();
		return errors.isEmpty();
	}

	/**
	 * Used in own validation method (validate()). Add an error on the given
	 * attribute.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param symbol
	 *          : Symbol error
	 * @param message
	 *          : Error message
	 */
	public void addError(String attribute, Error.Symbol symbol, String message) {
		Error error;
		if (message == null) error = new Error(symbol);
		else error = new Error(symbol, message);
		errors.put(attribute, Error.add(error, errors.get(attribute)));
	}

	/**
	 * Used in own validation method (validate()). Add an error on the given
	 * attribute.
	 * 
	 * @param attribute
	 *          : Attribute name
	 * @param message
	 *          : Error message.
	 */
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

	/**
	 * Get hash of ManyToMany associations of this model.
	 * 
	 * @return hash map attribute_name => ManyToMany object
	 */
	public HashMap<String, ManyToMany> getManyToManyAssociations() {
		return associations.getManyToManyAsociations();
	}

	/**
	 * Get hash of HasOne associations of this model.
	 * 
	 * @return hash map attribute_name => HasOne object
	 */
	public HashMap<String, HasOne> getHasOneAssociations() {
		return associations.getHasOneAssociations();
	}

	/**
	 * Get hash of HasMany associations of this model.
	 * 
	 * @return hash map attribute_name => HasMany object
	 */
	public HashMap<String, HasMany> getHasManyAssociations() {
		return associations.getHasManyAssociations();
	}

	/**
	 * Change default generated table name associated with this model
	 * 
	 * @param tableName
	 *          : new name.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Get the table name associated with this model.
	 * 
	 * @return table name
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * Get id of the object.
	 * 
	 * @return id of object
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set id of this object . Use with care ...
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get all associations has map of this model. With object return we can
	 * access all associations (manyToMany ...)
	 * 
	 * @return Associations object
	 */
	public Associations getAssociations() {
		return associations;
	}

	/**
	 * Set associations not by declaration in the model.
	 * 
	 * @param associations
	 */
	public void setAssociations(Associations associations) {
		this.associations = associations;
	}

	/**
	 * Get list of attributes (List<Attribute>)
	 * 
	 * @return List of Attribute objects
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * Get errors of this model instance . Errors are generated by validations
	 * calls.
	 * 
	 * @return hash map of errors.
	 */
	public HashMap<String, List<Error>> getErrors() {
		return errors;
	}

	/**
	 * Set the hash map of errors.
	 * 
	 * @param errors
	 */
	public void setErrors(HashMap<String, List<Error>> errors) {
		this.errors = errors;
	}

}
