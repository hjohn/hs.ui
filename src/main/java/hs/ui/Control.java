package hs.ui;

/**
 * Controls are components that can be contained within a ComponentRoot or a Container.
 * 
 * @param <T> type of control
 */
public interface Control<T> extends Component {
  Window getRoot();
  Container<T> getParent();
  void setParent(Container<T> parent);
}
