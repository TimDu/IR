package edu.buffalo.cse.irf14.scorer.test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.scorer.Okapi;

public class OkapiTest {

	final private static int docNum = 3;
	final private static int avgDocLen = 7;
	private static Okapi okapi;
	private static List<Integer> docIDs;
	
	@BeforeClass
	public static void setupBeforeClass() {
		
		okapi = new Okapi(3 * docNum, avgDocLen);
		docIDs = new LinkedList<Integer>();
		for (int i = 0; i < docNum; ++i) {
			docIDs.add(i);
			okapi.setDocLength(i, (7 * (i + 1) + 2) % 11);
		}
		okapi.setDocuments(docIDs);
	}
	
	@Test
	public void test() {
		setup(2, 4, 2);
		okapi.run();
		setup(8, 3, 1);
		okapi.run();
		List<Integer> result = okapi.getRankedList();
		assertEquals(docNum, result.size());
		for (int i = 0; i < result.size(); ++i) {
			assertTrue(docIDs.contains(result.get(i)));
			if (i < result.size() - 1) {
				assertTrue(okapi.getScore(i) > okapi.getScore(i + 1));
			}
			System.out.println(result.get(i) + "\t" + okapi.getTextScore(i));
		}
	}
	
	private void setup(long df, long tfd, long tfq) {
		okapi.setDocFreq(df);
		for (int i = 0; i < docNum; ++i) {
			okapi.setDocTermFreq(i, (tfd * (3 + i)) % 7);
		}
		okapi.setQueryTermFreq(tfq);
	}
}
