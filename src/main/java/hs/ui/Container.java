package hs.ui;

import hs.styles.Styler;

/**
 * Containers are components that can contain Controls.  They can only contain
 * other Containers if the Container also implements Control.
 *
 * @param <T> the type of container
 */
public interface Container<T> extends Component, Iterable<T> {
  void visitStyler(Styler styler);
  void add(T... controls);
  void add(int index, T... controls);
  void remove(T... controls);
  void removeAll();
}
