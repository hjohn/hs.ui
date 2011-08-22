package hs.ui.controls.x;

import hs.ui.controls.AbstractJComponent;
import hs.ui.controls.GUIControl;

import org.jdesktop.swingx.JXStatusBar;

public abstract class AbstractStatusBar<O> extends AbstractJComponent<O, JXStatusBar> {

  public AbstractStatusBar() {
    super(new JXStatusBar());
  }

  public O add(GUIControl c, JXStatusBar.Constraint constraint) {
    getComponent().add(c.getComponent(), constraint);
    return self();
  }
  
  public O setResizeHandleEnabled(boolean enabled) {
    getComponent().setResizeHandleEnabled(enabled);
    return self();
  }
}
