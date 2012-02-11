package oss.alphazero.util.ds2.adhoctests;

import java.util.Map;

import oss.alphazero.util.ds2.SplayTree;
import oss.alphazero.util.ds2.SplayTreeMap;

/**
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * @date:  Feb 11, 2012
 */
public class BenchSplayTree {
	static final int NUMS = 40000;
	static final int GAP  =   307;

	public static void main(String [ ] args) {
		System.out.println ("\n###################################");
		System.out.format  ("## a silly little bench \n");
		System.out.println ("###################################\n");
		
		int iters = 100;
		benchAgainstSplayTreeMap(iters);
	}

	public static final void benchAgainstSplayTreeMap(int iters) {

		for(int i=0; i<iters; i++){
			benchTree (new SplayTree<Integer>());
		}

		System.out.println();
		
		for(int i=0; i<iters; i++){
			benchMap(new SplayTreeMap<Integer, String>());
		}
	}
	
	public static final void benchTree(SplayTree<Integer> t) {
		final long start = System.nanoTime();

		// --------------------------------------
		// test inserts
		int cnt = 0;
		for(int i = GAP; i != 0; i = (i + GAP) % NUMS){
			t.insert(i);
			cnt++;
		}
		assert cnt == t.size() : "size and insert count mistmatch";

		// --------------------------------------
		// test removes
		int remcnt = 0;
		for(int i = 1; i < NUMS; i+= 2) {
			t.delete(i);
			remcnt++;
			cnt--;
		}
		assert cnt == t.size() : "size and updated count after remove mistmatch";

		// --------------------------------------
		// test for keys that should be contained
		// using find
		for(int i = 2; i < NUMS; i+=2)
			if(!t.contains(i))
				System.err.println("Error: containsKey fails for " + i);

		// --------------------------------------
		// test for keys that should not be contained
		for(int i = 1; i < NUMS; i+=2)
			if(t.contains(i)) 
				System.err.println("Error: containsKey fails - found deleted item " + i);


		final long delta = System.nanoTime() - start;

		System.out.format("delta:%12d [mapclass:%s]\n", delta, t.getClass().getSimpleName());
	}
	
	/**
	 * Using the same value to make the bench as fair as possible.
	 * @param t
	 */
	public static final void benchMap(Map<Integer, String> t) {
		final String v = "v";
		final long start = System.nanoTime();

		// --------------------------------------
		// test inserts
		int cnt = 0;
		for(int i = GAP; i != 0; i = (i + GAP) % NUMS){
			t.put(i, v);
			cnt++;
		}
		assert cnt == t.size() : "size and insert count mistmatch";

		// --------------------------------------
		// test removes
		int remcnt = 0;
		for(int i = 1; i < NUMS; i+= 2) {
			t.remove(i);
			remcnt++;
			cnt--;
		}
		assert cnt == t.size() : "size and updated count after remove mistmatch";

		// --------------------------------------
		// test for keys that should be contained
		// using find
		for(int i = 2; i < NUMS; i+=2)
			if(!t.containsKey(i))
				System.err.println("Error: containsKey fails for " + i);

		// --------------------------------------
		// test for keys that should not be contained
		for(int i = 1; i < NUMS; i+=2)
			if(t.containsKey(i)) 
				System.err.println("Error: containsKey fails - found deleted item " + i);


		final long delta = System.nanoTime() - start;

		System.out.format("delta:%12d [mapclass:%s]\n", delta, t.getClass().getSimpleName());
	}
}
