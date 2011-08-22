package hs.ui.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

public abstract class AbstractToolBarButton<O> extends AbstractButton<O> {

  public AbstractToolBarButton() {
    border().set(BorderFactory.createBevelBorder(BevelBorder.RAISED, UIManager.getColor("controlLtHighlight"), UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager.getColor("control")));
    getComponent().setBorderPainted(false);
    getComponent().setFocusPainted(false);
    
    getComponent().setRolloverEnabled(true);
    getComponent().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        getComponent().setBorderPainted(true);
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        getComponent().setBorderPainted(false);
      }
    });
  }
  
}
