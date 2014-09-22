package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A date format inspector, which tells if a string
 * is a valid date.
 */
public class DateMatcher {

	private final static String day = "(((0)?[1-9])|"
			+ "([12][0-9])|([3][01]))";
	private final static String month = "(jan(uary)?|feb(urary)?|"
			+ "mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|"
			+ "sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?|"
			+ "(0?[1-9])|[10-12])";
	private final static String year = "((\\d{4}([-\\/]\\d\\d)?)|"
			+ "(\\d{3}( |)(ad|bc))|(\\d{2}( |)(ad|bc))|"
			+ "(\\d{1}( |)(ad|bc)))";
	private final static String time = "(([0-9]|[01][0-9]|2[0-3]):"
			+ "[0-5][0-9]:([0-5][0-9]|)( |)((am|pm)|))";
	private final static String delimiter = "(,|, | |\\/|-|\\.)";
	
	private final static String date1 = "(" + day + delimiter
			+ month + "(" + delimiter + year + delimiter + "?" 
			+")?" + ")";
	private final static String date2 = "(" + month + delimiter
			+ day + "(" + delimiter + year  + delimiter + "?" 
			+ ")?" + ")";
	private final static String date3 = "(" + "(" + month
			+ delimiter + ")?" + year  + delimiter + "?" 
			+ ")";
	
	// This pattern does not guarantee that the matched string
	// is a date. Instead, it offers a very likely candidate
	private final static Pattern dateComponent =
			Pattern.compile(year + delimiter + "?" + "|"
					+ month + delimiter + "?" + "|"
					+ time + delimiter + "?" + "|"
					+ day + delimiter + "?",
					Pattern.CASE_INSENSITIVE);
	// This pattern has strong guarantee on date format
	private final static Pattern pattern =
			Pattern.compile(date1 + "|" + date2 + "|" + date3
					+ "|" + time, Pattern.CASE_INSENSITIVE);
	
	/**
	 * Method to tell if this parameter term contains a date
	 * component.
	 * @param str term to be matched with
	 * @return {@code true} if any date component is contained;
	 * otherwise, {@code false}
	 */
	public static boolean matches(String str) {
		Matcher matcher = dateComponent.matcher(str);
		
		return matcher.find();
	}
	
	/**
	 * Method to tell if the current string is likely to
	 * have subsequent date information in the next token.
	 * @param term term from a token
	 * @return {@code true} if next token could have
	 * subsequent date information; otherwise, {@code false}
	 */
	public static boolean hasNext(String term) {
		/*
		 * To satisfy this requirement, the current string must
		 * be ended with a date component.
		 */
		int endOffset = 0;
		Matcher matcher = dateComponent.matcher(term);
		
		while (matcher.find()) {
			endOffset = matcher.end();
		}
		
		term = term.trim();
		if (endOffset == term.length()) {
			return true;
		} else if (endOffset == term.length() - 1) {
			if (term.endsWith(",") || term.endsWith("/") ||
					term.endsWith("-") || term.endsWith(".")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Method to tell if the current string contains date
	 * information that could be merged by previous token.
	 * @param term term from a token
	 * @return {@code true} if this token could be merged;
	 * otherwise, {@code false}
	 */
	public static boolean shouldMerge(String term) {
		/*
		 * To satisfy this requirement, the current string must
		 * has consecutive date components, which start from
		 * the token beginning.
		 */
		Matcher matcher = dateComponent.matcher(term);

		if (matcher.find()) {
			if (matcher.start() == 0) {
				return true;
			}
		}
		
		// Consider 'AD' 'BC' as date component
		if (term.startsWith("AD") ||
				term.startsWith("BC")) {
			return true;
		}
		
		// Consider 'am' 'pm' as date component
		if (term.toLowerCase().startsWith("am") ||
				term.toLowerCase().startsWith("pm")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Parses and finds valid date combination(s) from words.
	 * @param words source words to be parsed
	 * @return a array of {@code string} if the result has date,
	 * {@code null} if no date is found
	 */
	public static Map<String, String> mapDates(String words) {
		String keySeq;	// Sub sequence that contains a raw date
		String valSeq;
		Matcher matcher = pattern.matcher(words);
		// Result map that stores mapping between raw date
		// and formatted date
		Map<String, String> map = null;
		
		while (matcher.find()) {
			keySeq = matcher.group();System.out.println(keySeq);
			valSeq = convert(keySeq);
			if (!valSeq.isEmpty()) {
				if (map == null) {
					map = new HashMap<String, String>();
				}
				map.put(keySeq, valSeq);
			}
		}
		
		return map;
	}
	
	/**
	 * Method that converts valid dates to words.
	 * @param input raw date
	 * @return well formatted date
	 */
	private static String convert(String input) {
		String result = new String();
		String delim = null;
		String endDelim = null;
		String []dateElements = new String[5];
		String []segs = input.split(" ");
		
		dateElements[0] = null;	// Default year
		dateElements[1] = null;	// Default month
		dateElements[2] = null;	// Default day
		dateElements[3] = null;	// Default time
		dateElements[4] = null;	// Alternate year

		for (int i = 0; i < segs.length; ++i) {
			segs[i] = segs[i].toLowerCase();
			if (segs[i].matches(day + delimiter + "?")) {
				segs[i] = segs[i].split(delimiter)[0];
				if (segs[i].length() == 1) {
					dateElements[2] = "0" + segs[i];
				} else {
					dateElements[2] = segs[i];
				}
			} else if (segs[i].matches(month + delimiter + "?")) {
				segs[i] = segs[i].split(delimiter)[0];
				if (segs[i].matches("\\d+")) {
					if (segs[i].length() == 1) {
						segs[i] = "0" + segs[i];
					}
				} else {
					segs[i] = String.valueOf(getMonth(segs[i]));
				}
				dateElements[1] = segs[i];
			} else if (segs[i].matches(year + delimiter + "?")) {
				String []tempSeg = segs[i].split(delimiter);
				if (segs[i].endsWith(",") || segs[i].endsWith("|") ||
						segs[i].endsWith("-") ||
						segs[i].endsWith("/") ||
						segs[i].endsWith(".")) {
					endDelim = segs[i].substring(segs[i].length() - 1);
				}
				segs[i] = tempSeg[0];

				if (segs[i].contains("ad")) {
					// Only the cases where there are no more than 3
					// digits would happen
					segs[i] = segs[i].split("ad")[0].trim();
					for (int j = 0; j < 4 - segs[i].length(); ++j) {
						segs[i] = "0" + segs[i];
					}
					dateElements[0] = segs[i];
				} else if (segs[i].contains("bc")) {
					// Only the cases where there are no more than 3
					// digits would happen
					segs[i] = segs[i].split("bc")[0].trim();
					for (int j = 0; j < 4 - segs[i].length() + 1; ++j) {
						segs[i] = "0" + segs[i];
					}
					segs[i] = "-" + segs[i];
					dateElements[0] = segs[i];
				} else {
					// Only the cases of 4 digits' year
					dateElements[0] = segs[i].split("-|\\/")[0];

					if (segs[i].contains("-")) {
						delim = "-";
					} else if (segs[i].contains("\\/")) {
						delim = "\\/";
					}
					if (delim != null) {
						dateElements[4] =
								dateElements[0].substring(0, 2)
								+ segs[i].split("-|\\/")[1];
					}
				}
			} else if (segs[i].matches(time + delimiter + "?")) {
				segs[i] = segs[i].split(delimiter)[0];
				if (segs[i].split(":")[0].length() == 1) {
					dateElements[3] = "0" + segs[i];
				} else {
					dateElements[3] = segs[i];
				}
			}
		}
		
		// Generate formatted date
		for (int i = 0; i < dateElements.length - 2; ++i) {
			if (dateElements[i] != null) {
				// Fill up date information once any date component
				// is not null
				for (int j = 0; j < dateElements.length - 2; ++j) {
					if (dateElements[j] == null) {
						result += addDefaultDate(j);
					} else {
						result += dateElements[j];
					}
				}
				break;
			}
		}
		// Generate formatted time stamp
		if (dateElements[3] != null) {
			if (result.isEmpty()) {
				result += " ";
			}
			result += dateElements[3];
		}
		// Generate alternate date
		if (dateElements[4] != null) {
			result += delim + dateElements[4]
					+ result.substring(4, result.length());
		}

		if (endDelim != null) {
			result += endDelim;
		}
		
		return result;
	}
	
	/**
	 * A default date value map method
	 * @param i date content indicator
	 */
	private static String addDefaultDate(int i) {
		switch(i) {
		case 0:
		case 4:
			return "1900";
		case 1:
			return "01";
		case 2:
			return "01";
		case 3:
			return "00:00:00";
		default:
			return "";
		}
	}
	
	/**
	 * Method that converts text-valued month to digits one.
	 * @param month text-based month
	 * @return number-based month
	 */
	private static String getMonth(String month) {
		if (month.startsWith("ja")) {
			return "01";
		} else if (month.startsWith("f")) {
			return "02";
		} else if (month.startsWith("mar")) {
			return "03";
		} else if (month.startsWith("ap")) {
			return "04";
		} else if (month.startsWith("may")) {
			return "05";
		} else if (month.startsWith("jun")) {
			return "06";
		} else if (month.startsWith("jul")) {
			return "07";
		} else if (month.startsWith("au")) {
			return "08";
		} else if (month.startsWith("s")) {
			return "09";
		} else if (month.startsWith("o")) {
			return "10";
		} else if (month.startsWith("n")) {
			return "11";
		} else {
			return "12";
		}
	}
}
