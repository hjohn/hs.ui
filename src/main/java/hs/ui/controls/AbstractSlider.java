package hs.ui.controls;

import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.Listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractSlider<T> extends AbstractJComponent<T, JSlider> {

  public AbstractSlider(int min, int max) {
    super(new JSlider());
    
    getComponent().setMinimum(min);
    getComponent().setMaximum(max);
    
    value.set(getComponent().getValue());
    
    // Swing -> Model
    
    getComponent().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if(!getComponent().getValueIsAdjusting()) {
          value.set(getComponent().getValue());
        }
      }
    });
    
    // Model -> Swing
    
    value.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        int v = value.get();
        
        if(getComponent().getValue() != v) {
          getComponent().setValue(v);
        }
      }
    });
    
    getComponent().addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if(clickJumps.get()) {
          // JOHNTODO extend to support vertical sliders
          getComponent().setValue(getComponent().getMinimum() + ((getComponent().getMaximum() - getComponent().getMinimum()) * e.getX() / getComponent().getWidth()));
        }
      }
    });
  }
  
  public AbstractSlider() {
    this(0, 100);
  }
  
  private final Model<Integer> value = new ValueModel<Integer>();

  public Model<Integer> value() {
    return value;
  }
  
  private final Model<Boolean> clickJumps = new ValueModel<Boolean>();
  
  public Model<Boolean> clickJumps() {
    return clickJumps;
  }
}
