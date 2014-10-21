package edu.buffalo.cse.irf14.query;

import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.QueryComponent.QueryType;

/**
 * A component in a clause, which stores the term content.
 * @author tianmiao
 *
 */
public class Term extends QueryComponent {

	private String []values;
	private IndexType []indexes;
	
	public Term(IndexType index, String content) {
		m_queryType = QueryType.Term;
		if (!content.startsWith("\"") && !content.endsWith("\"")) {
			// This is group of terms
			String []segs = content.split(" ");
			String []temp;
			values = new String[segs.length];
			indexes = new IndexType[segs.length];
			
			for (int i = 0; i < segs.length; ++i) {
				temp = segs[i].split(":");
				values[i] = new String();
				if (temp.length > 1) {
					for (int j = 1; j < temp.length; ++j) {
						values[i] += temp[j] + ":";
					}
					values[i] = values[i].substring(0, values[i].length() - 1);
					indexes[i] = IndexType.getIndex(temp[0]);
				} else {
					values[i] = segs[i];
					indexes[i] = (index == null) 
							? IndexType.TERM : index;
				}
			}
		} else {
			String []temp;
			
			// This is a term phrase
			values = new String[1];
			indexes = new IndexType[1];
			temp = content.split(":");
			if (temp.length > 1) {
				for (int i = 1; i < temp.length; ++i) {
					values[0] += temp[i] + ":";
				}
				values[0] = values[0].substring(0, values[0].length() - 1);
				indexes[0] = IndexType.getIndex(temp[0]);
			} else {
				values[0] = content;
				indexes[0] = (index == null) 
						? IndexType.TERM : index;
			}
		}
	}
	
	public int size() {
		return values.length;
	}
	
	public String getTerm(int i) {
		return values[i];
	}
	
	public IndexType getIndex(int i) {
		return (indexes[i] == null)
				? index : indexes[i];
	}
	
	@Override
	public void setComponent(
			Operator op, Operator starter, boolean isBegin) {
		for (int i = 0; i < indexes.length; ++i) {
			if (indexes[i] == null) {
				indexes[i] = index;
			}
		}
		if (op != null) {
			this.defaultOP = op;
		} else {
			defaultOP = Operator.OR;
		}
		this.startOP = starter;
		this.begin = isBegin;
	}
	
	@Override
	public String toString() {
		String connector;
		if (values.length > 1) {
			connector = (defaultOP == null)
					? "OR " : defaultOP.name() + " ";
		} else {
			connector = "";
		}
		String result = indexes[0].toString() + ":" + values[0];
		if (startOP != null) {
			switch (startOP) {
			case NOT:
			case NOTAND:
			case NOTOR:
				result = "<" + result + ">";
				connector = startOP.toString() + " ";
				break;
			default: break;
			}
		}
		connector = " " + connector;
		// Connect all pieces of value
		for (int i = 1; i < values.length; ++i) {
			result += connector;
			if ((startOP == Operator.NOT) || (startOP == Operator.NOTAND)
					|| (startOP == Operator.NOTOR)) {
				result += "<" + indexes[i].toString() + ":" + values[i] + ">";
			} else {
				result += indexes[i].toString() + ":" + values[i];
			}
		}

		return result;
	}
}
