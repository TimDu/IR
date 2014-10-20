package edu.buffalo.cse.irf14.query.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.buffalo.cse.irf14.query.Clause;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.Operator;

public class ClauseTest {

	private Clause clause;

	@Before
	public void setUpTerm0() throws Exception {
		clause = new Clause(null, Operator.AND);
		clause.setIndex(IndexType.AUTHOR);
		clause.setTerm("Alice", false);
	}
	
	@Before
	public void setUpTerm1() throws Exception {
		clause = new Clause(Operator.AND, null);
		clause.setIndex(IndexType.AUTHOR);
		clause.setTerm("Alice Bob", false);
	}
	@Before
	public void setUpTerm2() throws Exception {
		clause = new Clause(null, Operator.NOT);
		clause.setIndex(IndexType.PLACE);
		clause.setFirst();
		clause.setTerm("Alice Bob", true);
	}
	@Before
	public void setUpQuery1() throws Exception {
		clause = new Clause(Operator.AND, Operator.NOT);
		clause.setIndex(IndexType.TERM);
		clause.setQuery("(Alice Bob)", true, true);
	}

	@Test
	public void test() throws Exception {
		setUpTerm0();
		assertEquals("AND Author:Alice", clause.toString());
		setUpTerm1();
		assertEquals("[ Author:Alice AND Author:Bob ]", clause.toString());
		setUpTerm2();
		assertEquals("[ <Place:Alice> AND <Place:Bob> ]", clause.toString());
		setUpQuery1();
		assertEquals(Operator.NOT, clause.getStartOP());
		assertEquals(IndexType.TERM, clause.getIndex());
	}

}
