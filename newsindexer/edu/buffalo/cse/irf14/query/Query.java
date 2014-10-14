package edu.buffalo.cse.irf14.query;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents a parsed query<br>
 * NOTE:<br>
 * For each {@code clause} in a {@code query},
 * either {@link Term} or a {@link Query} instance can be
 * determined by calling {@code isQuery()} method in that {@link Clause}.
 * 
 * @author nikhillo
 *
 */
public class Query extends QueryComponent {
	
	private String raw;	// Raw query
	private List<Clause> clauses;
	private boolean isOriginal;	// Tells if this is the original query
	
	public Query(Index index, String content, boolean isOrg) {
		this.index = index;
		raw = content; 
		clauses = new LinkedList<Clause>();
		isOriginal = isOrg;
	}
	
	@Override
	public void setComponent(Operator op,
			Operator starter, boolean isBegin) {
		this.defaultOP = op;
		this.startOP = starter;
		this.begin = isBegin;
	}
	
	/**
	 * Method that Adds a clause to query structure. It returns
	 * a sub query if possible at the end.
	 * 
	 * @param clause raw representation of a clause
	 * @param op default operator connector
	 * @param startOP ending operator
	 * @param isBegin beginning flag
	 * @param prefInd prefix index of this query
	 * @return a new {@link Query} if this clause is a sub query;
	 * otherwise, just return {@code null}
	 */
	public Query addClause(String clause
			, Operator op, Operator startOP, boolean isBegin) {
		int ind = 0;	// Index that a term or query starts with
		String []seg = clause.trim().split(":");
		String tempStr;
		if (this.startOP != null) {
			switch (this.startOP) {
			case NOT:
				if (startOP != null) {
					switch(startOP) {
					case AND:
						startOP = Operator.NOTAND;
						break;
					case OR:
						startOP = Operator.NOTOR;
						break;
					default: break;
					}
				} else {
					startOP = Operator.NOT;
				}
			default:
				break;
			}
		}
		Clause tempClause = new Clause(op, startOP);
		Query subQuery = null;
		
		if (seg.length > 1) {
			Index type = Index.getIndex(seg[0].trim());
			if (type != null) {
				tempClause.setIndex(type);
				ind = 1;
			} else {
				if (this.index != null) {
					tempClause.setIndex(index);
				}
			}
		} else {
			if (this.index != null) {
				tempClause.setIndex(index);
			}
		}

		if (seg[ind].startsWith("(") 
				&& seg[seg.length - 1].endsWith(")")) {
			// Sub query case 
			tempStr = rawFilter(seg, ind, ":");
			subQuery = tempClause.setQuery(tempStr, false, isBegin);
		} else {
			// Term case
			tempStr = seg[0];
			for (int i = 1; i < seg.length; ++i) {
				tempStr += ":" + seg[i];
			}
			tempClause.setTerm(tempStr, isBegin);
		}
		if (clauses.size() == 0) {
			tempClause.setFirst();
		}
		clauses.add(tempClause);
		
		return subQuery;
	}
	
	/**
	 * Return number of clauses in this query.
	 */
	public int size() {
		return clauses.size();
	}
	
	public Clause getClause(int i) {
		if ((i >=0) && (i < clauses.size())) {
			return clauses.get(i);
		} else {
			return null;
		}
	}
	
	public String rawString() {
		return raw;
	}
	
	public boolean isOriginal() {
		return isOriginal;
	}
	
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {
		String query = null;

		if (clauses.size() > 0) {
			query = clauses.get(0).toString();
			
			for (int i = 1; i < clauses.size(); ++i) {
				query += " " + clauses.get(i).toString();
			}
			// Eliminate unnecessary column pair
			if (clauses.size() == 1) {
				if ((query.charAt(0) == '[') 
						&& (query.charAt(query.length() - 1) == ']')) {
					query = query.substring(1, query.length() - 1);
					query = query.trim();
				}
			}
			
			if (isOriginal) {
				query = "{ " + query + " }";
			}
		}
		
		return query;
	}
	
	/**
	 * Filter a raw query
	 * 
	 * @param seg texts split by a delimiter
	 * @param index to start filtering
	 * @param delim the delimiter
	 * @return filtered raw query
	 */
	private String rawFilter(String []seg, int index, String delim) {
		String tempStr;
		if (seg[index].startsWith("(")
				&& seg[seg.length - 1].endsWith(")")) {
			int ind = seg.length - 1;
			seg[index] = seg[index].substring(1);
			seg[ind] = seg[ind].substring(0, seg[ind].length() - 1);
		}
		tempStr = seg[index];
		for (int i = index + 1; i < seg.length; ++i) {
			tempStr += ":" + seg[i];
		}
		return tempStr;
	}
}
