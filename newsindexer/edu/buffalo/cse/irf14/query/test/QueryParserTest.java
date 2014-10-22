package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.query.QueryParser;

public class QueryParserTest {

	private static ArrayList<String> userQuery;
	private static ArrayList<String> results;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userQuery = new ArrayList<String>();
		results = new ArrayList<String>();
		// userQuery.add("hello");
		// userQuery.add("hello world");
		// userQuery.add("\"hello world\"");
		// userQuery.add("orange AND yellow");
		// userQuery.add("(black OR blue) AND bruises");
		userQuery.add("love NOT jihad");
		// userQuery.add("Category:War AND Author:Dutt "
		// + "AND Place:Baghdad AND prisoners detainees rebels");
		// userQuery.add("(Love NOT War) AND Category:(movies NOT crime)");
		// userQuery.add("test NOT (easy AND clean)");
		//
		// results.add("{ Term:hello }");
		// results.add("{ Term:hello OR Term:world }");
		// results.add("{ Term:\"hello world\" }");
		// results.add("{ Term:orange AND Term:yellow }");
		// results.add("{ [ Term:black OR Term:blue ] AND Term:bruises }");
		// results.add("{ Author:rushdie AND <Term:jihad> }");
		// results.add("{ Category:War AND Author:Dutt AND "
		// + "Place:Baghdad AND [ Term:prisoners OR "
		// + "Term:detainees OR Term:rebels ] }");
		// results.add("{ [ Term:Love AND <Term:War> ] AND [ Category:movies AND <Category:crime> ] }");
		// results.add("{ Term:test AND [ <Term:easy> OR <Term:clean> ] }");

//		results.add("{ Term:hello }");
//		results.add("{ Term:hello OR Term:world }");
//		results.add("{ Term:\"hello world\" }");
//		results.add("{ Term:orange AND Term:yellow }");
//		results.add("{ [ Term:black OR Term:blue ] AND Term:bruises }");
		results.add("{ love AND <Term:jihad> }");
//		results.add("{ Category:War AND Author:Dutt AND "
//				+ "Place:Baghdad AND [ Term:prisoners OR "
//				+ "Term:detainees OR Term:rebels ] }");
//		results.add("{ [ Term:Love AND <Term:War> ] AND [ Category:movies AND <Category:crime> ] }");
//		results.add("{ Term:test AND [ <Term:easy> OR <Term:clean> ] }");

		testAdd("A B C D", "{ Term:A OR Term:B OR Term:C OR Term:D }");
		testAdd("A AND B AND C AND D", "{ Term:A AND Term:B AND Term:C AND Term:D }");
		testAdd("A B (C NOT D)",
				"{ [ Term:A OR Term:B ] OR [ Term:C AND <Term:D> ] }");
         
		testAdd("A NOT (B C)", "{ Term:A AND [ <Term:B> AND <Term:C> ] }");
		testAdd("(A B) AND (C D) AND (E F) AND (G H)",
				"{ [ Term:A OR Term:B ] AND [ Term:C OR Term:D ] " + 
		"AND [ Term:E OR Term:F ] AND [ Term:G OR Term:H ] }");
		testAdd("(A OR B OR C OR D) AND ((E AND F) OR (G AND H)) "
				+ "AND ((I OR J OR K) AND (L OR M OR N OR O)) AND "
				+ "(P OR (Q OR (R OR (S AND T))))",
				"{ [ Term:A OR Term:B OR Term:C OR Term:D ] AND "
						+ "[ [ Term:E AND Term:F ] OR [ Term:G AND Term:H ] ] "
						+ "AND [ [ Term:I OR Term:J OR Term:K ] AND "
						+ "[ Term:L OR Term:M OR Term:N OR Term:O ] ] AND "
						+ "[ Term:P OR [ Term:Q OR [ Term:R OR [ Term:S AND Term:T ] ] ] ] }");

	}

	protected static void testAdd(String query, String result) {
		userQuery.add(query);
		results.add(result);
	}

	@Test
	public void test() {
		for (int i = 0; i < userQuery.size(); ++i) {
			System.out.print("User Query Raw: ");
			System.out.println(userQuery.get(i));
			System.out.print("User Query Parsed: ");
			System.out.println(QueryParser.parse(userQuery.get(i), null)
					.toString());
			System.out.print("Expected Result: ");
			System.out.println(results.get(i));
			assertEquals(results.get(i),
					QueryParser.parse(userQuery.get(i), null).toString());
		}
		System.out.println("PASSED.");
	}

}
