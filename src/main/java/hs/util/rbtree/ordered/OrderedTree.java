package hs.util.rbtree.ordered;

/**
 * Concrete implementation of {@link AbstractOrderedTree}.
 * 
 * {@inheritDoc}
 *
 * @author John Hendrikx
 */
public class OrderedTree<E> extends AbstractOrderedTree<E, OrderedNode<E>> {

  @Override
  protected OrderedNode<E> createNode(E value, OrderedNode<E> parent) {
    return new OrderedNode<E>(value, parent);
  }
}
