/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import java.util.LinkedList;
import java.util.List;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
		
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one 
	 * amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static Query parse(String userQuery, String defaultOperator) {
		//TODO: YOU MUST IMPLEMENT THIS METHOD
		Query raw = new Query(null, userQuery, true);
		Operator op;
		if (defaultOperator != null) {
			op = Operator.getOperator(defaultOperator);
		} else {
			op = Operator.OR;
		}

		try {
			parseEngine(raw, op);
		} catch(QueryParserException e) {
			e.printStackTrace();
			return null;
		}

		return raw;
	}
	
	/**
	 * Turn a raw query into a parsed one.
	 * 
	 * @param rawQuery raw query
	 * @return parsed query
	 * @throws QueryParserException 
	 */
	private static void parseEngine(Query rawQuery, Operator defaultOperator)
			throws QueryParserException {
		int startIndex = 0;
		int endIndex = 0;
		// List for recording all parentheses pairs in the most
		// outside of a query sentence 
		List<int[]> intervals = new LinkedList<int[]>();
		// Temporary interval recorder
		int[]tempInt = null;
		String raw = rawQuery.rawString();
		
		// First step.
		// It filters the clause indexes with the most outside 
		// parentheses
		int count = 0;	// Parentheses counter. +1 for '(', -1 for ')'
		boolean start = false;	// Turns true when the first unprocessed
								// '(' is encountered
		for (int i = 0; i < raw.length(); ++i) {
			if (raw.charAt(i) == '(') {
				if (!start) {
					// Turns start to true
					start = true;
					startIndex = i;
				}
				++count;
			} else if (raw.charAt(i) == ')') {
				--count;
			}
			// Check if a compete clause has formed 
			if (start && (count == 0)) {
				tempInt = new int[2];
				tempInt[0] = startIndex;
				tempInt[1] = i;
				intervals.add(tempInt);
				start = false;
			}
		}
		
		// Second step.
		// Recursively find clauses in sub queries from step one
		String [][]tempResult;
		int sTIndex = 0;
		int eTIndex;
		boolean isTBegin = true;
		
		if (!intervals.isEmpty()) {
			int index;	// position of index prefix
			boolean isBegin;
			Operator tempOP;
			String strQuery;
			String tempStr;
			String []tempSegs;
			endIndex = 0;
			
			for (int[] pair: intervals) {
				startIndex = pair[0];
				strQuery = raw.substring(startIndex, pair[1] + 1);
				tempOP = null;
	
				// Find qualified clauses among the intervals of 
				// parentheses pairs found in step one.
				eTIndex = pair[0];
				if (sTIndex != 0) {
					isTBegin = false;
				}
				if ((eTIndex - sTIndex) > 1) {
					tempStr = raw.substring(sTIndex, eTIndex).trim();
					
					// Add clauses
					tempResult = opProcess(tempStr);
					if (tempResult != null) {
						for (String []cl: tempResult) {
							rawQuery.addClause(cl[0]
									, defaultOperator
									, Operator.getOperator(cl[1]), isTBegin);
						}
					}
				}
				
				sTIndex = pair[1] + 1;
				// Finding terms finish
				
				if (startIndex > 0) {
					tempStr = raw.substring(endIndex, startIndex);
					// Find prefix operator
					tempSegs = tempStr.trim().split(" ");
					// Check the position of this clause,
					// if this clause is in the middle, find its prefix
					// operator
					if (tempSegs.length > 1) {
						// Find position of its prefix index, if there is any
						if (tempSegs[tempSegs.length - 1].endsWith(":")) {
							index = tempStr.lastIndexOf(
									tempSegs[tempSegs.length - 1]) + endIndex;
							strQuery = raw.substring(index, pair[1] + 1);
							index = tempSegs.length - 2;
						} else {
							index = tempSegs.length - 1;
						}
						
						tempOP = Operator.getOperator(tempSegs[index]);
						// Validation
						if (tempOP == null) {
							throw new QueryParserException();
						}
					} else if (tempSegs.length > 0) {
						tempOP = Operator.getOperator(
								tempSegs[tempSegs.length - 1]);
						// Validation
						if (tempOP == null) {
							throw new QueryParserException();
						}
					} else {
						tempOP = null;
					}
				}
				
				if (endIndex == 0) {
					isBegin = true;
				} else {
					isBegin = false;
				}
				
				parseEngine(rawQuery.addClause(
						strQuery, defaultOperator, tempOP, isBegin)
						, defaultOperator);
	
				endIndex = pair[1] + 1;
			}
			// Last clause split by sub-queries
			eTIndex = raw.length();
			if ((eTIndex - sTIndex) > 1) {
				tempStr = raw.substring(sTIndex, eTIndex).trim();
				
				// Add clauses
				tempResult = opProcess(tempStr);
				if (tempResult != null) {
					for (String []cl: tempResult) {
						rawQuery.addClause(cl[0]
								, defaultOperator
								, Operator.getOperator(cl[1]), isTBegin);
					}
				}
			}
		}

		// Fourth step.
		// Add terms if there is no sub-query
		if (intervals.size() == 0) {
			// Add clauses
			tempResult = opProcess(raw);

			if (tempResult != null) {
				for (String []cl: tempResult) {
					rawQuery.addClause(cl[0]
							, defaultOperator
							, Operator.getOperator(cl[1]), isTBegin);
					isTBegin = false;
				}
			}
		}
	}
	
	/**
	 * Operator separator. Extracts clauses from a sentence.
	 * 
	 * @param val query to be parsed
	 * @return parsed string representations. The first column
	 * represents different parsed values; while the second
	 * column represents a parsed text as well as the operator
	 * it is parsed through
	 */
	private static String[][] opProcess(String val) {
		String []segs = val.split(" |\t");
		String temp;
		String operator = new String();
		String []tempResult = new String[2];
		List<String[]> resultList = new LinkedList<String[]>();
		
		for (String seg: segs) {
			temp = collectOperator(seg);
			if ((operator != null) && operator.isEmpty()) {
				operator = temp;
				tempResult[1] = operator;
				if (operator == null) {
					tempResult[0] = seg + " ";
				} else {
					tempResult[0] = new String();
				}
			} else {
				if (temp == null) {
					if (seg.contains(":")
							&& (seg.split(":").length == 1)) {

						if (resultList.size() == 0) {
							return null;
						}
						return resultList.toArray(
								new String[resultList.size()][2]);
					}
					tempResult[0] += seg + " ";
				} else {
					tempResult[0] = tempResult[0].trim();
					resultList.add(tempResult);
					tempResult = new String[2];
					tempResult[0] = new String();
					operator = temp;
					tempResult[1] = operator;
				}
			}
		}
		// Last terms
		tempResult[0] = tempResult[0].trim();
		if (!tempResult[0].isEmpty()) {
			resultList.add(tempResult);
		}
		
		if (resultList.size() == 0) {
			return null;
		}
		return resultList.toArray(new String[resultList.size()][2]);
	}
	
	private static String collectOperator(String val) {
		String []positiveAry = {"AND", "OR", "NOT"};
		
		for (int i = 0; i < 3; ++i) {
			if (val.toUpperCase().equals(positiveAry[i])) {
				return val.toUpperCase();
			}
		}
		
		return null;
	}
}
