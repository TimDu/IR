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

	private final static String day = "((0?[1-9])|"
			+ "([12][0-9])|([3][01]))";
	private final static String month = "(jan(uaray)?|feb(urary)?|"
			+ "mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|"
			+ "sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?|"
			+ "(0?[1-9])|[10-12])";
	private final static String year = "((\\d{4}([-/]\\d\\d)?)|"
			+ "(\\d{3}( |)(ad|bc))|(\\d{2}( |)(ad|bc))|"
			+ "(\\d{1}( |)(ad|bc)))";
	private final static String time = "(([0-9]|[01][0-9]|2[0-3]):"
			+ "[0-5][0-9]:([0-5][0-9]|)( |)((am|pm)|))";
	private final static String delimiter = "(, | |/|-|\\.)";
	
	private final static String date1 = "(" + day + delimiter
			+ month + "(" + delimiter + year + ")?" + ")";
	private final static String date2 = "(" + month + delimiter
			+ day + "(" +	delimiter + year + ")?" + ")";
	private final static String date3 = "(" + "(" + month
			+ delimiter + ")?" + year + ")";
	
	private final static Pattern pattern =
			Pattern.compile(date1 + "|" + date2 + "|" + date3
					+ "|" + time);
	
	/**
	 * Parses and finds valid date(s) from words.
	 * @param words source words to be parsed
	 * @return a array of {@code string} if the result has date,
	 * {@code null} if no date is found
	 */
	public static String[] findDate(String words) {
		// Sub sequence that contains a raw date
		String subSeq;
		Matcher matcher = pattern.matcher(words);
		// Result list
		List<String> list = new LinkedList<String>();
		
		while (matcher.find()) {
			subSeq = matcher.group();
			subSeq = convert(subSeq);
			list.add(subSeq);
		}
		
		return list.isEmpty() ? null :
			list.toArray(new String[list.size()]);
	}
	
	/**
	 * Converts valid dates to words.
	 * @param input raw date
	 * @return well formatted date
	 */
	private static String convert(String input) {
		String result = new String();
		String delim = null;
		String []dateElements = new String[5];
		String []segs = input.split(delimiter);
		
		dateElements[0] = "1900";	// Default year
		dateElements[1] = "01";	// Default month
		dateElements[2] = "01";	// Default day
		dateElements[3] = "00:00:00";	// Default time
		dateElements[4] = null;	// Alternate year
		
		for (int i = 0; i < segs.length; ++i) {
			segs[i] = segs[i].toLowerCase();
			if (segs[i].matches(day)) {
				if (segs[i].length() == 1) {
					dateElements[2] = "0" + segs[i];
				} else {
					dateElements[2] = segs[i];
				}
			} else if (segs[i].matches(month)) {
				if (segs[i].matches("\\d+}")) {
					if (segs[i].length() == 1) {
						segs[i] = "0" + segs[i];
					}
					dateElements[1] = segs[i];
				}
			} else if (segs[i].matches(year)) {
				if (year.contains("ad")) {
					// Only the cases where there are no more than 3
					// digits would happen
					segs[i] = segs[i].split("ad")[0].trim();
					for (int j = 0; j < 4 - segs[i].length(); ++j) {
						segs[i] = "0" + segs[i];
					}
					dateElements[0] = segs[i];
				} else if (year.contains("bc")) {
					// Only the cases where there are no more than 3
					// digits would happen
					segs[i] = segs[i].split("bc")[0].trim();
					segs[i] = "-" + segs[i];
					for (int j = 0; j < 4 - segs[i].length(); ++j) {
						segs[i] = "0" + segs[i];
					}
					dateElements[0] = segs[i];
				} else {
					// Only the cases of 4 digits' year
					dateElements[0] = segs[i].split("-|/")[0];
					if (segs[i].contains("-")) {
						delim = "-";
					} else if (segs[i].contains("/")) {
						delim = "/";
					}
					if (delim != null) {
						dateElements[4] =
								dateElements[0].substring(0, 2)
								+ segs[i].split("-|/")[1];
					}
				}
			} else if (segs[i].matches(time)) {
				if (segs[i].split(":")[0].length() == 1) {
					dateElements[3] = "0" + segs[i];
				} else {
					dateElements[3] = segs[i];
				}
			}
		}
		
		for (int i = 0; i < dateElements.length - 1; ++i) {
			result += dateElements[i];
		}
		if (dateElements[4] != null) {
			result += dateElements[4];
			for (int i = 1; i <dateElements.length - 1; ++i) {
				result += dateElements[i];
			}
		}
		
		return result;
	}
}
