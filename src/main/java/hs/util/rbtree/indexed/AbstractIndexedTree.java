package hs.util.rbtree.indexed;

import hs.util.rbtree.AbstractRedBlackTree;

/**
 * Extends {@link AbstractRedBlackTree} with support for nodes with indices.  As the
 * tree is updated, the indices change accordingly.<p>
 * 
 * @author John Hendrikx
 *
 * @param <E> the element type
 * @param <N> the node type
 */
public abstract class AbstractIndexedTree<E, N extends AbstractIndexedNode<E, N>> extends AbstractRedBlackTree<E, N> {

  /**
   * Returns the index of a node.  Results are undefined if the node given
   * is not part of this tree.
   * 
   * @param indexedNode a node
   * @return the index of a node
   */
  public int indexOf(N indexedNode) {
    N node = indexedNode;
    int index = 0;
    
    while(node != null) {
      index += node.relativePosition;
      node = node.getParent();
    }
    
    return index;
  }
  
  /**
   * Returns a node at a specific index.  Results are undefined if the index
   * given does not exist.
   *    
   * @param index
   * @return
   */
  public N getNode(int index) {
    N temp = root;
    int result = index;
    
    for(;;) {
      result -= temp.relativePosition;
      if(result == 0) {
        return temp;
      }
      
      temp = result > 0 ? temp.getRight() : temp.getLeft();
    }
  }
  
  protected abstract N createNode(E value, N parent, int index);
  
  /**
   * Inserts a value at a given index into the tree.  Returns the node
   * created.
   * 
   * @param index the index where to insert
   * @param value the value to insert
   * @return a new node
   */
  protected N insert(int index, E value) {
    if(root == null) {
      root = createNode(value, null, index);
      return root;
    }
    
    N node = root;
    int currentPosition = index;
    
    for(;;) {
      currentPosition -= node.relativePosition;
      
      /*
       * currentPosition is the relative position of the insertion index compared 
       * to the current node.  If positive the tree is traversed to the right,
       * otherwise to the left.
       * 
       * The relative position of the nodes traversed is updated to reflect the
       * pending addition of a new node.
       */
      
      if(currentPosition > 0) {
        if(node.relativePosition < 0) {
          node.relativePosition--;
        }
        if(node.getRight() == null) {
          node.setRight(createNode(value, node, +1));
          node = node.getRight();
          break;
        }
        node = node.getRight();
      }
      else {
        if(node.relativePosition >= 0) {
          node.relativePosition++;
        }
        if(node.getLeft() == null) {
          node.setLeft(createNode(value, node, -1));
          node = node.getLeft();
          break;
        }
        node = node.getLeft();
      }
    }
    
    restoreInvariantsAfterInsertion(node);
    
    return node;
  }
  
  // Performance of       |    add(rnd)     add(end)  remove(rnd)  remove(end)          get      indexOf     contains      iterate
  //            HTreeList |      2265 ns      1151 ns      1928 ns       727 ns       655 ns      1177 ns       462 ns        62 ns  
  //            HTreeList |      2267 ns      1145 ns      1930 ns       802 ns       670 ns      1177 ns       462 ns        62 ns
  //            HTreeList |      2221 ns      1136 ns      1945 ns       713 ns       670 ns      1192 ns       461 ns        62 ns
  /**
   * @see hs.util.rbtree.AbstractRedBlackTree#rotateLeft(Node)
   */
  @Override
  protected void rotateLeft(N pivot) {
    N right = pivot.getRight();

    // Update relative positions

    if(right.getLeft() != null) {
      right.getLeft().relativePosition += right.relativePosition;
    }

    int rightRelativePosition = right.relativePosition;
    right.relativePosition += pivot.relativePosition;
    pivot.relativePosition = -rightRelativePosition;
    
    super.rotateLeft(pivot);
  }

  /**
   * @see hs.util.rbtree.AbstractRedBlackTree#rotateRight(Node)
   */
  @Override
  protected void rotateRight(N pivot) {
    N left = pivot.getLeft();
    
    // Update relative positions

    if(left.getRight() != null) {
      left.getRight().relativePosition += left.relativePosition;
    }
    
    int leftRelativePosition = left.relativePosition;
    left.relativePosition += pivot.relativePosition;
    pivot.relativePosition = -leftRelativePosition;
    
    super.rotateRight(pivot);
  }
  
  @Override
  protected void replaceNode(N node, N replacement) {
    if(replacement != null) {
      fixRelativePositions(replacement);
//      replacement.parent = node.parent;
      replacement.relativePosition = node.relativePosition;
    }
    else {
      fixRelativePositions(node);
    }
    
    super.replaceNode(node, replacement);
  }
  
  private static <E, N extends AbstractIndexedNode<E, N>> void fixRelativePositions(N node) {
    while(node.getParent() != null) {
      if(node.getParent().getLeft() == node) {
        if(node.getParent().relativePosition > 0) {
          node.getParent().relativePosition--;
        }
      }
      else {
        if(node.getParent().relativePosition < 0) {
          node.getParent().relativePosition++;
        }
      }
      node = node.getParent();
    }
  }
}
