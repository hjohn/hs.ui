package hs.ui.controls;

import hs.styles.Styler;
import hs.ui.Container;
import hs.ui.Window;
import hs.ui.swing.JCoolBarItem;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

public abstract class AbstractCoolBarItem<O> implements Container<GUIControl>, CoolBarControl {
  private final List<GUIControl> elements = new ArrayList<GUIControl>(); // TODO duplicate with group code.
  private final JCoolBarItem coolBarItem = new JCoolBarItem();

  @Override
  public JComponent getComponent() {
    return coolBarItem;
  }
  
  protected abstract O self();
  
  @Override
  public void add(GUIControl... controls) {
    for(GUIControl control : controls) {
      elements.add(control);
      control.setParent(this);
      getComponent().add(control.getComponent());
    }
  }
  
  @Override
  public void add(int index, GUIControl... controls) {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public void remove(GUIControl... controls) {
    throw new UnsupportedOperationException("Method not implemented");
  }
  
  @Override
  public void removeAll() {
    throw new UnsupportedOperationException("Method not implemented");
  }
  
  @Override
  public Iterator<GUIControl> iterator() {
    return Collections.unmodifiableList(elements).iterator();
  }
  
  public O setInitialWidth(int width) {
    getComponent().setMinimumSize(new Dimension(width, 1));

    return self();
  }
  
  @Override
  public void visitStyler(Styler styler) {

  }
  
  private Container<CoolBarControl> parent;

  @Override
  public Container<CoolBarControl> getParent() {
    return parent;
  }

  @Override
  public Window getRoot() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  @Override
  public void setParent(Container<CoolBarControl> parent) {
    this.parent = parent;
  }
}
