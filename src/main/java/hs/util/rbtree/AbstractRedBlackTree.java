package hs.util.rbtree;

/**
 * An abstract implementation of a red black tree.  The class provides
 * all the basic functionality to maintain a red black tree and do operations
 * on its nodes.<p>
 * 
 * There is only support for deleting nodes from the tree.  There is no support
 * for inserting or finding nodes.  This is left up to more specific
 * implementations.<p>
 * 
 * Note: No attempt is made to enforce consistency.  It is up to the 
 * integrating class to ensure consistent use and do range and sanity checks 
 * where appropriate.
 * 
 * @author John Hendrikx
 *
 * @param <E> the element type
 * @param <N> the node type
 */
public abstract class AbstractRedBlackTree<E, N extends AbstractRedBlackNode<E, N>> {
  
  /*
   * Red Black Invariants:
   * 
   * 1) A node is either red or black
   * 2) The root is black
   * 3) All leaves are black (leaves in this implementation are null)
   * 4) Both children of a red node are black
   * 5) Every direct path from a given node to its descendant leaves has the same 
   *    number of black nodes
   *    
   * From this can be deduced:
   * 
   * a) A black node, except for the root, always has a sibling (rule 5)
   * b) A red node that has a sibling always has two black children (rule 4 + 5)
   * c) A red node without a sibling has no children (rule 4 + 5)
   */
  
  /**
   * The root node of the tree or <code>null</code> if the tree is empty.
   */
  protected N root;

  /**
   * Removes all nodes from the tree.
   */
  public void clear() {
    root = null;
  }
  
  /**
   * Returns the root node of the tree or <code>null</code> if the tree is empty.
   * 
   * @return the root node of the tree or <code>null</code> if the tree is empty
   */
  public N getRoot() {
    return root;
  }

  /**
   * Rotates nodes left around the pivot:
   * <pre>
   *   P             R
   *    \           / \
   *     R    ==>  P   S
   *      \
   *       S
   * </pre>
   * 
   * @param pivot the pivot
   */
  protected void rotateLeft(N pivot) {
    N right = pivot.getRight();
    pivot.setRight(right.getLeft());
    
    if(right.getLeft() != null) {
      right.getLeft().setParent(pivot);
    }
    
    right.setParent(pivot.getParent());
    if(pivot.getParent() == null) {
      root = right;
    }
    else if(pivot.getParent().getLeft() == pivot) {
      pivot.getParent().setLeft(right);
    }
    else {
      pivot.getParent().setRight(right);
    }
    right.setLeft(pivot);
    pivot.setParent(right);
  }

  /**
   * Rotates nodes right around the pivot:
   * <pre>
   *       P         L
   *      /         / \
   *     L    ==>  S   P
   *    /   
   *   S    
   * </pre>
   * 
   * @param pivot the pivot
   */
  protected void rotateRight(N pivot) {
    N left = pivot.getLeft();
    pivot.setLeft(left.getRight());
    
    if(left.getRight() != null) {
      left.getRight().setParent(pivot);
    }
    
    left.setParent(pivot.getParent());
    if(pivot.getParent() == null) {
      root = left;
    }
    else if(pivot.getParent().getRight() == pivot) {
      pivot.getParent().setRight(left);
    }
    else {
      pivot.getParent().setLeft(left);
    }
    left.setRight(pivot);
    pivot.setParent(left);
  }

  /**
   * Copies the contents from one node to another.<p>
   * 
   * Subclasses should override this method to copy any additional contents 
   * stored in its nodes.
   * 
   * @param src the source node
   * @param dest the destination node
   */
  protected void copyContents(N src, N dest) {
    dest.setValue(src.getValue());
  }
    
  /**
   * Replaces a node.  The replacement is linked to the node's parent.  The
   * children of either node are unaffected.  The replacement is allowed to
   * be <code>null</code> effectively unlinking the node from its parent.<p>
   * 
   * Subclasses should override this method if replacing a node requires 
   * additional values to be adjusted in a custom node class.
   * 
   * @param node the node to replace
   * @param replacement a replacement
   */
  protected void replaceNode(N node, N replacement) {
    N parent = node.getParent();
    
    if(replacement != null) {
      replacement.setParent(parent);
    }
    
    if(parent == null) {
      root = replacement;
    }
    else if(node == parent.getLeft()) {
      parent.setLeft(replacement);
    }
    else {
      parent.setRight(replacement);
    }
  }
  
  /**
   * Deletes a node and then rebalances the tree.  Note that this specific
   * node (the wrapper around the actual content) may not actually be 
   * deleted.  The implementation is free to first swap the contents with
   * another node and then delete that node instead.
   * 
   * @param node a node to be deleted
   */
  public void deleteNode(N node) {
    N p = node;
    
    /*
     * To keep deletion simple, only nodes that have 0 or 1 children are
     * actually deleted.  To delete a node with two children, it is 
     * simply given the value of its successor, and the successor is then
     * in turn deleted instead.  The successor node will always satisfy 
     * the 0 or 1 children requirement.
     */
    
    if(p.getLeft() != null && p.getRight() != null) {
      N s = successor(p);
      copyContents(s, p);
      p = s;
    }
    
    N onlyChild = p.getLeft() != null ? p.getLeft() : p.getRight();

    if(onlyChild != null) {
      
      /*
       * The node to be deleted had exactly 1 child.  Replace the node
       * with this only child.  The tree is then restored from the 
       * (child) node.
       */
      
      replaceNode(p, onlyChild);

      if(p.isBlack()) {
        restoreInvariantsAfterDeletion(onlyChild);
      }
    }
    else {
      
      /*
       * The node to be removed had no children.  Before the node is
       * unlinked the tree is first restored from that position.
       */
      
      if(p.isBlack()) {
        restoreInvariantsAfterDeletion(p);
      }
      replaceNode(p, null);
    }
    
    // checkTree();
  }
  
  
  /**
   * Restores the tree to a valid red black tree after a deletion 
   * occured.  This method assumes the deletion occured at a node
   * with 0 or 1 children.<p>
   * 
   * This method is provided for subclasses that want a customized
   * deletion routine instead of using {@link #deleteNode(AbstractRedBlackNode)}.
   *  
   * @param startNode the node that is about to be deleted or was deleted
   */
  protected void restoreInvariantsAfterDeletion(N startNode) {
    N node = startNode;
    
    while(node != root && node.isBlack()) {
      N parent = node.getParent();
      N sibling = parent.getLeft();

      if(node == sibling) {
        sibling = parent.getRight();

        if(sibling.isRed()) {
          sibling.makeBlack();
          parent.makeRed();
          rotateLeft(parent);  // rotating left does not change left's parent
//          parent = node.getParent();
          sibling = parent.getRight();
        }

        if(isBlack(sibling.getLeft()) && isBlack(sibling.getRight())) {
          sibling.makeRed();
          node = parent;
        }
        else {
          if(isBlack(sibling.getRight())) {
            sibling.getLeft().makeBlack();
            sibling.makeRed();
            rotateRight(sibling);
            parent = node.getParent();
            sibling = parent.getRight();
          }
          if(sibling != null) {
            sibling.setBlack(parent.isBlack());
            if(sibling.getRight() != null) {
              sibling.getRight().makeBlack();
            }
          }
          parent.makeBlack();
          rotateLeft(parent);
          node = root;
        }
      }
      else {
        if(sibling.isRed()) {
          sibling.makeBlack();
          parent.makeRed();
          rotateRight(parent);  // rotating right does not change right's parent
//          parent = node.getParent();
          sibling = parent.getLeft();
        }

        if(isBlack(sibling.getLeft()) && isBlack(sibling.getRight())) {
          sibling.makeRed();
          node = parent;
        }
        else {
          if(isBlack(sibling.getLeft())) {
            sibling.getRight().makeBlack();
            sibling.makeRed();
            rotateLeft(sibling);
            parent = node.getParent();
            sibling = parent.getLeft();
          }
          if(sibling != null) {
            sibling.setBlack(parent.isBlack());
            if(sibling.getLeft() != null) {
              sibling.getLeft().makeBlack();
            }
          }
          parent.makeBlack();
          rotateRight(parent);
          node = root;
        }
      }
    }

    node.makeBlack();
  }
  
  /**
   * Restores the tree, from the point of a newly inserted node, to a valid
   * red black tree.<p>
   * 
   * This method is provided for subclasses to make it easy to allow for
   * inserting nodes into the tree.
   * 
   * @param insertedNode the node that was inserted
   */
  @SuppressWarnings("null")
  protected void restoreInvariantsAfterInsertion(N insertedNode) {
    N child = insertedNode;
    child.makeRed();  // a node that is inserted is always colored red

    while(child != null && child != root && child.getParent().isRed()) {
      N parent = child.getParent();
      N grandParent = parent.getParent();
      N uncle = grandParent == null ? null : grandParent.getLeft(); 
      
      if(parent == uncle) {
        uncle = grandParent.getRight();  // generates warning, but parent cannot be null here
        
        if(isRed(uncle)) {  // uncle is not null if it is red
          parent.makeBlack();
          uncle.makeBlack();
          grandParent.makeRed();
          child = grandParent;
        }
        else {
          if(child == parent.getRight()) {
            child = parent;
            rotateLeft(child);
            parent = child.getParent();
          }
          parent.makeBlack();
          parent.getParent().makeRed();
          rotateRight(parent.getParent());
        }
      }
      else {
        if(isRed(uncle)) {  // uncle is not null if it is red
          parent.makeBlack();
          uncle.makeBlack();
          if(grandParent != null) {
            grandParent.makeRed();
          }
          child = grandParent;
        }
        else {
          if(child == parent.getLeft()) {
            child = parent;
            rotateRight(child);
            parent = child.getParent();
          }
          parent.makeBlack();
          if(parent.getParent() != null) {
            parent.getParent().makeRed();
            rotateLeft(parent.getParent());
          }
        }
      }
    }
    root.makeBlack();
  }
  
  /**
   * Returns the successor of a Node.
   * 
   * @throws NullPointerException if <code>node</code> is null.
   */
  public static <E, N extends AbstractRedBlackNode<E, N>> N successor(N node) {
    if(node.getRight() != null) {
      N successor = node.getRight();
      
      while(successor.getLeft() != null) {
        successor = successor.getLeft();
      }
      
      return successor;
    }
    else {
      N successor = node.getParent();
      N previous = node;
      
      while(successor != null && previous == successor.getRight()) {
        previous = successor;
        successor = successor.getParent();
      }
      
      return successor;
    }
  }
  
  /**
   * Returns the predecessor of a Node.
   * 
   * @throws NullPointerException if <code>node</code> is null.
   */
  public static <E, N extends AbstractRedBlackNode<E, N>> N predecessor(N node) {
    if(node.getLeft() != null) {
      N predecessor = node.getLeft();
      
      while(predecessor.getRight() != null) {
        predecessor = predecessor.getRight();
      }
      
      return predecessor;
    }
    else {
      N predecessor = node.getParent();
      N previous = node;
      
      while(predecessor != null && previous == predecessor.getLeft()) {
        previous = predecessor;
        predecessor = predecessor.getParent();
      }
            
      return predecessor;
    }
  }
  
  
  private static <E, N extends AbstractRedBlackNode<E, N>> boolean isBlack(N node) {
    return node == null || node.isBlack();
  }

  private static <E, N extends AbstractRedBlackNode<E, N>> boolean isRed(N node) {
    return node != null && !node.isBlack();
  }
}
