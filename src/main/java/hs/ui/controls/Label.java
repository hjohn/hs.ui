package hs.ui.controls;

public class Label extends AbstractLabel<Label> {
  
  public Label() {
    weightX().set(0.0);
    weightY().set(0.0);
  }
  
  @Override
  protected Label self() {
    return this;
  }
}
