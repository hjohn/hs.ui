package hs.util.rbtree.indexed;

public final class IndexedNode<E> extends AbstractIndexedNode<E, IndexedNode<E>> {

  public IndexedNode(E value, IndexedNode<E> parent, int index) {
    super(value, parent, index);
  }

}