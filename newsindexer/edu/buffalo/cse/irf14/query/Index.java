package edu.buffalo.cse.irf14.query;

/**
 * A component in a clause, which stores index types.
 * @author tianmiao
 *
 */
public enum Index {

	TERM ("Term"),
	CATEGORY ("Category"),
	AUTHOR ("Author"),
	PLACE ("Place");
	
	private String represent;
	
	private Index(String val) {
		represent = val;
	}
	
	public static Index getIndex(String val) {
		val = val.toUpperCase();
		if (val.equals("PLACE")) {
			return PLACE;
		} else if (val.equals("CATEGORY")) {
			return CATEGORY;
		} else if (val.equals("AUTHOR")) {
			return AUTHOR;
		} else if (val.equals("TERM")){
			return TERM;
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return represent;
	}
}
