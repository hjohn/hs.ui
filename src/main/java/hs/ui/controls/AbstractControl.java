package hs.ui.controls;

import hs.models.BeanModel;
import hs.models.Model;
import hs.smartlayout.Anchor;
import hs.smartlayout.Constraints;
import hs.ui.ComponentRoot;
import hs.ui.Container;
import hs.ui.Control;
import hs.ui.Window;

public abstract class AbstractControl<O> implements GUIControl {
  private final Constraints layoutConstraints = new Constraints();
  
  protected abstract O self();
  
  @Override
  public Constraints getLayoutConstraints() {
    return layoutConstraints;
  }

  private final BeanModel<Integer> minWidth = new BeanModel<Integer>(layoutConstraints, "minWidth");
  private final BeanModel<Integer> maxWidth = new BeanModel<Integer>(layoutConstraints, "maxWidth");
  private final BeanModel<Integer> minHeight = new BeanModel<Integer>(layoutConstraints, "minHeight");
  private final BeanModel<Integer> maxHeight = new BeanModel<Integer>(layoutConstraints, "maxHeight");
  private final BeanModel<Double> weightX = new BeanModel<Double>(layoutConstraints, "weightX");
  private final BeanModel<Double> weightY = new BeanModel<Double>(layoutConstraints, "weightY");
  private final BeanModel<Anchor> anchor = new BeanModel<Anchor>(layoutConstraints, "anchor");
  
  public Model<Integer> minWidth() {
    return minWidth;
  }
  
  public Model<Integer> maxWidth() {
    return maxWidth;
  }

  public Model<Integer> minHeight() {
    return minHeight;
  }

  public Model<Integer> maxHeight() {
    return maxHeight;
  }
  
  public Model<Double> weightX() {
    return weightX;
  }
  
  public Model<Double> weightY() {
    return weightY;
  }
  
  public Model<Anchor> anchor() {
    return anchor;
  }
  
//  protected final Notifier<O, TransferEvent<O>> transferNotifier = new Notifier<O, TransferEvent<O>>(self());
//  
//  public ListenerList<O, TransferEvent<O>> onTransfer() {
//    return transferNotifier.getListenerList();
//  }
  
  
  private Container<GUIControl> parent;
  
  @Override
  public void setParent(Container<GUIControl> parent) {
    if(this.parent != null && parent != null) {
      throw new RuntimeException(this + " already has parent " + this.parent);
    }
    this.parent = parent;
  }
  
  @Override
  public Container<GUIControl> getParent() {
    return parent;
  }
  
  @Override
  public Window getRoot() {
    Object componentRoot = this;

    /* 
     * Determine the top level in this control hierarchy which has no Control parent.  
     * If this results in null then this means that either this Control hierarchy is not 
     * (yet) displayed or this is a root-group for a Window.
     */
    
    while(!(componentRoot instanceof ComponentRoot)) {
      if(!(componentRoot instanceof Control)) {
        return null;
      }

      componentRoot = ((Control<?>)componentRoot).getParent();
    }
    
    return ((ComponentRoot)componentRoot).getWindow();
  }
}
