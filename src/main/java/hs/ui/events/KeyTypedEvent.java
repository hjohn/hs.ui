package hs.ui.events;

import hs.ui.Control;

import java.awt.event.KeyEvent;

public class KeyTypedEvent extends ControlEvent {
  private final KeyEvent inputEvent;

  public KeyTypedEvent(Control<?> source, KeyEvent inputEvent) {
    super(source);
    this.inputEvent = inputEvent;
  }
    
  public char getTypedChar() {
    return inputEvent.getKeyChar();
  }
  
  public boolean isControlDown() {
    return inputEvent != null ? inputEvent.isControlDown() : false;
  }
  
  public boolean isShiftDown() {
    return inputEvent != null ? inputEvent.isShiftDown() : false;
  }
}
