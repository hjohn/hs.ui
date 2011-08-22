package hs.ui.controls;

import hs.models.Accessor;
import hs.models.BasicFontModel;
import hs.models.BeanAccessor;
import hs.models.BeanModel;
import hs.models.FontModel;
import hs.models.Model;
import hs.models.PluggableModel;
import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.ui.AcceleratorScope;
import hs.ui.ControlListener;
import hs.ui.events.KeyPressedEvent;
import hs.ui.events.KeyTypedEvent;
import hs.ui.util.SwingTranslator;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

public abstract class AbstractJComponent<O, T extends JComponent> extends AbstractControl<O> {
  private final T component;
  private final JComponent secondaryComponent;
  
  private final BeanModel<Border> border;
  private final PluggableModel<Boolean> opaque;
  private final BeanModel<Boolean> visible;
  private final BeanModel<Color> bgColor;
  private final BeanModel<Color> fgColor;
  private final FontModel font; 

  public AbstractJComponent(final T component, final JComponent secondaryComponent) {
    this.component = component;
    this.secondaryComponent = secondaryComponent;
   
    border = new BeanModel<Border>(component, "border");
//    opaque = new OwnedBeanModel<O, Boolean>(self(), secondaryComponent, "opaque");  // TODO should affect all relevant components... like JScrollPane, JViewPort, JTable...
    visible = new BeanModel<Boolean>(secondaryComponent, "visible");
    bgColor = new BeanModel<Color>(secondaryComponent, "background");
    fgColor = new BeanModel<Color>(secondaryComponent, "foreground");
    opaque = new PluggableModel<Boolean>(new Accessor<Boolean>() {
      @Override
      public Boolean read() {
        return component.isOpaque();
      }

      @Override
      public void write(Boolean value) {
        component.setOpaque(value);
        secondaryComponent.setOpaque(value);
        
        if(component instanceof JScrollPane) {
          ((JScrollPane)component).getViewport().setOpaque(value);
        }
      }
    });
      
    font = new BasicFontModel(new BeanAccessor<Font>(secondaryComponent, "font"));
    
    /*
     * Register key listener
     */
    
    secondaryComponent.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        keyTypedNotifier.notifyListeners(new KeyTypedEvent(AbstractJComponent.this, e));
      }

      @Override
      public void keyPressed(KeyEvent e) {
        keyPressedNotifier.notifyListeners(new KeyPressedEvent(AbstractJComponent.this, e, true));
      }

      @Override
      public void keyReleased(KeyEvent e) {
        keyPressedNotifier.notifyListeners(new KeyPressedEvent(AbstractJComponent.this, e, false));
      }
    });
  }
  
  public AbstractJComponent(T component) {
    this(component, component);
  }
  
  @Override
  public final T getComponent() {
    return component;
  }
  
  protected final JComponent getSecondaryComponent() {
    return secondaryComponent;
  }

  public Model<Border> border() {
    return border;
  }

  public Model<Boolean> opaque() {
    return opaque;
  }
  
  public Model<Boolean> visible() {
    return visible;
  }
  
  public Model<Color> bgColor() {
    return bgColor;
  }

  public Model<Color> fgColor() {
    return fgColor;
  }
  
  public FontModel font() {
    return font;
  }
  
  public void requestFocus() {
    if(secondaryComponent != null) {
      secondaryComponent.requestFocusInWindow();
    }
    else {
      getComponent().requestFocusInWindow();
    }
  }
  
  public void repaint() {
    getComponent().repaint();
  }
  
  public void setAccelerator(KeyStroke keyStroke, AcceleratorScope scope, final ControlListener<O> listener) {
    System.out.println(getComponent().getInputMap(SwingTranslator.toSwing(scope)).allKeys());
    InputMap inputMap = getComponent().getInputMap(SwingTranslator.toSwing(scope));
    inputMap.put(keyStroke, listener == null ? null : keyStroke.toString());

    System.out.println(getComponent().getActionMap().allKeys());

    ActionMap actionMap = getComponent().getActionMap();
    actionMap.put(keyStroke.toString(), listener == null ? null : new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        listener.onEvent(self());
      }
    });
  }
  
  private final Notifier<KeyTypedEvent> keyTypedNotifier = new Notifier<KeyTypedEvent>();
  
  public ListenerList<KeyTypedEvent> onKeyTyped() {
    return keyTypedNotifier.getListenerList();
  }
  
  private final Notifier<KeyPressedEvent> keyPressedNotifier = new Notifier<KeyPressedEvent>();
  
  public ListenerList<KeyPressedEvent> onKeyPressed() {
    return keyPressedNotifier.getListenerList();
  } 
}
