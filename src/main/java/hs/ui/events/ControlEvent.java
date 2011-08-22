package hs.ui.events;

import hs.ui.Control;

public abstract class ControlEvent {
  private final Control<?> source;

  public ControlEvent(Control<?> source) {
    this.source = source;
  }

  public Control<?> getSource() {
    return source;
  }
}
