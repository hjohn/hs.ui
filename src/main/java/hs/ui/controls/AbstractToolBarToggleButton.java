package hs.ui.controls;

import hs.models.events.Listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public abstract class AbstractToolBarToggleButton<O> extends AbstractToggleButton<O, JToggleButton> {
  private static final Border RAISED_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED, UIManager.getColor("controlLtHighlight"), UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager.getColor("control"));
  private static final Border LOWERED_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED, UIManager.getColor("controlLtHighlight"), UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager.getColor("control"));
  
  private boolean mouseEntered = false;
  
  public AbstractToolBarToggleButton() {
    super(new JToggleButton());
    
    border().set(RAISED_BORDER);
    getComponent().setBorderPainted(false);
    getComponent().setFocusPainted(false);
    
    getComponent().setRolloverEnabled(true);
    getComponent().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        mouseEntered = true;
        update();
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        mouseEntered = false;
        update();
      }
    });
    
    selected().onChange().call(new Listener() {
      @Override
      public void onEvent() {
        update();
      }
    });
  }

  private void update() {
    getComponent().setBorderPainted(mouseEntered || selected().get());
    border().set(selected().get() ? LOWERED_BORDER : RAISED_BORDER);
  }
}
