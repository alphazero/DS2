package oss.alphazero.util.ds2;

/**
 * Implements a top-down Splay Tree based on original work
 * of Danny Sleator available at http://www.link.cs.cmu.edu/splay/
 * 
 * Modified for Java 5 and later, using Java generics.
 * 
 * @param T SprayTree node value type
 * 
 * @author Danny Sleator <sleator@cs.cmu.edu>
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * 
 * This code is in the public domain.
 * 
 * @author Joubin Houshyar <alphazero@sensesay.net>
 * @date:  Feb 10, 2012
 * 
 */
public class SplayTree<T extends Comparable<T>>
{
	class BinaryNode
	{
		BinaryNode(T key) {
			this.key = key;
			left = right = null;
		}

		/** node value item */
		T key;
		/** left child */
		BinaryNode left;
		/** right child */
		BinaryNode right; 
	}
	/** root node (initially null) */
	private BinaryNode root;

	public SplayTree() {
		root = null;
	}

	/**
	 * Insert into the tree.
	 * @param key the item to insert.
	 * @return true if successfully added; false if item is already present.
	 */
	final public boolean insert(T key) {
		// if empty then just add it
		if (isEmpty()) {
			root = new BinaryNode(key);
			return true;
		}

		splay(key);

		int c;
		if ((c = key.compareTo(root.key)) == 0) {
			String errmsg = String.format("duplicate:", key.toString()).toString();
			throw new IllegalArgumentException(errmsg);	    
		}
		
		BinaryNode n = new BinaryNode(key);
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
		
		return true;
	}

	/**
	 * Remove item from the tree.  Note that a splay operation
	 * is performed on tree even if the key does not exist.  
	 * 
	 * @param key the item to remove.
	 * @return true if key was found and removed. false otherwise.
	 */
	final public boolean remove(T key) {
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
		return true;
	}

	/**
	 * @return the smallest item in tree; null if empty
	 */
	final public T findMin() {
		BinaryNode x = root;
		if(root == null) 
			return null;
		while(x.left != null) 
			x = x.left;
		
		splay(x.key);
		
		return x.key;
	}

	/**
	 * @return the largest item in the tree; null if empty
	 */
	final public T findMax() {
//		BinaryNode x = root;
		if(isEmpty()) 
			return null;
		
		BinaryNode x = root;
		while(x.right != null) 
			x = x.right;
		
		splay(x.key);
		
		return x.key;
	}

	/**
	 * Find an item in the tree. Splay op is applied
	 * to tree regardless of whether item exists or not.
	 * @return true if contained; false otherwise
	 * 
	 * REVU (jh): this method should just return boolean.
	 * REVU (jh): rename to contains
	 */
	final public boolean contains(T key) {
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

	/** 
	 * This method just illustrates the top-down method of
	 * implementing the move-to-root operation and is not used
	 * in this version. 
	 */
	@SuppressWarnings("unused")
	private void moveToRoot(T key) {
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

	/** header node (changed from static - jh) */
	private final BinaryNode header = new BinaryNode(null); // For splay

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

	private void splay(T key) {
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

	// test code stolen from Weiss
	// cleaned up to use typesafe form - jh
	public static void main(String [ ] args)
	{
		SplayTree<Integer> t = new SplayTree<Integer>();
		final int NUMS = 40000;
		final int GAP  =   307;

		System.out.format("Running 'Weiss' ad-hoc tests with NUMS:%s GAP:%s\n", NUMS, GAP);
		System.out.format("*** NOTE: enable assert with Java -ea ...*** \n\n");

		// test inserts
		for(int i = GAP; i != 0; i = (i + GAP) % NUMS){
			boolean r = t.insert(i);
			assert r : "on insert " + i;
		}
		System.out.println(" - Inserts successfully completed");

		// test removes
		for(int i = 1; i < NUMS; i+= 2) {
			boolean r = t.remove(i);
			assert r : "on remove of " + i;
		}
		System.out.println(" - Removes successfully completed");

		// test min and max
		Integer max = t.findMax();
		assert max != null : "max is null";

		Integer min = t.findMin();
		assert min != null : "min is null";
		
		if((min).intValue() != 2 || (max).intValue() != NUMS - 2)
			System.err.println("FindMin or FindMax error!");
		
		System.out.println(" - Min/Max tests successfully completed");

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