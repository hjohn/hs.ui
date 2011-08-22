package hs.ui.controls;

import javax.swing.JCheckBox;

public abstract class AbstractCheckBox<O> extends AbstractToggleButton<O, JCheckBox> {

  public AbstractCheckBox() {
    super(new JCheckBox());
    
//    minWidth().set(10);
    maxHeight().set(0);
  }
}
