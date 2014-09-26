package edu.buffalo.cse.irf14.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumericMatcher {

	private static String integer = "(\\d+)";
	private static String float_num = "(\\d+\\.\\d+)";
	private static String long_num = "([1-9]\\d{0,2})(,\\d{3})+";
	private static String combo =
			"[1-9](\\d{0,2})(,\\d{3})*(\\.\\d*)?";
	
	private static Pattern pattern = Pattern.compile(combo
			+ "|" + float_num +  "|" + long_num + "|" + integer);
	
	public static String[] getMatches(String str) {
		String temp = null;
		Matcher matcher = pattern.matcher(str);
		List<String> list = new LinkedList<String>();
		
		while (matcher.find()) {
			temp = matcher.group();

			list.add(temp);
		}
		//System.out.println(list.size());
		return list.isEmpty() ? null :
			list.toArray(new String[list.size()]);
	}
}
