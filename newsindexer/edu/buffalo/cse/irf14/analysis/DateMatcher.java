package edu.buffalo.cse.irf14.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A date format inspector, which tells if a string
 * is a valid date.
 */
public class DateMatcher {

	private final static String day = "(([0][1-9])|"
			+ "([12][0-9])|([3][01]))";
	private final static String month = "(Jan(uaray)?|Feb(urary)?|"
			+ "Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|"
			+ "Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?)";
	private final static String year = "((\\d{4}([-/]\\d\\d)?)|"
			+ "(\\d{3}( |)(AD|BC))|(\\d{2}( |)(AD|BC))|"
			+ "(\\d{1}( |)(AD|BC)))";
	private final static String time = "(([0-9]|0[0-9]|1[012]:"
			+ "[0-5][0-9]:([0-5][0-9]|)( |)((AM|PM))|))";
	private final static String delimiter = "(, | |/|-|\\.)";
	
	private final static String date1 = "(" + day + delimiter +
			month + "(" + delimiter + year + ")?" + ")";
	private final static String date2 = "(" + month + delimiter +
			day + "(" +	delimiter + year + ")?" + ")";
	private final static String date3 = "(" + "(" + month +
			delimiter + ")?" + year + ")";
	
	private final static Pattern pattern =
			Pattern.compile(date1 + "|" + date2 + "|" + date3 +
					"|" + time);
	
	/**
	 * Parses and finds valid date(s) from words
	 * @param words source words to be parsed
	 * @return a array of {@code string} if the result has date,
	 * {@code null} if no date is found
	 */
	public static String[] findDate(String words) {
		String subSeq;
		Matcher matcher = pattern.matcher(words);
		List<String> list = new LinkedList<String>();
		
		while (matcher.find()) {
			subSeq = matcher.group();
			// TODO format the date
			//list.add()
		}
		
		return list.isEmpty() ? null :
			list.toArray(new String[list.size()]);
	}
	
	/**
	 * Converts valid dates to words
	 * @return
	 */
	private static String convert(String input) {
		
		return null;
	}
}
