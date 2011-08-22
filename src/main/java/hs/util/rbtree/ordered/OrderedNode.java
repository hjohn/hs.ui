package hs.util.rbtree.ordered;

import hs.util.rbtree.AbstractRedBlackNode;

/**
 * Concrete implementation of {@link AbstractRedBlackNode}.
 * 
 * {@inheritDoc}
 * 
 * @author John Hendrikx
 */
public class OrderedNode<E> extends AbstractRedBlackNode<E, OrderedNode<E>> {

  public OrderedNode(E value, OrderedNode<E> parent) {
    super(value, parent);
  }

}
