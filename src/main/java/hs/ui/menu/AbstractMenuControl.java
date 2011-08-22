package hs.ui.menu;

import hs.ui.Container;
import hs.ui.Window;

public abstract class AbstractMenuControl implements MenuControl {

  @Override
  public Window getRoot() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  private Container<MenuControl> parent;
  
  @Override
  public Container<MenuControl> getParent() {
    return parent;
  }

  @Override
  public void setParent(Container<MenuControl> parent) {
    this.parent = parent;
  }
}
