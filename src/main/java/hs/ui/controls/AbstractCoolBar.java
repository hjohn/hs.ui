package hs.ui.controls;

import hs.ui.swing.JCoolBar;

public abstract class AbstractCoolBar<O> extends AbstractContainer<CoolBarControl, O, JCoolBar> {

  public AbstractCoolBar() {
    super(new JCoolBar());
  }

  @Override
  protected void internalAdd(CoolBarControl control, int index) {
    getComponent().add(control.getComponent(), index);
  }
  
  @Override
  protected void internalRemove(CoolBarControl control) {
    getComponent().remove(control.getComponent());
  }
}
