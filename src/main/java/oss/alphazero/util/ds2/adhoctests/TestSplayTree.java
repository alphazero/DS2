package oss.alphazero.util.ds2.adhoctests;

import oss.alphazero.util.ds2.SplayTree;

/**
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * @date:  Feb 11, 2012
 */
public class TestSplayTree {
	// ========================================================================
	// Ad-hoc Tests
	// ========================================================================
	/**
	 * "test code stolen from Weiss"
	 * (jh) modified to use typesafe form
	 * <b>NOTE: Must enable asserts with java -ea ... </b>
	 */
	static final int NUMS = 40000;
	static final int GAP  =   307;

	public static void main(String [ ] args) {
		System.out.format("Running 'Weiss' ad-hoc tests with NUMS:%s GAP:%s\n", NUMS, GAP);
		System.out.format("*** NOTE: enable assert with Java -ea ...*** \n");

		testAsSplayTree();
	}
	
	public static void testAsSplayTree () {

		System.out.println ("\n###################################");
		System.out.format  ("## tests SplayTree interface\n");
		System.out.println ("###################################\n");

		SplayTree<Integer> t = new SplayTree<Integer>();

		// --------------------------------------
		// test null key checks
		// on insert
		boolean didcheck = false;
		try {
			t.insert(null);

		} catch (IllegalArgumentException e) {
			didcheck = true;
		} finally {
			assert didcheck : "did not prevent insert with null key";
		}

		// on remove
		didcheck = false;
		try {
			t.delete(null);

		} catch (IllegalArgumentException e) {
			didcheck = true;
		} finally {
			assert didcheck : "did not prevent delete with null key";
		}

		// on find
		didcheck = false;
		try {
			t.find(null);

		} catch (IllegalArgumentException e) {
			didcheck = true;
		} finally {
			assert didcheck : "did not prevent find with null key";
		}
		System.out.println(" - null key tests successfully completed");

		// --------------------------------------
		// test inserts
		int cnt = 0;
		for(int i = GAP; i != 0; i = (i + GAP) % NUMS){
			boolean r = t.insert(i);
			assert r : "on insert of node " + i;
			cnt++;
		}
		assert cnt == t.size() : "size and insert count mistmatch";
		System.out.format(" - %d inserts nodes successfully completed\n", cnt);

		// --------------------------------------
		// test removes
		int remcnt = 0;
		for(int i = 1; i < NUMS; i+= 2) {
			boolean r = t.delete(i);
			assert r : "on delete of node " + i;
			remcnt++;
			cnt--;
		}
		assert cnt == t.size() : "size and updated count after remove mistmatch";
		System.out.format(" - %d delete nodes successfully completed\n", remcnt);
		System.out.format(" - %d items now in tree\n", t.size());

		// --------------------------------------
		// test min and max keys
		Integer maxkey = t.maxKey();
		assert maxkey != null : "max is null";

		Integer minkey = t.minKey();
		assert minkey != null : "min is null";

		if((minkey).intValue() != 2 || (maxkey).intValue() != NUMS - 2)
			System.err.println("FindMin or FindMax error!");

		System.out.format (" - (minkey:%s, maxkey:%d)\n", minkey, maxkey);
		System.out.println(" - Min/Max key tests successfully completed");

		// --------------------------------------
		// test for keys that should be contained
		// using find
		for(int i = 2; i < NUMS; i+=2)
			if(t.find(i) == null)
				System.err.println("Error: find fails for " + i);
		for(int i = 2; i < NUMS; i+=2)
			if(!t.contains(i))
				System.err.println("Error: contains fails for " + i);
		System.out.println(" - Positive containment tests successfully completed");

		// --------------------------------------
		// test for keys that should not be contained
		for(int i = 1; i < NUMS; i+=2)
			if(t.find(i) != null) 
				System.err.println("Error: find fails - found deleted item " + i);
		for(int i = 1; i < NUMS; i+=2)
			if(t.contains(i)) 
				System.err.println("Error: contains fails - found deleted item " + i);
		System.out.println(" - negative containment tests successfully completed");
	}
}
