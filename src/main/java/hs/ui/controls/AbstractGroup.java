package hs.ui.controls;

import hs.smartlayout.Anchor;
import hs.smartlayout.SmartLayout;
import hs.ui.swing.JPaintablePanel;
import hs.ui.swing.Painter;

import java.awt.Dimension;

public abstract class AbstractGroup<T> extends AbstractContainer<GUIControl, T, JPaintablePanel> {
  private final boolean vertical;
  
  private Double overrideWeightX = null;
  private Double overrideWeightY = null;

  // TODO There really should be a way to have a group that adjust weight towards its content, unless overriden by user.. crappy way implemented now... fails when children or sub-children are later added/removed or changed
  public AbstractGroup(boolean vertical, int lines, int horizontalSpacing, int verticalSpacing) {
    super(new JPaintablePanel());
    this.vertical = vertical;
    
    getComponent().setOpaque(false);
    getComponent().setLayout(new SmartLayout(vertical, lines, horizontalSpacing, verticalSpacing));
    
    weightX().set(0.0);
    weightY().set(0.0);
    anchor().set(Anchor.NORTH_WEST);
    
    getComponent().setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
  }
  
  public AbstractGroup(boolean vertical, int lines) {
    this(vertical, lines, 4, 4);
  }
  
  public void overrideWeightX(double wx) {
    overrideWeightX = wx;
  }

  public void overrideWeightY(double wy) {
    overrideWeightY = wy;
  }

  private void updateWeight() {
    double wx = 0;
    double wy = 0;
    
    for(GUIControl control : this) {
      if(vertical) {
        wx = Math.max(wx, ((AbstractControl<?>)control).weightX().get());
        wy += ((AbstractControl<?>)control).weightY().get();
      }
      else {
        wx += ((AbstractControl<?>)control).weightX().get();
        wy = Math.max(wy, ((AbstractControl<?>)control).weightY().get());
      }
    }
    
    weightX().set(overrideWeightX != null ? overrideWeightX : wx);
    weightY().set(overrideWeightY != null ? overrideWeightY : wy);
  }
  
  @Override
  protected void internalAdd(GUIControl control, int index) {
    getComponent().add(control.getComponent(), control.getLayoutConstraints(), index);
  }
  
  @Override
  protected void internalRemove(GUIControl control) {
    getComponent().remove(control.getComponent());
  }
  
  @Override
  public void add(int index, GUIControl... controls) {
    super.add(index, controls);
    updateWeight();
  }
  
  @Override
  public void remove(GUIControl... controls) {
    super.remove(controls);
    updateWeight();
  }
   
  public T setPainter(Painter painter) {
    getComponent().setPainter(painter);
    return self();
  }
}
