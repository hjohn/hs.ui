package hs.ui.menu;

import java.util.Iterator;

import hs.styles.Styler;
import hs.ui.Container;

import javax.swing.JMenu;

public class Menu extends AbstractMenuControl implements Container<MenuControl> {
  private final JMenu menu = new JMenu();
  
  public Menu(String title) {
    menu.setText(title);
  }

  @Override
  public void add(MenuControl... menuControls) {    
    for(MenuControl menuControl : menuControls) {
      menuControl.setParent(this);
      menu.add(menuControl.getComponent());
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
  public JMenu getComponent() {
    return menu;
  }

  @Override
  public void visitStyler(Styler styler) {
    throw new UnsupportedOperationException("Method not implemented");
  }
}
