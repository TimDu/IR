package edu.buffalo.cse.irf14.query;

/**
 * Operators in query.
 * @author tianmiao
 *
 */
public enum Operator {

	AND ("AND"),
	OR ("OR"),
	NOT ("AND"),
	NOTAND ("OR"),
	NOTOR ("AND");
	
	private String represent;
	
	private Operator(String val) {
		represent = val;
	}
	
	public static Operator getOperator(String val) {
		if (val == null) {
			return null;
		} else if (val.toUpperCase().equals("AND")) {
			return Operator.AND;
		} else if (val.toUpperCase().equals("OR")) {
			return Operator.OR;
		} else if (val.toUpperCase().equals("NOT")) {
			return Operator.NOT;
		}
			
		return null;
	}
	
	@Override
	public String toString() {
		return represent;
	}
}
