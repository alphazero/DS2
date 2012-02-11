package oss.alphazero.util.ds2;

import java.util.Map;

/**
 * Implements a top-down Splay Tree based on original work
 * of Danny Sleator available at http://www.link.cs.cmu.edu/splay/
 * with partial support for {@link Map} interface.
 * <ol>
 * <li>Modified for Java 5 and later, using Java generics.</li>
 * <li>Modified API for clarity</li>
 * <li>Modified to (partially) support Map<K, V> semantics - original
 * coupled node key with node value</li>
 * <li>Null key is clearly not allowed.</li>
 * <li>Null values are allowed.</li>
 * </ol>
 * 
 * @param K SprayTree node key type
 * 
 * @author Danny Sleator <sleator@cs.cmu.edu>
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * 
 * This code is in the public domain.
 * 
 * @update:  Feb 10, 2012
 * 
 */
public class SplayTree<K extends Comparable<K>, V>
{
	// ------------------------------------------------------------------------
	// Inner class: BinaryNode
	// ------------------------------------------------------------------------
	class BinaryNode implements Map.Entry<K, V>
	{
		BinaryNode(K key, V value) {
			this.key = key;
			left = right = null;
		}

		/** node key */
		K key;
		/** node value */
		V value;
		/** left child */
		BinaryNode left;
		/** right child */
		BinaryNode right;
		/* (non-Javadoc) @see java.util.Map.Entry#getKey() */
		@Override final
		public K getKey() {
			return key;
		}
		/* (non-Javadoc) @see java.util.Map.Entry#getValue() */
		@Override final
		public V getValue() {
			return value;
		}
		/* (non-Javadoc) @see java.util.Map.Entry#setValue(java.lang.Object) */
		@Override final
		public V setValue(V value) {
			V oldv = value;
			this.value = value;
			return oldv;
		} 
	}
	
	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------
	/** root node (initially null) */
	private BinaryNode root;

	/** header node (changed from static - jh) */
	private final BinaryNode header = new BinaryNode(null, null); // For splay
	
	/** number of key-value mappings */
	private int size;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public SplayTree() {
		root = null;
	}

	// ------------------------------------------------------------------------
	// Public API
	// ------------------------------------------------------------------------
	/**
	 * Insert into the key-value mapping into the tree. Size is incremented.
	 * @param key the item to insert.
	 * @return true if successfully added; false if item is already present.
	 * @throws IllegalArgumentException if key is null
	 */
	final public boolean insert(K key, V value) throws IllegalArgumentException {
		if(key == null)
			throw new IllegalArgumentException("null key");
		
		// if empty then just add it
		if (isEmpty()) {
			root = new BinaryNode(key, value);
			++size;
			return true;
		}

		splay(key);

		// key is already present
		int c;
		if ((c = key.compareTo(root.key)) == 0) {
			return false;
		}
		
		// insert new node
		BinaryNode n = new BinaryNode(key, value);
		if (c < 0) {
			n.left = root.left;
			n.right = root;
			root.left = null;
		} else {
			n.right = root.right;
			n.left = root;
			root.right = null;
		}
		root = n;
		++size;
		
		return true;
	}

	/**
	 * Remove node from the tree.  Note that a splay operation
	 * is performed on tree even if the key does not exist.  
	 * 
	 * @param key of the node to remove.
	 * @return true if key was found and removed. false otherwise.
	 */
	final public boolean remove(K key) {
		// splay the tree - if key exists the root will be key
		splay(key);
		if (key.compareTo(root.key) != 0) {
			return false; // not found
		}
		
		// key exists and is root - delete it
		if (root.left == null) {
			root = root.right;
		} else {
			final BinaryNode x = root.right;
			root = root.left;
			splay(key);
			root.right = x;
		}
		--size;
		
		return true;
	}

	/**
	 * @return the smallest item in tree; null if empty
	 */
	final public K minKey() {
		BinaryNode x = root;
		if(root == null) 
			return null;
		while(x.left != null) 
			x = x.left;
		
		splay(x.key);
		
		return x.key;
	}

	/**
	 * @return the largest key in the tree; null if empty
	 */
	final public K maxKey() {
		if(isEmpty()) 
			return null;
		
		BinaryNode x = root;
		while(x.right != null) 
			x = x.right;
		
		splay(x.key);
		
		return x.key;
	}

	/**
	 * Find a key in the tree. Splay operation is applied
	 * to tree regardless of whether item exists or not.
	 * @return true if contained; false otherwise
	 * 
	 * REVU (jh): this method should just return boolean.
	 * REVU (jh): renamed to contains
	 * REVU (jh): nope - lets rename it back to find
	 *            and introduce contains using find.
	 */
	final public boolean contains(K key) {
		if (isEmpty()) 
			return false;

		splay(key);
		
		if(root.key.compareTo(key) != 0) 
			return false;
		
		return true;
	}

	/**
	 * Test if the tree is logically empty.
	 * @return true if empty, false otherwise.
	 */
	final public boolean isEmpty() {
		return root == null;
	}

	// ------------------------------------------------------------------------
	// Inner Ops
	// ------------------------------------------------------------------------
	/** 
	 * This method just illustrates the top-down method of
	 * implementing the move-to-root operation and <b>is not used
	 * in this version</b>. 
	 */
	@SuppressWarnings("unused")
	private void moveToRoot(K key) {
		BinaryNode l, r, t;
		l = r = header;
		t = root;
		header.left = header.right = null;
		for (;;) {
			if (key.compareTo(t.key) < 0) {
				if (t.left == null) break;
				r.left = t;                                 /* link right */
				r = t;
				t = t.left;
			} else if (key.compareTo(t.key) > 0) {
				if (t.right == null) break;
				l.right = t;                                /* link left */
				l = t;
				t = t.right;
			} else {
				break;
			}
		}
		l.right = t.left;                                   /* assemble */
		r.left = t.right;
		t.left = header.right;
		t.right = header.left;
		root = t;
	}

	/**
	 * Internal method to perform a top-down splay.
	 * 
	 *   splay(key) does the splay operation on the given key.
	 *   If key is in the tree, then the BinaryNode containing
	 *   that key becomes the root.  If key is not in the tree,
	 *   then after the splay, key.root is either the greatest key
	 *   < key in the tree, or the lest key > key in the tree.
	 *
	 *   This means, among other things, that if you splay with
	 *   a key that's larger than any in the tree, the rightmost
	 *   node of the tree becomes the root.  This property is used
	 *   in the delete() method.
	 */

	private void splay(K key) {
		BinaryNode l, r, t, y;
		l = r = header;
		t = root;
		header.left = header.right = null;
		for (;;) {
			if (key.compareTo(t.key) < 0) {
				if (t.left == null) break;
				if (key.compareTo(t.left.key) < 0) {
					y = t.left;                            /* rotate right */
					t.left = y.right;
					y.right = t;
					t = y;
					if (t.left == null) break;
				}
				r.left = t;                                 /* link right */
				r = t;
				t = t.left;
			} else if (key.compareTo(t.key) > 0) {
				if (t.right == null) break;
				if (key.compareTo(t.right.key) > 0) {
					y = t.right;                            /* rotate left */
					t.right = y.left;
					y.left = t;
					t = y;
					if (t.right == null) break;
				}
				l.right = t;                                /* link left */
				l = t;
				t = t.right;
			} else {
				break;
			}
		}
		l.right = t.left;                                   /* assemble */
		r.left = t.right;
		t.left = header.right;
		t.right = header.left;
		root = t;
	}

	final public int size() {
		return size;
	}
	// ------------------------------------------------------------------------
	// Ad-hoc Tests
	// ------------------------------------------------------------------------
	
	// test code stolen from Weiss
	// cleaned up to use typesafe form - jh
	public static void main(String [ ] args)
	{
		SplayTree<Integer, String> t = new SplayTree<Integer, String>();
		final int NUMS = 40000;
		final int GAP  =   307;

		System.out.format("Running 'Weiss' ad-hoc tests with NUMS:%s GAP:%s\n", NUMS, GAP);
		System.out.format("*** NOTE: enable assert with Java -ea ...*** \n\n");

		int cnt = 0;
		// test inserts
		for(int i = GAP; i != 0; i = (i + GAP) % NUMS){
			boolean r = t.insert(i, String.format("%d-value", i).toString());
			assert r : "on insert " + i;
			cnt++;
		}
		assert cnt == t.size() : "size and insert count mistmatch";
		System.out.format(" - %d Inserts successfully completed\n", cnt);
		

		// test removes
		int remcnt = 0;
		for(int i = 1; i < NUMS; i+= 2) {
			boolean r = t.remove(i);
			assert r : "on remove of " + i;
			remcnt++;
			cnt--;
		}
		assert cnt == t.size() : "size and updated count after remove mistmatch";
		System.out.format(" - %d Removes successfully completed\n", remcnt);
		System.out.format(" - %d items now in tree\n", t.size());

		// test min and max keys
		Integer maxkey = t.maxKey();
		assert maxkey != null : "max is null";

		Integer minkey = t.minKey();
		assert minkey != null : "min is null";
		
		if((minkey).intValue() != 2 || (maxkey).intValue() != NUMS - 2)
			System.err.println("FindMin or FindMax error!");
		
		System.out.format (" - (minkey:%s, maxkey:%d)\n", minkey, maxkey);
		System.out.println(" - Min/Max key tests successfully completed");

		// test for keys that should be contained
		for(int i = 2; i < NUMS; i+=2)
			if(!t.contains(i))
				System.err.println("Error: find fails for " + i);
		System.out.println(" - Positive containment tests successfully completed");

		// test for keys that should not be contained
		for(int i = 1; i < NUMS; i+=2)
			if(t.contains(i)) 
				System.err.println("Error: Found deleted item " + i);
		System.out.println(" - negative containment tests successfully completed");
	}
}