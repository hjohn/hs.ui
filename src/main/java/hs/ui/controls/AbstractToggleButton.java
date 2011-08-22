package hs.ui.controls;

import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.events.Listener;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractToggleButton<O, T extends JToggleButton> extends AbstractBaseButton<O, T> {

  public AbstractToggleButton(T delegate) {
    super(delegate);
    
//    minWidth().set(10);
//    maxHeight().set(0);
    
    /*
     * Register listener for Swing->Model.
     */
    
    getComponent().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        selected.set(getComponent().isSelected());
      }
    });
    
    /*
     * Register listener for Model->Swing.
     */
    
    selected.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        boolean value = selected.get();
        if(getComponent().isSelected() != value) {
          getComponent().setSelected(value);
        }
      }
    });
  }
  
  private final OwnedModel<O, Boolean> selected = new OwnedValueModel<O, Boolean>(self(), false);

  public OwnedModel<O, Boolean> selected() {
    return selected;
  }
}
