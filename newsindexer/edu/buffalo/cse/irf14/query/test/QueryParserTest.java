package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.query.QueryParser;

public class QueryParserTest {
	
	private static String []userQuery;
	private static String []result;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		userQuery = new String[9];
		result = new String[9];
		userQuery[0] = "hello";
		userQuery[1] = "hello world";
		userQuery[2] = "\"hello world\"";
		userQuery[3] = "orange AND yellow";
		userQuery[4] = "(black OR blue) AND bruises";
		userQuery[5] = "Author:rushdie NOT jihad";
		userQuery[6] = "Category:War AND Author:Dutt AND Place:Baghdad AND prisoners detainees rebels";
		userQuery[7] = "(Love NOT War) AND Category:(movies NOT crime)";
		userQuery[8] = "test NOT (easy AND clean)";

		
		result[0] = "{ Term:hello }";
		result[1] = "{ Term:hello OR Term:world }";
		result[2] = "{ Term:\"hello world\" }";
		result[3] = "{ Term:orange AND Term:yellow }";
		result[4] = "{ [ Term:black OR Term:blue ] AND Term:bruises }";
		result[5] = "{ Author:rushdie AND <Term:jihad> }";
		result[6] = "{ Category:War AND Author:Dutt AND Place:Baghdad AND [ Term:prisoners OR Term:detainees OR Term:rebels ] }";
		result[7] = "{ [ Term:Love AND <Term:War> ] AND [ Category:movies AND <Category:crime> ] }";
		result[8] = "{ Term:test AND [ <Term:easy> OR <Term:clean> ] }";

	}

	@Test
	public void test() {
		for (int i = 0; i < 9; ++i) {
			System.out.println(result[i]);
			assertEquals(result[i], QueryParser.parse(userQuery[i], null).toString());
		}
		System.out.println("PASSED.");
	}

}
