package hs.ui.controls;

import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.events.Listener;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public abstract class AbstractTextArea<O> extends AbstractJComponent<O, JScrollPane> {
  private final JTextArea textArea;
  
  public AbstractTextArea() {
    super(new JScrollPane(), new JTextArea());
    
    textArea = (JTextArea)getSecondaryComponent();
    getComponent().setViewportView(textArea);
  
    minWidth().set(10);
    minHeight().set(10);
    weightX().set(1.0);
    weightY().set(1.0);
    
    /*
     * Register listener for Swing->Model.
     */
    
//    getComponent().addActionListener(new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        text.set(getComponent().getText());
//        
//        /*
//         * Workaround for JTextField consuming VK_ENTER, resulting in the Default Button
//         * not being called. 
//         */
//        
//        JRootPane rootPane = ((JTextField)e.getSource()).getRootPane();
//        JButton defaultButton = rootPane.getDefaultButton();
//        
//        if(defaultButton != null) {
//          defaultButton.doClick();
//        }
//      }
//    });
    
    getComponent().addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        text.set(textArea.getText());
      }
    });
    
    /*
     * Register listener for Model->Swing.
     */
    
    text.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        String value = text.get();
        if(!textArea.getText().equals(value)) {
          System.err.println("setting JTextArea to : " + value + " (old: " +  textArea.getText() + ")");
          textArea.setText(value);
        }
      }
    });
  }

  private final OwnedModel<O, String> text = new OwnedValueModel<O, String>(self());

  public OwnedModel<O, String> text() {
    return text;
  }
}
