package hs.ui.controls;

import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.Listener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class MultiLineDynamicLabel extends AbstractJComponent<MultiLineDynamicLabel, JScrollPane> {
  public final Model<String> text = new ValueModel<String>();
  
  public MultiLineDynamicLabel() {
    super(new JScrollPane(), new JTextArea());
    
    final JTextArea textArea = (JTextArea)getSecondaryComponent();
    getComponent().setViewportView(textArea);
    getComponent().setBorder(new EmptyBorder(0, 0, 0, 0));
    
    opaque().set(false);
    
    textArea.setEditable(false);
    textArea.setFocusable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    
    weightX().set(1.0);
    weightY().set(1.0);
    
    minWidth().set(1);
    maxWidth().set(Integer.MAX_VALUE);
    minHeight().set(1);
    maxHeight().set(Integer.MAX_VALUE);
    
    getComponent().setEnabled(false);
    getComponent().setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    getComponent().setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    
    text.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        textArea.setText(text.get());
      }
    });
  }
  
  @Override
  protected MultiLineDynamicLabel self() {
    return this;
  }
}
