package hs.ui.events;

import hs.ui.Control;

import java.awt.event.KeyEvent;

public class KeyPressedEvent extends ControlEvent {
  private final KeyEvent inputEvent;
  private final boolean pressed;

  public KeyPressedEvent(Control<?> source, KeyEvent inputEvent, boolean pressed) {
    super(source);
    this.inputEvent = inputEvent;
    this.pressed = pressed;
  }

  public boolean isPressed() {
    return pressed;
  }
  
  public int getKeyCode() {
    return inputEvent.getKeyCode();
  }
  
  public boolean isControlDown() {
    return inputEvent != null ? inputEvent.isControlDown() : false;
  }
  
  public boolean isShiftDown() {
    return inputEvent != null ? inputEvent.isShiftDown() : false;
  }
}
