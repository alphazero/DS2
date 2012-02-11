package oss.alphazero.util.ds2.adhoctests;

import java.util.SortedSet;
import java.util.TreeSet;

import oss.alphazero.util.ds2.SplayTree;

/**
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * @date:  Feb 11, 2012
 */
public class TrySplayTree {
	static final int NUMS = 40000;
	static final int GAP  =   307;

	public static void main(String[] args) {
		trySplayTreeOfStrings();
		tryAFewStrings();
	}
	public static void tryAFewStrings () {

		String[] data = {
			"kiyoumars", "farideh", "azita", "joubin", "azadeh", "kermit", "azadeh", "felix", "charlie" 
		};
		
		System.out.println ("\n###################################");
		System.out.format  ("## tests SplayTree<String>\n");
		System.out.println ("###################################\n");

		SortedSet<String> set = new TreeSet<String>();
		SplayTree<String> t = new SplayTree<String>();
		System.out.println ("\n-- populate splaytree --");
		for(String s : data) {
			if(t.insert(s))	{
				set.add(s);
				System.out.format("+ '%s'\n", s);
			}
		}
		System.out.println ("\n-- sort order --");
		for(String s : set){
			System.out.format("TS: '%s'\n", s);
		}
		String item = "farideh";
		System.out.format ("\n-- find %s and walk the splaytree --\n", item);
		SplayTree<String>.Node n = t.find(item);
		System.out.println ("-- go left --");
		goLeft(n);
		System.out.println ("-- go right --");
		goRight(n);
	}
	
	private static void goLeft(SplayTree<String>.Node n) {
		if(n == null)
			return;
		System.out.format("%s\n", n);
		if(n != null) 
			goLeft(n.left());
	}
	
	private static void goRight(SplayTree<String>.Node n) {
		if(n == null)
			return;
		System.out.format("%s\n", n);
		if(n != null) 
			goRight(n.right());
	}
	public static void trySplayTreeOfStrings () {

		System.out.println ("\n###################################");
		System.out.format  ("## tests SplayTree<String>\n");
		System.out.println ("###################################\n");

		SplayTree<String> t = new SplayTree<String>();

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
			boolean r = t.insert(String.valueOf(i));
			assert r : "on insert of node " + i;
			cnt++;
		}
		assert cnt == t.size() : "size and insert count mistmatch";
		System.out.format(" - %d inserts nodes successfully completed\n", cnt);

		// --------------------------------------
		// test removes
		int remcnt = 0;
		for(int i = 1; i < NUMS; i+= 2) {
			boolean r = t.delete(String.valueOf(i));
			assert r : "on delete of node " + i;
			remcnt++;
			cnt--;
		}
		assert cnt == t.size() : "size and updated count after remove mistmatch";
		System.out.format(" - %d delete nodes successfully completed\n", remcnt);
		System.out.format(" - %d items now in tree\n", t.size());

		// --------------------------------------
		// test min and max keys
		String maxkey = t.maxKey();
		assert maxkey != null : "max is null";

		String minkey = t.minKey();
		assert minkey != null : "min is null";

		System.out.format (" - (minkey:%s, maxkey:%s)\n", minkey, maxkey);
		
		/* NOTE
		 * String views value "10" to be smaller than "2", so minKey will return "10".
		 * This is consistent with java sort order of strings but obviously does not
		 * map to natural numbers. Same issue applies to max values. "39998" is considerd
		 * less than "9998".  So, here the original test is modified to reflect that.
		 * Naturally we can create the keys using String.format("%06d", i).toString() and
		 * use the original test but this could be a gotcha and it is left here to highlight
		 * it.
		 */
		if(!(minkey.equals("10") && maxkey.equals("9998")))
			System.err.println("FindMin or FindMax error!");

		System.out.println(" - Min/Max key tests successfully completed");

		// --------------------------------------
		// test for keys that should be contained
		// using find
		for(int i = 2; i < NUMS; i+=2)
			if(t.find(String.valueOf(i)) == null)
				System.err.println("Error: find fails for " + i);
		for(int i = 2; i < NUMS; i+=2)
			if(!t.contains(String.valueOf(i)))
				System.err.println("Error: contains fails for " + i);
		System.out.println(" - Positive containment tests successfully completed");

		// --------------------------------------
		// test for keys that should not be contained
		for(int i = 1; i < NUMS; i+=2)
			if(t.find(String.valueOf(i)) != null) 
				System.err.println("Error: find fails - found deleted item " + i);
		for(int i = 1; i < NUMS; i+=2)
			if(t.contains(String.valueOf(i))) 
				System.err.println("Error: contains fails - found deleted item " + i);
		System.out.println(" - negative containment tests successfully completed");
	}
}
