package migration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import core.Attribute;
import core.Hermes;
import core.Inflector;
import core.Introspector;
import core.Jointure;
import core.ManyToMany;

//TODO : REFACTORING
public class Migration {

	private ArrayList<Table>	tables	= new ArrayList<Table>();

	public static void execute(String packageName, String fileName) {
		Migration migration = new Migration(packageName);
		Iterator<Table> tables = migration.getTables().iterator();

		File file = new File(fileName);
		try {
			FileOutputStream migrationFile = new FileOutputStream(file);
			PrintStream printStream = new PrintStream(migrationFile);
			while (tables.hasNext()) {
				Table table = (Table) tables.next();
				printStream.println(table.sql());
			}
			printStream.close();
		}
		catch (Exception e) {
			System.err.println("Error writing to file");
		}

	}

	public Migration(String packageName) {
		loadModels(packageName);
		idDefinition();
		columnsDefinitions();
		foreignKeysDefinitions();
		jointureDefinitions();
		handleSTI();
	}

	private void jointureDefinitions() {
		ArrayList<Table> tablesClone = (ArrayList<Table>) tables.clone();
		for (Table table : tablesClone) {
			Hermes klass = Introspector.instanciate(table.getKlass());
			HashMap<String, ManyToMany> associations = klass.getAssociations().getManyToManyAsociations();
			for (String attribute : associations.keySet()) {
				Jointure jointure = associations.get(attribute).getJointure();
				if (!isJointureExists(jointure)) tables.add(new Table(jointure.getTableName()));
			}
		}
	}

	private boolean isJointureExists(Jointure jointure) {
		for (Table table : tables)
			if (table.getName().equals(jointure.getTableName())) return true;
		return false;
	}

	public Table tableWithKlass(Class<? extends Hermes> klass) {
		for (Table table : tables)
			if (table.getKlass().equals(klass)) return table;
		return null;
	}

	private void handleSTI() {
		Table parent = null;
		ArrayList<Table> tablesClone = (ArrayList<Table>) tables.clone();
		for (Table table : tablesClone) {
			if (table.isSingleTableInheritence()) {
				parent = tableWithKlass(table.getParent());
				parent.getColumns().putAll(table.getColumns());
				parent.getForeignKeys().putAll(table.getForeignKeys());
				tables.remove(table);
			}
		}
	}

	private void foreignKeysDefinitions() {
		for (Table table : tables) {
			Hermes klass = Introspector.instanciate(table.getKlass());
			Set<String> hasOneAttributes = klass.getAssociations().getHasOneAssociations().keySet();
			Set<String> hasManyAttributes = klass.getAssociations().getHasManyAssociations().keySet();

			for (String attribute : hasOneAttributes)
				addForeignKeyToTable(attribute, klass);
			for (String attribute : hasManyAttributes)
				addForeignKeyToTable(attribute, klass);
		}
	}

	private void addForeignKeyToTable(String attribute, Hermes klass) {
		Table table = tableFor(attribute, klass);
		String foreignKeyName = Inflector.foreignKey(klass);
		table.getForeignKeys().put(foreignKeyName, "integer");
	}

	private Table tableFor(String attribute, Hermes klass) {
		for (Table table : tables)
			if (table.getKlass().equals(Introspector.klass(attribute, klass))) return table;
		return null;
	}

	private void columnsDefinitions() {
		for (Table table : tables) {
			Hermes klass = Introspector.instanciate(table.getKlass());
			for (Attribute attribute : klass.getAttributes())
				table.getColumns().put(attribute.getName(), attribute.getSqlType());
		}
	}

	private void idDefinition() {
		String definition = "int primary key auto_increment";
		for (Table table : tables) {
			table.getColumns().put("id", definition);
		}
	}

	private void loadModels(String packageName) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource(packageName.replace('.', '/'));
		File directory = new File(resource.getFile());

		for (String file : directory.list())
			try {
				String klassName = packageName + "." + file.replace(".class", "");
				Class<? extends Hermes> klass = (Class<? extends Hermes>) Class.forName(klassName);
				tables.add(new Table(klass));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}

	// Getters & Setters

	public ArrayList<Table> getTables() {
		return tables;
	}
}
