package hs.ui.menu;

import java.util.Iterator;

import hs.styles.Styler;
import hs.ui.ComponentRoot;
import hs.ui.Container;
import hs.ui.Window;

import javax.swing.JMenuBar;

public class MenuBar implements ComponentRoot, Container<MenuControl> {
  private final JMenuBar menuBar = new JMenuBar();
  
  @Override
  public void add(MenuControl... menuControls) {
    for(MenuControl menuControl : menuControls) {
      menuControl.setParent(this);
      menuBar.add(menuControl.getComponent());
    }
  }

  @Override
  public void add(int index, MenuControl... controls) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public void remove(MenuControl... controls) {
    throw new UnsupportedOperationException("Method not implemented");
  }
  
  @Override
  public void removeAll() {
    throw new UnsupportedOperationException("Method not implemented");
  }
  
  @Override
  public Iterator<MenuControl> iterator() {
    throw new UnsupportedOperationException("Method not implemented");
  }
  
  @Override
  public JMenuBar getComponent() {
    return menuBar;
  }

  private Window window;
  
  // MODULE LEVEL
  public void setWindow(Window window) {
    if(this.window != null) {
      throw new RuntimeException(this + " already has window " + this.window);
    }
    this.window = window;
  }  
  
  @Override
  public Window getWindow() {
    return window;
  }

  @Override
  public void visitStyler(Styler styler) {
    throw new UnsupportedOperationException("Method not implemented");
  }
}
