/**
 * 
 */
package edu.buffalo.cse.irf14.index;

/**
 * @author nikhillo
 *
 */
public enum IndexType {
	TERM ("Term"),
	CATEGORY ("Category"),
	AUTHOR ("Author"),
	PLACE ("Place");
	
	private String represent;
	
	private IndexType(String val) {
		represent = val;
	}
	
	public static IndexType getIndex(String val) {
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
};
