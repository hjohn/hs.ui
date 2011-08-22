package hs.util.rbtree.indexed;

import hs.util.rbtree.AbstractRedBlackNode;

public class AbstractIndexedNode<E, N> extends AbstractRedBlackNode<E, N> {
  public int relativePosition;
  
  public AbstractIndexedNode(E value, N parent, int index) {
    super(value, parent);
    relativePosition = index;
  }
  
  @Override
  public String toString() {
    return "Node(pos=" + relativePosition + ",c=" + isBlack() + ",v=" + getValue() + ")";
  }
}