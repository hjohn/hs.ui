package hs.ui.controls;

import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.events.Listener;
import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.ui.CommitBehaviour;
import hs.ui.events.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;

public abstract class AbstractTextField<O extends GUIControl> extends AbstractJComponent<O, JTextField> {
  private CommitBehaviour commitBehaviour = CommitBehaviour.FOCUS_LOST;
  
  public AbstractTextField() {
    super(new JTextField());
    
    minWidth().set(10);
    maxHeight().set(0);
    
    /*
     * Register listener for Swing->Model.
     */
    
//    For later perhaps.  Note: calling setText() will cause 2 document events, remove + insert.  Need to disable the listener during updates to prevent these.    
//    getComponent().getDocument().addDocumentListener(new DocumentListener() {
//      @Override
//      public void changedUpdate(DocumentEvent e) {
//        text.set(getComponent().getText());
//      }
//
//      @Override
//      public void insertUpdate(DocumentEvent e) {
//        System.err.println("insert");
//        text.set(getComponent().getText());
//      }
//
//      @Override
//      public void removeUpdate(DocumentEvent e) {
//        System.err.println("remove");
//        text.set(getComponent().getText());
//      }
//    });
    
    getComponent().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        text.set(getComponent().getText());
        actionNotifier.notifyListeners(new ActionEvent(self()));
        
        /*
         * Workaround for JTextField consuming VK_ENTER, resulting in the Default Button
         * not being called. 
         */
        
        JRootPane rootPane = ((JTextField)e.getSource()).getRootPane();
        JButton defaultButton = rootPane.getDefaultButton();
        
        if(defaultButton != null) {
          defaultButton.doClick();
        }
      }
    });
    
    getComponent().addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if(commitBehaviour != CommitBehaviour.ACTION_KEY) {
          text.set(getComponent().getText());
        }
      }
    });
    
    /*
     * Register listener for Model->Swing.
     */
    
    text.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        String value = text.get();
        if(!getComponent().getText().equals(value)) {
          System.err.println("setting JTextField to : " + value + " (old: " +  getComponent().getText() + ")");
          getComponent().setText(value);
        }
      }
    });
  }

  private final OwnedModel<O, String> text = new OwnedValueModel<O, String>(self());

  public OwnedModel<O, String> text() {
    return text;
  }
  
  private final Notifier<ActionEvent> actionNotifier = new Notifier<ActionEvent>();

  public ListenerList<ActionEvent> onAction() {
    return actionNotifier.getListenerList();
  }
  
  public O setCommitBehaviour(CommitBehaviour behaviour) {
    this.commitBehaviour = behaviour;
    return self();
  }
  
  public O revert() {
    getComponent().setText(text.get());
    return self();
  }
  
}
