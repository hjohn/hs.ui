package hs.ui.controls;


public class ListView<T> extends AbstractListView<ListView<T>, T> {
  
  @Override
  protected ListView<T> self() {
    return this;
  }
}
