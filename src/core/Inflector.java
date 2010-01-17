package core;

public class Inflector {

    public static String pluralize(String word) {
	if (word.toLowerCase().equals("person"))
	    return "people";
	if (word.toLowerCase().equals("address"))
	    return "addresses";
	if (word.toLowerCase().equals("pet"))
	    return "pets";
	return word;
    }

    public static String foreignKeyName(String attribute) {
	return attribute + "_id";
    }
}
