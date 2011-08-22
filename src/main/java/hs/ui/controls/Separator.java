package hs.ui.controls;

import hs.ui.Orientation;

public class Separator extends AbstractSeparator<Separator> {

  public Separator(Orientation orientation) {
    super(orientation);
    
    weightX().set(0.0);
    weightY().set(0.0);
  }

  @Override
  protected Separator self() {
    return this;
  }

}
