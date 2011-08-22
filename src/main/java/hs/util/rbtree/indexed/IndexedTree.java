package hs.util.rbtree.indexed;

public class IndexedTree<E> extends AbstractIndexedTree<E, IndexedNode<E>> {

  @Override
  protected IndexedNode<E> createNode(E value, IndexedNode<E> parent, int index) {
    return new IndexedNode<E>(value, parent, index);
  }
}
