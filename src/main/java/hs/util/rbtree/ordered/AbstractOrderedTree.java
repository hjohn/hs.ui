package hs.util.rbtree.ordered;

import hs.util.rbtree.AbstractRedBlackNode;
import hs.util.rbtree.AbstractRedBlackTree;

import java.util.Comparator;

/**
 * Extends {@link AbstractRedBlackTree} with support to find and insert nodes
 * using a {@link Comparator}.  This allows construction of trees that keeps
 * its elements in a specific order.<p>
 * 
 * Note: Care must be taken when not always using the same comparator when
 * calling methods of this class.
 * 
 * @author John Hendrikx
 *
 * @param <E> the element type
 * @param <N> the node type
 */
public abstract class AbstractOrderedTree<E, N extends AbstractRedBlackNode<E, N>> extends AbstractRedBlackTree<E, N> {
  
  /**
   * Creates a new node.  Implementations should override this method to 
   * provide the appropriate type of node.
   * 
   * @param value the value to store in the node
   * @param parent the parent of the node
   * @return a node
   */
  protected abstract N createNode(E value, N parent);
  
  /**
   * Finds a node matching the given value as interpreted by the comparator,
   * assuming the comparator matches the tree's order (see 
   * {@link #insert(Object, Comparator)} for more information).  Returns 
   * <code>null</code> if no exact match exists.
   * 
   * @param value the value to match against
   * @param comparator the comparator which interprets the values 
   * @return a matching node or <code>null</code> if none was found
   */
  public N findNode(E value, Comparator<E> comparator) {
    N node = root;
    
    while(node != null) {
      int comparison = comparator.compare(value, node.getValue());
      
      if(comparison < 0) {
        node = node.getLeft();
      }
      else if(comparison > 0) {
        node = node.getRight();
      }
      else {
        break;
      }
    }
    
    return node;
  }
  
  /**
   * Inserts a value into the tree, where its position is determined by the 
   * comparator.  A new node is created unless one with the same value
   * already exists.  In the latter case, the value of the node is merely 
   * updated.<p>
   * 
   * Note that it is important that the same comparator is used when
   * inserts are done in the tree as otherwise the tree's order will become
   * inconsistent.  When a tree's order is inconsistent, {@link #findNode(Object, Comparator)} will not
   * produce conclusive results.
   * 
   * @param value the value to insert
   * @param comparator the comparator which interprets the values  
   * @return the newly created or updated node
   */
  public N insert(E value, Comparator<E> comparator) {
    N node = root;
    
    if(node == null) {
      root = createNode(value, null);
      return root;
    }
    
    N parent;
    int comparison;
    
    do {
      parent = node;
      comparison = comparator.compare(value, node.getValue());
      
      if(comparison < 0) {
        node = node.getLeft();
      }
      else if(comparison > 0) {
        node = node.getRight();
      }
      else {
        node.setValue(value);
        return node;
      }
    } while(node != null);
    
    N newNode = createNode(value, parent);
    
    if(comparison < 0) {
      parent.setLeft(newNode);
    }
    else {
      parent.setRight(newNode);
    }
    
    restoreInvariantsAfterInsertion(newNode);
    
    return newNode;
  }
}
