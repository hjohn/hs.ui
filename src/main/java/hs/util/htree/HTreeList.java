package hs.util.htree;

import hs.util.hash.HashList;
import hs.util.rbtree.AbstractRedBlackTree;
import hs.util.rbtree.indexed.AbstractIndexedTree;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.TreeMap;

/**
 * Tree based implementation of the <code>List</code> interface.  Does not permit 
 * <code>null</code> elements.<p>
 * 
 * When the restrictions are observed, this implementation will provide O(log n)
 * performance for <code>add</code>, <code>remove</code>, <code>get</code>, 
 * <code>set</code>, <code>contains</code>, <code>indexOf</code> and 
 * <code>lastIndexOf</code> as well as providing constant time performance for
 * <code>size</code>, <code>isEmpty</code>, <code>iterator</code> and 
 * <code>listIterator</code>. 
 * 
 * <h3>Limitations</h3>
 * 
 * Internally the elements of the list get hashed to provide good performance for
 * <code>contains</code>, <code>indexOf</code> and <code>lastIndexOf</code>.  This
 * means that {@link Object#hashCode()} must return sane values for the elements in
 * the list.<p>
 * 
 * If the implementation of <code>hashCode</code> is poor or the list contains many
 * duplicates, then the performance of <code>indexOf</code>, <code>lastIndexOf</code>
 * and <code>remove</code> will degrade to O(n) performance.<p>
 *  
 * This implementation will use significantly more memory than {@link ArrayList} 
 * to store its elements, but only slightly more than {@link TreeMap}.
 * 
 * @author John Hendrikx
 *
 * @param <T> the type of the elements stored in the list
 */
public class HTreeList<T> extends AbstractList<T> implements RandomAccess {
  
  /**
   * The tree used to store all elements.
   */
  private final HashedNodeTree tree = new HashedNodeTree();

  /**
   * The <code>HashList</code> used to store all elements, using 
   * <code>HTreeListNode</code> objects as elements and their
   * value as keys.
   */
  private final HashList<T> hashList = new HashList<T>();

  /**
   * The size of the list.
   */
  private int size;

  /**
   * Constructs a list containing the elements of the specified collection,
   * in the order they are returned by the collection's iterator.
   * 
   * @param collection the collection whose elements are to be placed into this list
   * @throws NullPointerException if the specified collection is null
   */
  public HTreeList(Collection<T> collection) {
    addAll(collection);
  }
  
  /**
   * Constructs a new empty list.
   */
  public HTreeList() {
  }


  /**
   * @see List#get(int)
   */
  @Override
  public T get(int index) {
    if(index < 0 || index >= size()) {
      throw new IndexOutOfBoundsException("0 <= index < " + size() + ": " + index);
    }
    return tree.getNode(index).getValue();
  } 

  /**
   * @see List#size()
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * @see List#iterator()
   */
  @Override
  public Iterator<T> iterator() {
    return listIterator(0);
  }

  /**
   * @see List#listIterator()
   */
  @Override
  public ListIterator<T> listIterator() {
    return listIterator(0);
  }

  /**
   * @see List#listIterator(int)
   */
  @Override
  public ListIterator<T> listIterator(int fromIndex) {
    checkInterval(fromIndex, 0, size());
    return new HTreeListIterator(fromIndex);
  }
  
  /**
   * The performance of this method will degrade to O(n) time if the
   * element searched for is one of many duplicates.
   *
   * @see List#indexOf(Object)
   */
  @Override
  public int indexOf(Object o) {
    HTreeListNode<T> node = (HTreeListNode<T>)hashList.getEntry(o);
    
    if(node == null) {
      return -1;
    }

    int lowestIndex = Integer.MAX_VALUE;
    
    do {
      int index = tree.indexOf(node);
      
      if(index < lowestIndex) {
        lowestIndex = index;
      }
      
      do {
        node = (HTreeListNode<T>)node.getNextEntry();
      } while(node != null && !node.getValue().equals(o));
    } while(node != null);
    
    return lowestIndex;
  }
  
  /**
   * The performance of this method will degrade to O(n) time if the
   * element searched for is one of many duplicates.
   *
   * @see List#lastIndexOf(Object)
   */
  @Override
  public int lastIndexOf(Object o) {
    HTreeListNode<T> node = (HTreeListNode<T>)hashList.getEntry(o);
    
    if(node == null) {
      return -1;
    }

    int highestIndex = -1;
    
    do {
      int index = tree.indexOf(node);
      
      if(index > highestIndex) {
        highestIndex = index;
      }

      do {
        node = (HTreeListNode<T>)node.getNextEntry();
      } while(node != null && !node.getValue().equals(o));
    } while(node != null);
    
    return highestIndex;
  }

  public void printTree() {
//    for(E e : nodeMap.keySet()) {
//      Node<E> n = nodeMap.get(e);
//      if(n.parent == null && n != root) {
//        n = null;
//      }
//      else if(n.parent != null && n.parent.left != n && n.parent.right != n) {
//        n = null;
//      }
//      
//      System.out.println(e + "->" + n);
//    }
    
//    TreePrinter.printTree(8, new Tree<HTreeListNode<T>>() {
//      @Override
//      public HTreeListNode<T> getLeft(HTreeListNode<T> node) {
//        return node.getLeft();
//      }
//
//      @Override
//      public HTreeListNode<T> getRight(HTreeListNode<T> node) {
//        return node.getRight();
//      }
//
//      @Override
//      public HTreeListNode<T> getRoot() {
//        return tree.getRoot();
//      }
//
//      @Override
//      public String toString(HTreeListNode<T> node) {
//        return (node.isBlack() ? "B" : "R") + "(" + node.relativePosition + ":" + node.getValue() + ")";
//        //return node.toString();
//      }
//    });

//    System.err.println("ROOT = " + root2 + " ; size = " + size);
//    for(int i = 0; i < size; i++) {
//      Node<E> node = getNode(i);
//      System.err.println("[" + i + "] " + node + " -> " + node.parent);
//    }
  }
  
  /**
   * @see List#contains(Object)
   */
  @Override
  public boolean contains(Object object) {
    return hashList.contains(object);
  }

  /**
   * @see List#add(int, Object)
   */
  @Override
  public void add(int index, T obj) {
    checkInterval(index, 0, size());
    modCount++;
    size++;
    nodeMapAdd(tree.insert(index, obj));
  }

  private void nodeMapAdd(HTreeListNode<T> node) {
    hashList.add(node);
  }
  
  /**
   * @see List#set(int, Object)
   */
  @Override
  public T set(int index, T obj) {
    checkInterval(index, 0, size() - 1);
    HTreeListNode<T> node = tree.getNode(index);
    T result = node.getValue();
    hashList.remove(node);
    node.setValue(obj);
    hashList.add(node);
    return result;
  }

  /**
   * @see List#remove(int)
   */
  @Override
  public T remove(int index) {
    checkInterval(index, 0, size() - 1);
    
    //Node<E> node = findNodeForDeletion(index);
    HTreeListNode<T> node = tree.getNode(index);
    T result = node.getValue();
    modCount++;
    size--;
//    System.err.println("Removing " + node);
    hashList.remove(node);
    tree.deleteNode(node);
    return result;
  }

  /**
   * @see List#remove(Object)
   */
  @Override
  public boolean remove(Object o) {
    int index = indexOf(o);
    if(index == -1) {
      return false;
    }
    remove(index);
    return true;
  }
  
  /**
   * @see List#clear()
   */
  @Override
  public void clear() {
    modCount++;
    tree.clear();
    size = 0;    // Can track size by nodeMap now 
    hashList.clear();
  }

  private void checkInterval(int index, int startIndex, int endIndex) {
    if(index < startIndex || index > endIndex) {
      throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + size());
    }
  }

  /**
   * Implementation of {@link AbstractIndexedTree} which uses 
   * {@link HTreeListNode} objects as its nodes to support hashing without 
   * using another object instance per entry. 
   */
  private final class HashedNodeTree extends AbstractIndexedTree<T, HTreeListNode<T>> {
    @Override
    protected HTreeListNode<T> createNode(T value, HTreeListNode<T> parent, int index) {
      return new HTreeListNode<T>(value, parent, index);
    }

    @Override
    public HTreeListNode<T> insert(int index, T value) {
      return super.insert(index, value);
    }

    @Override
    public void copyContents(HTreeListNode<T> a, HTreeListNode<T> b) {
      hashList.remove(a);
      b.setValue(a.getValue());
      hashList.add(b);
    }
  }

  /**
   * List iterator implementation optimized for traversing nodes.
   */
  private class HTreeListIterator implements ListIterator<T> { 
    
    /**
     * Last node that was returned from a call to next() or previous(). 
     */
    private HTreeListNode<T> lastReturnedNode;
    
    /**
     * Index of lastReturnedNode.
     */
    private int lastReturnedIndex = -1;
    
    /**
     * The index of the node that would be returned by next().
     */
    private int nextIndex;
            
    /**
     * The last known modification count the list has.
     */
    private int expectedModCount;

    /**
     * Create a ListIterator for a list.
     * 
     * @param treeList the parent list
     * @param fromIndex the index to start at
     */
    public HTreeListIterator(int fromIndex) throws IndexOutOfBoundsException {
      this.expectedModCount = modCount;
      this.lastReturnedNode = null;
      this.nextIndex = fromIndex;
    }

    /**
     * Checks the modification count of the list is the value that this object
     * expects.
     */
    private void checkModCount() {
      if(modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
    }

    @Override
    public boolean hasNext() {
      return nextIndex < size();
    }

    @Override
    public boolean hasPrevious() {
      return nextIndex > 0;
    }

    @Override
    public T next() {
      checkModCount();
      if(!hasNext()) {
        throw new NoSuchElementException();
      }
      if(lastReturnedNode == null) {
        lastReturnedNode = tree.getNode(nextIndex);
      }
      else if(lastReturnedIndex != nextIndex) {
        lastReturnedNode = AbstractRedBlackTree.successor(lastReturnedNode);
      }
      lastReturnedIndex = nextIndex++;
      return lastReturnedNode.getValue();
    }

    @Override
    public T previous() {
      checkModCount();
      if(!hasPrevious()) {
        throw new NoSuchElementException();
      }
      if(lastReturnedNode == null) {
        lastReturnedNode = tree.getNode(nextIndex - 1);
      }
      else if(lastReturnedIndex == nextIndex) {
        lastReturnedNode = AbstractRedBlackTree.predecessor(lastReturnedNode);
      }
      lastReturnedIndex = --nextIndex;
      return lastReturnedNode.getValue();
    }

    @Override
    public int nextIndex() {
      return nextIndex;
    }

    @Override
    public int previousIndex() {
      return nextIndex - 1;
    }

    @Override
    public void remove() {
      checkModCount();
      if(lastReturnedNode == null) {
        throw new IllegalStateException();
      }

      HTreeList.this.remove(lastReturnedIndex);

      if(lastReturnedIndex != nextIndex) {
        nextIndex--;
      }
      lastReturnedNode = null;
      lastReturnedIndex = -1;
      expectedModCount++;
    }

    @Override
    public void set(T obj) {
      checkModCount();
      if(lastReturnedNode == null) {
        throw new IllegalStateException();
      }
      HTreeList.this.set(lastReturnedIndex, obj);  // TODO node is known, this can be done faster
//      current.setValue(obj);
    }

    @Override
    public void add(T obj) {
      checkModCount();
      HTreeList.this.add(nextIndex, obj);
      lastReturnedNode = null;
      lastReturnedIndex = -1;
      nextIndex++;
      expectedModCount++;
    }
  }
    

  
  @SuppressWarnings("unused")
  private void checkTree() {
    HTreeListNode<T> start = tree.getRoot();
 
    if(start != null) {
      while(start.getLeft() != null) {
        start = start.getLeft();
      }
      
//      System.out.println("size = " + size());
      
      for(int i = 0; i < size(); i++) {
        try {
          if(start != tree.getNode(i)) {
            System.out.println("Inconsistent at index: " + i + "; " + start + " != " + tree.getNode(i));
            printTree();
            break;
          }
          start = AbstractRedBlackTree.successor(start);
        }
        catch(Exception e) {
          System.out.println("Inconsistent at index: " + i + "; " + start + " --> " + e);
          printTree();
          break;
        }
      }
    }
  }
}