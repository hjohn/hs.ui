package hs.ui.menu;

import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.ui.events.ActionEvent;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuItem extends AbstractMenuControl {
  private final JMenuItem menuItem = new JMenuItem();
  
  public MenuItem(String title) {
    menuItem.setText(title);
    
    /*
     * Swing -> clickNotifier
     */
    
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        actionNotifier.notifyListeners(new ActionEvent(MenuItem.this));
      }
    });
  }

  @Override
  public JMenuItem getComponent() {
    return menuItem;
  }
 
  private final Notifier<ActionEvent> actionNotifier = new Notifier<ActionEvent>();
  
  public ListenerList<ActionEvent> onClick() {
    return actionNotifier.getListenerList();
  }

  public MenuItem setAccelerator(KeyStroke keyStroke) {
    menuItem.setAccelerator(keyStroke);
    return this;
  }
}
