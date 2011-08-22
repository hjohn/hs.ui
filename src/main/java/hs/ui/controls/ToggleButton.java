package hs.ui.controls;

import javax.swing.JToggleButton;

public class ToggleButton extends AbstractToggleButton<ToggleButton, JToggleButton> {

  public ToggleButton() {
    super(new JToggleButton());
  }

  @Override
  protected ToggleButton self() {
    return this;
  }

}
