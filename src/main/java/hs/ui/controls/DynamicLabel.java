package hs.ui.controls;

public class DynamicLabel extends AbstractLabel<DynamicLabel> {
  
  public DynamicLabel() {
    weightX().set(1.0);
    weightY().set(0.0);
    
    minWidth().set(1);
    maxWidth().set(Integer.MAX_VALUE);
    
    getComponent().setFocusable(false);
  }
  
  @Override
  protected DynamicLabel self() {
    return this;
  }
}
