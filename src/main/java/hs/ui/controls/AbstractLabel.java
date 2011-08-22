package hs.ui.controls;

import hs.models.Model;
import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.ValueModel;
import hs.models.events.Listener;
import hs.ui.HorizontalAlignment;

import javax.swing.Icon;
import javax.swing.JLabel;

public abstract class AbstractLabel<O> extends AbstractJComponent<O, JLabel> {
  
  public AbstractLabel() {
    super(new JLabel());
            
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
    
    horizontalAlignment.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        if(horizontalAlignment.get() == HorizontalAlignment.LEFT) {
          getComponent().setHorizontalAlignment(JLabel.LEFT);
        }
        else if(horizontalAlignment.get() == HorizontalAlignment.RIGHT) {
          getComponent().setHorizontalAlignment(JLabel.RIGHT);
        }
        else {
          getComponent().setHorizontalAlignment(JLabel.CENTER);
        }
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
  
  public final Model<HorizontalAlignment> horizontalAlignment = new ValueModel<HorizontalAlignment>(HorizontalAlignment.LEFT);
}
