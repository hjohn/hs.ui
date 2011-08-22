package hs.ui.frames;

import hs.models.OwnedBeanModel;
import hs.models.OwnedModel;
import hs.smartlayout.Constraints;
import hs.ui.Window;
import hs.ui.controls.GUIControl;

import javax.swing.JComponent;

public abstract class AbstractFrame<O> implements Window {
  private final Constraints layoutConstraints = new Constraints();
  
  protected abstract O self();
  
  public Constraints getLayoutConstraints() {
    return layoutConstraints;
  }

  private final OwnedBeanModel<O, Integer> minWidth = new OwnedBeanModel<O, Integer>(self(), layoutConstraints, "minWidth");
  private final OwnedBeanModel<O, Integer> maxWidth = new OwnedBeanModel<O, Integer>(self(), layoutConstraints, "maxWidth");
  private final OwnedBeanModel<O, Integer> minHeight = new OwnedBeanModel<O, Integer>(self(), layoutConstraints, "minHeight");
  private final OwnedBeanModel<O, Integer> maxHeight = new OwnedBeanModel<O, Integer>(self(), layoutConstraints, "maxHeight");
  
  public OwnedModel<O, Integer> minWidth() {
    return minWidth;
  }
  
  public OwnedModel<O, Integer> maxWidth() {
    return maxWidth;
  }

  public OwnedModel<O, Integer> minHeight() {
    return minHeight;
  }

  public OwnedModel<O, Integer> maxHeight() {
    return maxHeight;
  }
  
  @Override
  public JComponent getComponent() {
    throw new UnsupportedOperationException();
  }
  
  public abstract O add(GUIControl... controls);  // TODO should not be here
  public abstract O validate();
  public abstract void dispose();
  public abstract void toFront();

}
