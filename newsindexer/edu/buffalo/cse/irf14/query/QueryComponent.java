package edu.buffalo.cse.irf14.query;

import edu.buffalo.cse.irf14.index.IndexType;

/**
 * Public component shared by Query and Term class
 * @author tianmiao
 *
 */
public abstract class QueryComponent {

	protected IndexType index;
	protected Operator defaultOP;
	protected Operator startOP;	// true if this component follows with 'NOT'
	protected boolean begin;	// true if this component starts a query
	
	public enum QueryType {
	      Term, Query 
	}
	
	protected QueryType m_queryType;
	
	public QueryType getType()
	{
		return m_queryType;
	}
	
	public IndexType getIndex() {
		return index;
	}
	
	/**
	 * Setup the content in this component
	 * 
	 * @param content could be a {@code query} or {@code term}
	 * @param index {@link IndexType} type of this component 
	 * @param op default operator
	 * @param isBegin beginning flag
	 * @param starter starter operator
	 */
	public abstract void setComponent(
			Operator op, Operator starter, boolean isBegin);
	@Override
	public abstract String toString();
}
