package hs.ui.menu;

import javax.swing.JSeparator;

public class Separator extends AbstractMenuControl {
  private final JSeparator separator = new JSeparator(); 
  
  @Override
  public JSeparator getComponent() {
    return separator;
  }
}