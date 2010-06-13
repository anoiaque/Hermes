package adapters.MySql;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import core.Attribute;
import core.Introspector;

public class TypeCast {

	public static Object toJava(Object value) {
		if (value == null) return null;
		if (Introspector.isDate(value)) return dateToJava((Date) value);
		if (Introspector.isTimestamp(value)) return timeStampToJava((Timestamp) value);
		return value;
	}

	public static Object toSql(Attribute attribute) {
		return value(attribute);
	}

	public static Object toSql(Object value) {
		if (Introspector.isBoolean(value)) return booleanToSql((Boolean) value);
		if (Introspector.isDate(value)) return dateToSql((Calendar) value);
		if (Introspector.isTimestamp(value)) return timestampToSql((Timestamp) value);
		return value;
	}

	

	public static Object dateToSql(Calendar value) {
		if (value == null) return null;
		int year = ((Calendar) value).get(Calendar.YEAR);
		int month = ((Calendar) value).get(Calendar.MONTH);
		int day = ((Calendar) value).get(Calendar.DAY_OF_MONTH);

		return year + "-" + month + "-" + day;
	}

	public static int booleanToSql(Boolean value) {
		return (value.equals(new Boolean(true))) ? 1 : 0;
	}

	public static Object timestampToSql(Timestamp value) {
		return value;
	}

	// Private methods

	private static Object value(Attribute attribute) {
		if (attribute.getSqlType().equals(Mapping.BOOLEAN)) return booleanToSql(attribute);
		if (attribute.getSqlType().equals(Mapping.STRING)) return sanitize(attribute);
		if (attribute.getSqlType().equals(Mapping.DATE)) return dateToSql(attribute);
		if (attribute.getSqlType().equals(Mapping.TIMESTAMP)) return timestampToSql(attribute);
		return attribute.getValue();
	}

	private static Object dateToSql(Attribute attribute) {
		return dateToSql((Calendar) attribute.getValue());
	}

	private static int booleanToSql(Attribute attribute) {
		return (attribute.getValue().equals(new Boolean(true))) ? 1 : 0;
	}

	private static Object timestampToSql(Attribute attribute) {
		return attribute.getValue();
	}

	private static String sanitize(Attribute attribute) {
		String value = (String) attribute.getValue();
		if (value == null) return null;
		value = value.replace("'", "''");
		return value;
	}

	private static Timestamp timeStampToJava(Timestamp value) {
		return value;
	}

	@SuppressWarnings("deprecation")
	private static Calendar dateToJava(Date value) {
		Calendar date = Calendar.getInstance();
		value.setMonth(value.getMonth() + 1); // Calendar JANUARY = 0..
		date.setTime(value);
		return date;
	}

}
