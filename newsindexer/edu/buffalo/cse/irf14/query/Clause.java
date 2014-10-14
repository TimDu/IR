package edu.buffalo.cse.irf14.query;

/**
 * Class that represents a clause in a query.
 * @author tianmiao
 * 
 */
public class Clause {

	private boolean isQuery;
	private boolean isFirst;
	private Index index;	// Index type
	private QueryComponent component;
	private Operator startOP;	// Operator at start of the clause
	private Operator defaultOP;
	
	public Clause (Operator op, Operator sOP) {
		isFirst = false;
		index = Index.TERM;
		component = null;
		startOP = sOP;
		if (op != null) {
			defaultOP = op;
		} else {
			defaultOP = Operator.OR;
		}
	}
	
	public void setIndex(Index index) {
		this.index = index;
	}
	
	public void setTerm(String val, boolean isBegin) {
		component = new Term(index, val);
		component.setComponent(defaultOP, startOP, isBegin);
		isQuery = false;
	}
	
	public Query setQuery(String val, boolean isOrg, boolean isBegin) {
		component = new Query(index, val, isOrg);
		component.setComponent(defaultOP, startOP, isBegin);
		isQuery = true;
		return (Query)component;
	}
	
	public void setFirst() {
		isFirst = true;
	}
	
	public Index getIndex() {
		return index;
	}
	
	public QueryComponent getComponent() {
		return component;
	}
	
	public boolean isQuery() {
		return isQuery;
	}
	
	public Operator getStartOP() {
		return startOP;
	}
	
	@Override
	public String toString() {
		// Null check. Either term or query must be not null
		if (component == null) {
			return null;
		}
		String content = component.toString();
		String prefix = new String();

		if (isQuery && !((Query)component).isOriginal()) {
			content = "[ " + content + " ]";
		} else if (!isQuery) {
			if (((Term)component).size() > 1) {
				content = "[ " + content + " ]";
			}
		}

		if ((startOP != null) && !isFirst) {
			prefix = startOP.toString() + " ";
		}

		return prefix + content;
	}
}
