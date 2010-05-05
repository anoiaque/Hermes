package core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Options {

	private final static String	LIMIT_REGEXP	= "limit.*?=>\\s*\\d*";
	private final static String	OFFSET_REGEXP	= "offset.*?=>\\s*\\d*";
	private final static String	ORDER_REGEXP	= "order.*?=>\\s*'.*?'";

	private String							options;

	public Options(String options) {
		this.options = options;
	}

	public String limit() {
		return match(LIMIT_REGEXP);
	}

	public String offset() {
		return match(OFFSET_REGEXP);
	}

	public String order() {
		return match(ORDER_REGEXP);
	}

	private String match(String regexp) {
		if (options == null) return null;
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(options);

		if (!matcher.find()) return null;
		return optionValue(matcher.group());
	}

	private String optionValue(String option) {
		return option.split("=>")[1].trim();
	}

}
