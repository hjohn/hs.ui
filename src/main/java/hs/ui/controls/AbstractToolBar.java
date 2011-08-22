package hs.ui.controls;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

public abstract class AbstractToolBar<O> extends AbstractJComponent<O, JToolBar> {

  public AbstractToolBar() {
    super(new JToolBar("Test"));
    
    getComponent().add(new DemoAction("1", new ImageIcon(), "Hi there", 'x'));
    getComponent().add(new JButton("HI"));
  }
  
  
  
  class DemoAction extends AbstractAction {

    public DemoAction(String text, Icon icon, String description,
        char accelerator) {
      super(text, icon);
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      putValue(SHORT_DESCRIPTION, description);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
  }

}
