package hs.ui.controls;

import hs.styles.Styler;
import hs.ui.Container;
import hs.ui.Control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

public abstract class AbstractContainer<T extends Control<T>, O, S extends JComponent> extends AbstractJComponent<O, S> implements Container<T> {
  private final List<T> elements = new ArrayList<T>();
  
  protected abstract void internalAdd(T control, int index);
  protected abstract void internalRemove(T control);
  
  public AbstractContainer(S component) {
    super(component);
  }
  
  @Override
  public void add(int index, T... controls) {
    for(T control : controls) {
      if(index == -1) {
        elements.add(control);
      }
      else {
        elements.add(index, control);
      }
      control.setParent(this);
      internalAdd(control, index);
    }
  }

  @Override
  public void add(T... controls) {
    add(-1, controls);
  }

  @Override
  public void remove(T... controls) {
    for(T control : controls) {
      if(elements.remove(control)) {
        internalRemove(control);
        control.setParent(null);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void removeAll() {
    remove((T[]) elements.toArray(new GUIControl[elements.size()]));
  }
  
  @Override
  public Iterator<T> iterator() {
    return Collections.unmodifiableList(elements).iterator();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void visitStyler(Styler styler) {
    styler.preVisitGroup(this);
    styler.visit(elements.toArray(new GUIControl[elements.size()]));
   
    for(T control : elements) {
      if(control instanceof Container) {
        ((Container<T>)control).visitStyler(styler);
      }
    }
    
    styler.postVisitGroup(this);
  }
  
  public void revalidate() {
    getComponent().revalidate();
  }
}
