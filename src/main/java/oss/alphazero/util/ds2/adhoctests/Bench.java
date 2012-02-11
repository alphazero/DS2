package oss.alphazero.util.ds2.adhoctests;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import oss.alphazero.util.ds2.SplayTree;

/**
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * @date:  Feb 11, 2012
 */
public class Bench {
	static final int NUMS = 40000;
	static final int GAP  =   307;

	public static void main(String [ ] args) {
		System.out.println ("\n###################################");
		System.out.format  ("## a silly little bench \n");
		System.out.println ("###################################\n");
		
		for(int i=0; i<100; i++){
			benchAgainstJdkMaps();
			System.out.println();
		}
	}

	public static final void benchAgainstJdkMaps() {

		benchMap (new TreeMap<Integer, String>());
		benchMap (new HashMap<Integer, String>());
		benchMap (new SplayTree<Integer, String>());

	}
	public static final void benchMap(Map<Integer, String> t) {
		final long start = System.nanoTime();

		// --------------------------------------
		// test inserts
		int cnt = 0;
		for(int i = GAP; i != 0; i = (i + GAP) % NUMS){
			t.put(i, String.format("%d-value", i).toString());
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
