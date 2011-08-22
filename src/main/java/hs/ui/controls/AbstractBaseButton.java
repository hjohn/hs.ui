package hs.ui.controls;

import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.events.Listener;

import javax.swing.Icon;

public abstract class AbstractBaseButton<O, T extends javax.swing.AbstractButton> extends AbstractJComponent<O, T> {

  public AbstractBaseButton(T delegate) {
    super(delegate);
    
    /*
     * Register listener for Model->Swing
     */
    
    text.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        getComponent().setText(text.get());
      }
    });
   
    icon.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        getComponent().setIcon(icon.get());
      }
    });
  }

  private final OwnedModel<O, String> text = new OwnedValueModel<O, String>(self());
  
  public OwnedModel<O, String> text() {
    return text;
  }
  
  private final OwnedModel<O, Icon> icon = new OwnedValueModel<O, Icon>(self());
  
  public OwnedModel<O, Icon> icon() {
    return icon;
  }
}
