package hs.ui.controls;

import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.events.Listener;

import javax.swing.JProgressBar;

public abstract class AbstractProgressBar<O> extends AbstractJComponent<O, JProgressBar> {

  public AbstractProgressBar() {
    super(new JProgressBar());
    
    weightX().set(1.0);
    weightY().set(0.0);
    
    getComponent().setMinimum(0);
    getComponent().setMaximum(16384);
    
    /*
     * Models -> Swing
     */
    
    indeterminate.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        getComponent().setIndeterminate(indeterminate.get());
      }
    });
    
    value.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        System.err.println("Progressbar to : " + ((int)(value.get() * 16384)));
        getComponent().setValue((int)(value.get() * 16384));
      }
    });
  }
  
  private final OwnedModel<O, Boolean> indeterminate = new OwnedValueModel<O, Boolean>(self(), false);
  
  public OwnedModel<O, Boolean> indeterminate() {
    return indeterminate;
  }
  
  private final OwnedModel<O, Float> value = new OwnedValueModel<O, Float>(self(), 0.0f);
  
  public OwnedModel<O, Float> value() {
    return value;
  }
}
