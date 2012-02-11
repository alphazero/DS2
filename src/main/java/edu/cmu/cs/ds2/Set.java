package edu.cmu.cs.ds2;

import java.util.*;

/* This purely functional implementation of AVL trees is 
   adapted from set.ml in the standard ocaml distribution.
 
   Note that s.insert(x) returns a new set obtained by adding x to s,
   as does s.delete(x).  These operations do not change the set s,
   but instead return a new set modified accordingly.

   Danny Sleator, January 2011
*/

public class Set<E extends Comparable<E>> implements Iterable<E> {
    /* Here's the public interface to this class: 
       constructors (all O(1) time):

         Set(E x)                   a new singleton set containing x
         Set()                      a new empty set
         Set(Set s)                 make a new copy of a set
       
       Operations (all O(log n) time unless otherwise specified):

         boolean isEmpty()          returns true if the set is empty.  O(1) time
         int size()                 returns the size of the set. O(n) time
         boolean contains(E x)      returns true if x is in the set
         Set<E> insert(E x)         adds x to the set
         Set<E> delete(E x)         delete x from the set if it is in the set
         E min()                    return the first (minimum) in the set
         E max()                    return the last (maximum) in the set
         Set tailSet(E x, boolean inclusive)   new set of all elements < x (<= if inclusive=true)
         Set headSet(E x, boolean inclusive)   new set of all elements > x (>= if inclusive=true)
         Set join(Set l, Set r)     a new set that is the union of these sets. all elements of l must be < all elements of r
         Iterator<E> iterator()     returns an iterator for the elements of this set
    */

    private class Node {
	final E v;
	final int h;
	final Node l, r;
	Node(Node ll, E vv, Node rr) {
	    l = ll; v = vv; r = rr;
	    h = 1+Math.max (height(l), height(r));
	}
    }

    private Node root;

    /* This is the "balance factor" -- the maximum allowed height difference
     * between siblings.  Setting it to larger values, increases the worst-case
     * depth of the tree, but reduces the amount of rebalancing that happens.
    */
    static final int B=1;  

    // Constructors:
    Set(E x) { root = new Node(null, x, null); }
    Set() { root = null; }
    Set(Set<E> s) { root = s.root; }  /* copy a set */
    private Set(Node r) { root = r; } /* Take a node and turn it into a set. */


    private int height (Node t) {
	return (t==null)? 0: t.h;
    }

    /**
     * This method returns true if the set is empty
     */
    public boolean isEmpty() {
	return root==null;
    }

    /**
     * This method returns the number of elements in the set.
     * It runs in O(n) time for a set of size n.
     */
    public int size() {
	return sizeAux(root);
    }
    
    private int sizeAux(Node t) {
	if(t==null) return 0;
	return sizeAux(t.l) + 1 + sizeAux(t.r);
    }
    
    /**
     * This method returns true if x in the set.
     */
    boolean contains(E x) {
	return containsAux(x, root);
    }

    private boolean containsAux(E x, Node t) {
	if (t==null) return false;
	int c = x.compareTo(t.v);
	if (c==0) return true;
	if (c<0) return containsAux(x, t.l);
	else return containsAux(x, t.r);
    }

    /**
     * This method returns the largest element in the set
     * that is < x.  If there is no such element, then it
     * returns null.
     */
    public E predecessor(E x) {
        return predAux(root, x, null);
    }

    private E predAux(Node t, E x, E best) {
        if (t==null) return best;
        int c = x.compareTo(t.v);
        if (c>0) return predAux(t.r, x, t.v);
        else return predAux(t.l, x, best);
    }

    /**
     * This method returns the smallest element in the set
     * that is > x.  If there is no such element, then it
     * returns null.
     */
    public E successor(E x) {
        return succAux(root, x, null);
    }

    private E succAux(Node t, E x, E best) {
        if (t==null) return best;
        int c = x.compareTo(t.v);
        if (c<0) return succAux(t.l, x, t.v);
        else return succAux(t.r, x, best);
    }

    
    /* Creates a new node with left child l, value v and right child r.
       We must have all elements of l < v < all elements of r.
       l and r must be balanced and | height(l) - height(r) | <= B. 
    */
    private Node create(Node l, E v, Node r) {
	return new Node(l,v,r);
    }
    
    /* Same as create, but performs one step of rebalancing if necessary.
       Assumes l and r balanced and | height(l) - height(r) | <= B+1.
    */
    private Node bal(Node l, E v, Node r) {
	int hl = height(l);
	int hr = height(r);
	if (hl > hr + B) {
	    if (l==null) throw new RuntimeException("balance error");
	    if (height(l.l) >= height(l.r)) {
		return create (l.l, l.v, create(l.r, v, r));
	    } else {
		if (l.r==null) throw new RuntimeException("balance error");
		return create (create(l.l, l.v, l.r.l), l.r.v, create(l.r.r, v, r));
	    }
	} else if (hr > hl + B) {
	    if (height(r.r) >= height(r.l)) {
		return create(create(l,v,r.l), r.v, r.r);
	    } else {
		if (r.l==null) throw new RuntimeException("balance error");
		return create(create(l,v,r.l.l), r.l.v, create(r.l.r, r.v, r.r));
	    }
	} else {
	    return create(l,v,r);
	}
    }
    
    /**
     * This method inserts x into the set.  Nothing happens
     * if x is already in the set.
     */
    Set<E> insert(E x) {
	return new Set<E> (insertAux (x, root));
    }

    private Node insertAux (E x, Node t) {
	if (t==null) return create(null, x, null);
	int c = x.compareTo(t.v);
	if (c==0) return t;
	if (c<0) return bal (insertAux(x, t.l), t.v, t.r);
	else return bal (t.l, t.v, insertAux(x, t.r));
    }
    
    /**
     * This method returns the minimum element of the set.
     */
    public E min() {
	if (root==null) throw new RuntimeException("not found");
	return minAux(root);
    }

    private E minAux(Node t) {
	if (t.l==null) return t.v;
	return minAux(t.l);
    }

    /**
     * This method returns the maximum element of the set.
     */    
    public E max() {
	if (root==null) throw new RuntimeException("not found");
	return maxAux(root);
    }

    private E maxAux(Node t) {
	if (t.r==null) return t.v;
	return maxAux(t.r);
    }

    private Node deleteMin (Node t) {
	if (t==null) throw new RuntimeException("deleteMin error");
	if (t.l==null) return t.r;
	return bal (deleteMin(t.l), t.v, t.r);
    }
    
    /* Merge two trees l and r into one.
       All elements of l must precede the elements of r.
       Assume | height l - height r | <= B.
    */
    
    private Node merge(Node t1, Node t2) {
	if (t1==null) return t2;
	if (t2==null) return t1;
	return bal(t1, minAux(t2), deleteMin(t2));
    }

    /**
     * This method deletes x from the set.  Nothing happens if x
     * is not in the set.
     */
    Set<E> delete(E x) {
	return new Set<E>(deleteAux (x, root));
    }
    
    private Node deleteAux (E x, Node t) {
	if (t==null) return null;
	int c = x.compareTo(t.v);
	if (c==0) return merge(t.l, t.r);
	if (c<0) return bal (deleteAux(x,t.l), t.v, t.r);
	else return bal(t.l, t.v, deleteAux(x, t.r));
    }
    
    /* the following two methods are for joining two sets, where
       the first is less than the second */

    /**
     * This method joins two sets together.  All the elements
     * of the first must be less than all the elements of the second.
     * Throws an exception if this is not the case.
     */
    public Set<E> join(Set<E> s2) {
	return new Set<E>(joinAux(root, s2.root));
    }
    
    private Node joinAux(Node t1, Node t2) {
	if (t1==null) return t2;
	if (t2==null) return t1;
	if (maxAux(t1).compareTo(minAux(t2)) >=0) throw new RuntimeException("improper join");
	return join3 (t1, minAux(t2), deleteMin(t2));
    }

    /* Same as create and bal, but no assumptions are made on the
       relative heights of l and r. */
    
    private Node join3 (Node l, E v, Node r) {
	if (l==null) return insertAux(v,r);
	if (r==null) return insertAux(v,l);
	if (l.h > r.h + B) return bal(l.l, l.v, join3(l.r, v, r));
	if (r.h > l.h + B) return bal(join3(l, v, r.l), r.v, r.r);
	return create(l,v,r);
    }

    private class SplitReturn {
	Node l, r;
	boolean found;
    }
    
    private SplitReturn splitAux (E x, Node t, SplitReturn sr) {
	if (t==null) {
	    sr.l = sr.r = null;
	    sr.found = false;
	} else {
	    int c = x.compareTo(t.v);
	    if (c==0) {
		sr.l = t.l;
		sr.r = t.r;
		sr.found = true;
	    } else if (c<0) {
		sr = splitAux(x, t.l, sr);
		sr.r = join3(sr.r, t.v, t.r);
	    } else {
		sr = splitAux(x, t.r, sr);
		sr.l = join3(t.l, t.v, sr.l);
	    }
	}
	return sr;
    }
    
    private Set<E> half_split(E x, boolean inclusive, boolean takehead) {
	SplitReturn sr = new SplitReturn();  /* where we put the return values from split */
	sr = splitAux(x, root, sr);
	Set<E> hs = new Set<E>((takehead)?sr.l:sr.r);
	if (inclusive && sr.found) return hs.insert(x);
	return hs;
    }

    /**
     * This returns a subset of the given set containing all elements
     * that are greater than x (greater than or equal to if inclusive
     * is true).
     */
    public Set<E> tailSet(E x, boolean inclusive) {
	return half_split(x, inclusive, false);
    }
    
    /**
     * This returns a subset of the given set containing all elements
     * that are less than x (less than or equal to if inclusive
     * is true).
     */
    public Set<E> headSet(E x, boolean inclusive) {
	return half_split(x, inclusive, true);
    }
    
    public Iterator<E> iterator () { 
	final Set<E> s = new Set<E>(root);

	return new Iterator<E>() {
	    public boolean hasNext() { return !s.isEmpty(); }
	    
	    public E next() { 
		E c = s.min();
		s.root = deleteAux(c, s.root); 
		return c; 
	    } 
	    
	    public void remove() { throw new UnsupportedOperationException(); }
	};
    }
    
    private String myToString(Node t) {
	if (t == null) return "";
	return "("+myToString(t.l) + " " + t.v + " " + myToString(t.r)+")"; 
    }
    
    public String toString() {
	return myToString(root);
    }

    private String myurl(Node t) {
	if (t == null) return "";
	return t.v+"."+myurl(t.l)+myurl(t.r);
    }
    
    private String url() {
	return "http://www.link.cs.cmu.edu/cgi-bin/splay/splay-cgi.pl/"+myurl(root)+"/";
    }

    /* return the height of the tree, or -1 if not balanced */
    int checkHeight(Node t) {  
	if (t==null) return 0;
	int lb = checkHeight(t.l);
	int rb = checkHeight(t.r);
	if (lb<0 || rb<0) return -1;
	if (Math.abs(lb-rb) > B) return -1;
	return 1+Math.max(lb,rb);
    }
    
    boolean checkBalance() {  
	return checkHeight(root) < 0;
    }

    public static void main (String[] args) {
	Set<Integer> s = new Set<Integer>();
	s = s.insert(1);
	s = s.insert(2);
	s = s.insert(3);
	s = s.insert(4);
	s = s.insert(5);
	s = s.insert(6);
	s = s.insert(7);
        System.out.println("The predecessor of 8 is "+s.predecessor(8));
        System.out.println("The successor of 3 is "+s.successor(3));

	Set<Integer> t = s.headSet(5, false);
	Set<Integer> u = s.tailSet(5, false);
	Set<Integer> v = t.join(u);
	System.out.println("s = "+s);
	System.out.println("t = "+t);
	System.out.println("u = "+u);
	System.out.println("s = "+s);
	System.out.println("v = "+v);
	if (s.checkBalance() || t.checkBalance() || u.checkBalance() || v.checkBalance()) System.out.println("balance error");
	
	// DK: added to test iterator    
	System.out.println("Using iterator to print each value in s = " + s);
	for (Integer i : s) System.out.println("   "+i);

	/*	
	Set<Integer> w = new Set<Integer>();
	for (int i=0; i<1000; i++) {
	    w = w.insert(2*i);
	    w = w.delete(2*i);
	    w = w.insert(2*i);
	    if (!w.contains(2*i) || w.contains(2*i+1)) System.out.println("contains error");
	    if (w.checkBalance()) System.out.println("balance error");
	}
	*/
	System.out.println("B="+B);
	Set<Integer> w = new Set<Integer>();
	for (int i=0; i<40; i++) {
	    w = w.insert(i);
	}
	System.out.println(w.url());
    }
}