package hs.ui.events;

import hs.ui.Control;

import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;

// TODO only used for single items (ie, double clicks)... needs renaming
public class ItemsEvent<T> extends ControlEvent { // implements Iterable<T> {
  private final List<T> items;
  private final InputEvent inputEvent;

  public ItemsEvent(Control<?> source, T item, InputEvent inputEvent) {
    super(source);
    this.inputEvent = inputEvent;
    
    items = new ArrayList<T>();
    items.add(item);
  }
  
//  public ItemsEvent(List<T> items) {
//    this.items = items;
//  }

  public T getFirstItem() {
    return items.get(0);
  }
  
//  @Override
//  public Iterator<T> iterator() {
//    return items.iterator();
//  }
  
  public boolean isControlDown() {
    return inputEvent != null ? inputEvent.isControlDown() : false;
  }
  
  public boolean isShiftDown() {
    return inputEvent != null ? inputEvent.isShiftDown() : false;
  }
}
