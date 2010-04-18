package core;

import java.util.ArrayList;
import java.util.List;

public class Error {

	public enum Symbol {
		PRESENCE, SIZE, UNIQUENESS, FORMAT
	};

	private String	message;
	private Symbol	symbol;

	public Error(Symbol symbol) {
		this.setSymbol(symbol);
		this.message = message(symbol);
	}

	public Error(Symbol symbol, String message) {
		this.setSymbol(symbol);
		this.message = message;
	}

	public static List<Error> add(Error error, List<Error> errors) {
		if (errors == null) errors = new ArrayList<Error>();
		errors.add(error);
		return errors;
	}

	public static String message(Symbol symbol) {
		String message = "";
		switch (symbol) {
		case PRESENCE:
			message = "is not present";
			return message;
		case SIZE:
			message = "has bad length";
			break;
		case UNIQUENESS:
			message = "must be unique";
			break;
		case FORMAT:
			message = "has bad format";
			break;
		}
		return message;
	}

	// Getter & Setter
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	public Symbol getSymbol() {
		return symbol;
	}
}
