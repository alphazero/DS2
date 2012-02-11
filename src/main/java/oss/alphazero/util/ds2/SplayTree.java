package oss.alphazero.util.ds2;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
public class SplayTree<K extends Comparable<K>, V> implements Map<K,V>
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
	 * @throws IllegalArgumentException if key is null
	 */
	final public boolean delete(K key) {
		if(key == null)
			throw new IllegalArgumentException("null key");

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
	 * Find a node in the tree. Splay operation is applied
	 * to tree regardless of whether key specified exists or not.
	 * @return the node (now root) if contained; null otherwise
	 * @throws IllegalArgumentException if key is null
	 * 
	 */
	final public BinaryNode find(K key) {
		if(key == null)
			throw new IllegalArgumentException("null key");

		if (isEmpty()) 
			return null;

		splay(key);

		if(root.key.compareTo(key) != 0) 
			return null;

		return root;
	}

	/**
	 * Test if the tree is logically empty.
	 * @return true if empty, false otherwise.
	 */
	final public boolean isEmpty() {
		return root == null;
	}


	// ------------------------------------------------------------------------
	// Public API : Map<K, V>
	// ------------------------------------------------------------------------

	/* (non-Javadoc) @see java.util.Map#containsKey(java.lang.Object) */
	@SuppressWarnings("unchecked")
	@Override final
	public boolean containsKey(Object key) {
		boolean res = false;

		if(find((K)key) != null)
			res = true;

		return res;
	}

	/* (non-Javadoc) @see java.util.Map#get(java.lang.Object) */
	@SuppressWarnings("unchecked")
	@Override final
	public V get(Object key) {
		final BinaryNode node = find((K)key);
		if(node == null)
			return null;
		
		return node.value;
	}

	/* (non-Javadoc) @see java.util.Map#put(java.lang.Object, java.lang.Object) */
	@Override final
	public V put(K key, V value) {
		final BinaryNode node = find((K)key);
		if(node == null) {
			if(!insert(key, value))
				throw new RuntimeException("BUG: find returned null but insert failed!");
			return null; // successful insert of new key per Map#put
		}
		return node.setValue(value);
	}

	/* (non-Javadoc) @see java.util.Map#remove(java.lang.Object) */
	@SuppressWarnings("unchecked")
	@Override final
	public V remove(Object key) {
		final BinaryNode node = find((K)key);
		if(node == null)
			return null; // wasn't there; null per Map#remove
		
		// delete the node - save value for return
		V value = node.value;
		if(!delete((K)key))
			throw new RuntimeException("BUG: find returned node but delete failed!");

		return value;
	}

	/* (non-Javadoc) @see java.util.Map#size() */
	@Override final
	public int size() {
		return size;
	}
	
	/* (non-Javadoc) @see java.util.Map#putAll(java.util.Map) */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(K k : m.keySet())
			insert(k, m.get(k));
	}

	/** NOT SUPPORTED */
	@Override
	public void clear() {
		throw new RuntimeException ("Map<K,V>#clear is not supported!");
	}

	/** NOT SUPPORTED */
	@Override
	public boolean containsValue(Object value) {
		throw new RuntimeException ("Map<K,V>#containsValue is not supported!");
	}
	
	/** NOT SUPPORTED */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new RuntimeException ("Map<K,V>#entrySet is not supported!");
	}

	/** NOT SUPPORTED */
	@Override
	public Set<K> keySet() {
		throw new RuntimeException ("Map<K,V>#keySet is not supported!");
	}
	
	/** NOT SUPPORTED */
	@Override
	public Collection<V> values() {
		throw new RuntimeException ("Map<K,V>#values is not supported!");
	}

	// ------------------------------------------------------------------------
	// Inner Ops
	/*
	 * Exactly per original by Dr. Sleator.  Not touched.
	 */
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
	
	// ------------------------------------------------------------------------
	// Ad-hoc Tests
	// ------------------------------------------------------------------------

	// test code stolen from Weiss
	// cleaned up to use typesafe form - jh
	// NOTE: ENABLE ASSERT!
	public static void main(String [ ] args)
	{
		SplayTree<Integer, String> t = new SplayTree<Integer, String>();
		final int NUMS = 40000;
		final int GAP  =   307;

		System.out.format("Running 'Weiss' ad-hoc tests with NUMS:%s GAP:%s\n", NUMS, GAP);
		System.out.format("*** NOTE: enable assert with Java -ea ...*** \n\n");

		// --------------------------------------
		// test null key checks
		// on insert
		boolean didcheck = false;
		try {
			t.insert(null, "woof");

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
			assert didcheck : "did not prevent remove with null key";
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
			boolean r = t.insert(i, String.format("%d-value", i).toString());
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
			if(!t.containsKey(i))
				System.err.println("Error: containsKey fails for " + i);
		System.out.println(" - Positive containment tests successfully completed");

		// --------------------------------------
		// test for keys that should not be contained
		for(int i = 1; i < NUMS; i+=2)
			if(t.find(i) != null) 
				System.err.println("Error: find fails - found deleted item " + i);
		for(int i = 1; i < NUMS; i+=2)
			if(t.containsKey(i)) 
				System.err.println("Error: containsKey fails - found deleted item " + i);
		System.out.println(" - negative containment tests successfully completed");
	}
}