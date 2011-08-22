package hs.ui.events;

import hs.ui.Control;

public class ActionEvent extends ControlEvent {
  public ActionEvent(Control<?> source) {
    super(source);
  }
}
