package edu.buffalo.cse.irf14.scorer.test;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.scorer.Okapi;
import edu.buffalo.cse.irf14.scorer.ScoreModel;

public class OkapiTest {

	final private static int docNum = 3;
	final private static int avgDocLen = 7;
	private static Okapi okapi;
	private static List<Integer> docIDs;

	private static Method method1 = null;
	private static Method method2 = null;
	private static Method method3 = null;
	private static Method method4 = null;
	
	@BeforeClass
	public static void setupBeforeClass() {
		try {
			method1 = Okapi.class.getDeclaredMethod("setDocLength", Integer.class, Integer.class);
			method2 = ScoreModel.class.getDeclaredMethod("setDocFreq", Long.class);
			method3 = ScoreModel.class.getDeclaredMethod("setDocTermFreq", Integer.class, Long.class);
			method4 = ScoreModel.class.getDeclaredMethod("setQueryTermFreq", Long.class);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		method1.setAccessible(true);
		method2.setAccessible(true);
		method3.setAccessible(true);
		method4.setAccessible(true);
		okapi = new Okapi(3 * docNum, avgDocLen);
		docIDs = new LinkedList<Integer>();
		for (int i = 0; i < docNum; ++i) {
			docIDs.add(i);
			try {
				method1.invoke(okapi, i, (7 * (i + 1) + 2) % 11);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		try {
			method2.invoke(okapi, df);
			for (int i = 0; i < docNum; ++i) {
				method3.invoke(okapi, i, (tfd * (3 + i)) % 7);
			}
			method4.invoke(okapi, tfq);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
