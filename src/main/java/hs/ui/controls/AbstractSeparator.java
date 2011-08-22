package hs.ui.controls;

import hs.ui.Orientation;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public abstract class AbstractSeparator<O> extends AbstractJComponent<O, JSeparator> {

  public AbstractSeparator(Orientation orientation) {
    super(new JSeparator(orientation == Orientation.HORIZONTAL ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL));
  }
}
