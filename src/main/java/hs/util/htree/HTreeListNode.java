package hs.util.htree;

import hs.util.hash.HashEntry;
import hs.util.rbtree.indexed.AbstractIndexedNode;

public final class HTreeListNode<E> extends AbstractIndexedNode<E, HTreeListNode<E>> implements HashEntry<E> {
  private HashEntry<E> next;
  
  public HTreeListNode(E value, HTreeListNode<E> parent, int index) {
    super(value, parent, index);
  }

  @Override
  public E getKey() {
    return getValue();
  }

  @Override
  public HashEntry<E> getNextEntry() {
    return next;
  }

  @Override
  public void setNextEntry(HashEntry<E> entry) {
    this.next = entry;
  }

}