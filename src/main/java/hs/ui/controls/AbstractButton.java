package hs.ui.controls;

import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.ui.events.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public abstract class AbstractButton<O> extends AbstractBaseButton<O, JButton> {
  private static final String CLICK_KEY = "click";
  
  public AbstractButton() {
    super(new JButton());
    
    System.err.println(getComponent().getActionMap());

    getComponent().setHideActionText(true);
    getComponent().setAction(new AbstractAction(CLICK_KEY) {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        actionNotifier.notifyListeners(new ActionEvent(AbstractButton.this));
      }
    });
  }

  private final Notifier<ActionEvent> actionNotifier = new Notifier<ActionEvent>();

  public ListenerList<ActionEvent> onClick() {
    return actionNotifier.getListenerList();
  }
  
  public O setAccelerator(KeyStroke keyStroke) {
    InputMap inputMap = getComponent().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(keyStroke, CLICK_KEY);
    
    ActionMap actionMap = getComponent().getActionMap();
    actionMap.put(CLICK_KEY, getComponent().getAction());
    
    return self();
  }
}
