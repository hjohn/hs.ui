package hs.ui.frames;

import hs.models.Accessor;
import hs.models.Model;
import hs.models.OwnedBeanModel;
import hs.models.OwnedModel;
import hs.models.PluggableOwnedModel;
import hs.models.events.Listener;
import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.styles.StyleSheet;
import hs.styles.SwingStyler;
import hs.ui.BoundsModel;
import hs.ui.ComponentRoot;
import hs.ui.Control;
import hs.ui.Window;
import hs.ui.controls.AbstractButton;
import hs.ui.controls.CoolBar;
import hs.ui.controls.GUIControl;
import hs.ui.controls.ToolBar;
import hs.ui.controls.VerticalGroup;
import hs.ui.events.ClosedEvent;
import hs.ui.events.ClosingEvent;
import hs.ui.menu.MenuBar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXStatusBar;

public class WindowFrame extends AbstractFrame<WindowFrame> {
  private final FrameAdapter frameAdapter;
  private final JPanel rootPanel = new JPanel(new BorderLayout());
  private final RootGroup group = new RootGroup(this);
  
  private final MyWindowAdapter windowAdapter = new MyWindowAdapter(this);
  
  public WindowFrame(Window owner, GraphicsConfiguration gc, boolean isDialog) {
    if(isDialog) {
      this.frameAdapter = new Dialog(owner);
    }
    else {
      this.frameAdapter = new TopFrame((java.awt.Window)owner.getContainer(), gc);
    }
    
    title = new OwnedBeanModel<WindowFrame, String>(this, frameAdapter, "title");

    rootPanel.setOpaque(true);
    rootPanel.setBackground(new Color(0, 0, 0 ,0));
    rootPanel.add(group.getComponent(), BorderLayout.CENTER);

    frameAdapter.setContentPane(rootPanel);
    
    minWidth().set(1);
    minHeight().set(1);
    maxWidth().set(Integer.MAX_VALUE);
    maxHeight().set(Integer.MAX_VALUE);
    
    bounds().set(frameAdapter.getComponent().getBounds());
    
    frameAdapter.getComponent().addComponentListener(new ComponentAdapter() {
      @Override
      public void componentMoved(ComponentEvent e) {
        boundsModel.set(frameAdapter.getComponent().getBounds());
      }
      
      @Override
      public void componentResized(ComponentEvent e) {
        boundsModel.set(frameAdapter.getComponent().getBounds());
      }
    });
    
    boundsModel.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        if(!frameAdapter.getComponent().getBounds().equals(boundsModel.get())) {
          System.err.println("Setting frame size to : " + boundsModel.get());
          frameAdapter.getComponent().setBounds(boundsModel.get());
        }
      }
    });
    
    Listener sizeListener = new Listener() {
      @Override
      public void onEvent() {
        int minW = minWidth().get();
        int minH = minHeight().get();
        int maxW = maxWidth().get();
        int maxH = maxHeight().get();

        frameAdapter.setMinimumSize(new Dimension(minW, minH));
        frameAdapter.setMaximumSize(new Dimension(maxW, maxH));
      }
    };
    
    minWidth().onChange().call(sizeListener);
    minHeight().onChange().call(sizeListener);
    maxWidth().onChange().call(sizeListener);
    maxHeight().onChange().call(sizeListener);
  }
  
  public WindowFrame() {
    this(null, null, false);
  }
  
  @Override
  public Container getContainer() {
    return frameAdapter.getComponent();
  }
  
  @Override
  protected WindowFrame self() {
    return this;
  }
  
  @Override
  protected void finalize() throws Throwable {
    if(frameAdapter != null && frameAdapter.isDisplayable()) {
      
      /*
       * Checks whether proper clean up was performed.  Even though this class is written in such a way that
       * it will be eligable for Garbage Collection when there are no references to this instance, the JFrame
       * will not as native resources prevent it from being collected (as long as it is displayable).
       * 
       * If proper cleanup was not performed, the finalizer will do it for you to prevent resource leaks.
       * The onClose and onClosing listeners however will not be called in these situations, so it is best
       * to do proper cleanup.
       */
      
      System.err.println("LEAK: Window was not properly disposed (which prevents proper calling of close events).  Disposing in finalizer to prevent resource leaks.");
      frameAdapter.dispose();
    }
    super.finalize();
  }
  
  @Override
  public WindowFrame add(GUIControl... controls) {
    group.add(controls);
    return this;
  }
  
  public WindowFrame remove(GUIControl... controls) {
    group.remove(controls);
    return this;
  }
  
  // TODO generalize these "bar" placement methods so they can be used for anything
  
  public WindowFrame setMenuBar(MenuBar menuBar) {
    frameAdapter.setMenuBar(menuBar.getComponent());
    menuBar.setWindow(this);
    return this;
  }
  
  public WindowFrame setStatusBar(Control<?> statusBar) {
    for(java.awt.Component component : rootPanel.getComponents()) {
      if(component instanceof JXStatusBar) {
        rootPanel.remove(component);
        break;
      }
    }
    rootPanel.add(statusBar.getComponent(), BorderLayout.SOUTH);
    return this;
  }
  
  public WindowFrame addToolBar(ToolBar toolBar) {
    rootPanel.add(toolBar.getComponent(), BorderLayout.NORTH);
    return this;
  }
  
  private CoolBar coolBar;
  
  public WindowFrame setCoolBar(CoolBar coolBar) {
    this.coolBar = coolBar;
    rootPanel.add(coolBar.getComponent(), BorderLayout.NORTH);
    return this;
  }
  
  public WindowFrame setDefaultButton(AbstractButton<?> button) {
    frameAdapter.setDefaultButton(button);
    return this;
  }

  /**
   * Blocks until window is closed 
   */
  public void open(java.awt.Dialog.ModalityType modalityType) {
    frameAdapter.open(modalityType);
  }
  
  @Override
  public void dispose() {
    frameAdapter.dispose();
  }
  
  @Override
  public void toFront() {
    frameAdapter.toFront();
  }
  
  private final BoundsModel<WindowFrame> boundsModel = new BoundsModel<WindowFrame>(this);
  
  @Override
  public BoundsModel<WindowFrame> bounds() {
    return boundsModel;
  }
  
  public WindowFrame centerOn(Window associatedWindow) {
    Rectangle aw = associatedWindow.bounds().get();
    Rectangle r = bounds().get();
    
    int x = (aw.x + aw.width / 2) - r.width / 2;
    int y = (aw.y + aw.height / 2) - r.height / 2;
    
    frameAdapter.setLocation(x, y);
    
    return this;
  }

  private StyleSheet styleSheet;
  
  public WindowFrame setStyleSheet(StyleSheet styleSheet) {
    this.styleSheet = styleSheet;
    return this;
  }
  
  @Override
  public WindowFrame pack() {
    if(styleSheet != null) {
      SwingStyler styler = new SwingStyler(styleSheet);
      
      group.visitStyler(styler);
      if(coolBar != null) {
        coolBar.visitStyler(styler);
      }
    }
    
    frameAdapter.pack();
    bounds().set(frameAdapter.getComponent().getBounds());

    return this;
  }
  
  @Override
  public WindowFrame validate() {
    frameAdapter.validate();
    return this;
  }
  
  private final OwnedModel<WindowFrame, String> title;
  
//  private final AbstractOwnedModel<Window, String> title = new AbstractOwnedModel<Window, String>(this) {
//    @Override
//    public String read() {
//      return frame.getTitle();
//    }
//    
//    @Override
//    public void write(String value) {
//      frame.setTitle(value);
//    }
//  };

  public OwnedModel<WindowFrame, String> title() {
    return title;
  }
  
  private final OwnedModel<WindowFrame, Boolean> visible = new PluggableOwnedModel<WindowFrame, Boolean>(this, new Accessor<Boolean>() {
    @Override
    public Boolean read() {
      return frameAdapter.isVisible();
    }

    @Override
    public void write(Boolean value) {
      frameAdapter.setVisible(value);
      windowAdapter.setHard(value);
    }
  });

  @Override
  public OwnedModel<WindowFrame, Boolean> visible() {
    return visible;
  }

  private final OwnedModel<WindowFrame, Boolean> maximized = new PluggableOwnedModel<WindowFrame, Boolean>(this, new Accessor<Boolean>() {
    @Override
    public Boolean read() {
      return frameAdapter.isMaximized();
    }

    @Override
    public void write(Boolean value) {
      frameAdapter.setMaximized(value);
    }
  });

  public Model<Boolean> maximized() {
    return maximized;
  }
  
  private final Notifier<ClosedEvent> closedNotifier = new Notifier<ClosedEvent>();

  @Override
  public ListenerList<ClosedEvent> onClosed() {
    return closedNotifier.getListenerList();
  }
  
  private final Notifier<ClosingEvent> closingNotifier = new Notifier<ClosingEvent>();

  public ListenerList<ClosingEvent> onClosing() {
    return closingNotifier.getListenerList();
  }
      
  private static final class MyWindowAdapter extends WindowAdapter {
    private final WeakReference<WindowFrame> windowReference;
    @SuppressWarnings("unused")
    private WindowFrame windowHardReference;

    public MyWindowAdapter(WindowFrame frame) {
      windowReference = new WeakReference<WindowFrame>(frame);
    }
     
    public void setHard(boolean hard) {
      windowHardReference = hard ? windowReference.get() : null;
    }
    
    @Override
    public void windowClosed(WindowEvent e) {
      WindowFrame frame = windowReference.get();
      
      if(frame != null) {
        frame.closedNotifier.notifyListeners(new ClosedEvent());
      }
    }

    @Override
    public void windowClosing(WindowEvent e) {
      WindowFrame frame = windowReference.get();
      
      if(frame != null) {
        ClosingEvent closingEvent = new ClosingEvent();
      
        frame.closingNotifier.notifyListeners(closingEvent);
        System.err.println("Window onClosing");
        if(!closingEvent.isVetoed()) {
          System.err.println("Window visible to false");
          frame.dispose();
        }
      }
    }
  }

  /*
   * Internal implementation classes
   */
  
  public abstract class FrameAdapter {
    
    protected abstract Container getComponent();

    public boolean isDisplayable() {
      return getComponent().isDisplayable();
    }
    
    public boolean isVisible() {
      return getComponent().isVisible();
    }

    public void setVisible(boolean visible) {
      getComponent().setVisible(visible);
    }
    
    public void setMinimumSize(Dimension minimum) {
      getComponent().setMinimumSize(minimum);
    }

    public void setMaximumSize(Dimension minimum) {
      getComponent().setMaximumSize(minimum);
    }
    
    public void setLocation(int x, int y) {
      getComponent().setLocation(x, y);
    }
    
    public void validate() {
      getComponent().validate();
    }

    public abstract String getTitle();
    public abstract void setTitle(String title);
    public abstract void setMenuBar(JMenuBar menuBar);
    public abstract void setContentPane(JComponent content);
    public abstract void pack();
    public abstract void dispose();
    public abstract void open(java.awt.Dialog.ModalityType modalityType);
    public abstract void setDefaultButton(AbstractButton<?> button);
    public abstract void toFront();
    
    public abstract boolean isMaximized();
    public abstract void setMaximized(boolean maximized);
  }
  
  public class TopFrame extends FrameAdapter {
    private final java.awt.Window frame;

    public TopFrame(java.awt.Window owner, GraphicsConfiguration gc) {
      this.frame = new java.awt.Window(owner, gc) {
//        {
//          setBackground(new Color(0,0,0,0));
//        }
      };

      //frame.setBackground(new Color(0,0,0,0));
      frame.addWindowListener(windowAdapter);
      //frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    @Override
    protected Container getComponent() {
      return frame;
    }
    
    @Override
    public String getTitle() {
      return "";
    }
    
    @Override
    public void setTitle(String title) {
      // has no title
    }
    
    @Override
    public void setMenuBar(JMenuBar menuBar) {
      // has no menubar
    }

    @Override
    public void setContentPane(JComponent content) {
      frame.removeAll();
      frame.add(content);
    }
    
    @Override
    public void validate() {
      frame.invalidate();
      frame.validate();
    }
    
    @Override
    public void pack() {
      frame.pack();
    }
        
    @Override
    public void dispose() {
//      System.err.println("Removing window Listener");
//      frame.removeWindowListener(windowAdapter);
      frame.dispose();
    }
    
    @Override
    public void toFront() {
      frame.toFront();
    }

    @Override
    public void open(ModalityType modalityType) {
      throw new UnsupportedOperationException("Modal frames are not supported");
    }

    @Override
    public void setDefaultButton(AbstractButton<?> button) {
//      frame.getRootPane().setDefaultButton(button.getComponent());
    }

    @Override
    public boolean isMaximized() {
      return false;
    }

    @Override
    public void setMaximized(boolean maximized) {
    
    }
  }
  
  public class Dialog extends FrameAdapter {
    private final JDialog dialog;
    
    public Dialog(Window owner) {
      Container awtOwner = owner != null ? owner.getContainer() : null;
            
      if(awtOwner instanceof JFrame) {
        dialog = new JDialog((JFrame)awtOwner);
      }
      else {
        dialog = new JDialog((JDialog)awtOwner);
      }
      
      dialog.addWindowListener(windowAdapter);
      dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    @Override
    public void dispose() {
//      dialog.removeWindowListener(windowAdapter);
      dialog.dispose();
    }
    
    @Override
    public void toFront() {
      dialog.toFront();
    }

    @Override
    protected Container getComponent() {
      return dialog;
    }

    @Override
    public String getTitle() {
      return dialog.getTitle();
    }

    @Override
    public void pack() {
      dialog.pack();
    }

    @Override
    public void setContentPane(JComponent content) {
      dialog.setContentPane(content);
    }

    @Override
    public void setMenuBar(JMenuBar menuBar) {
      dialog.setJMenuBar(menuBar);
    }

    @Override
    public void setTitle(String title) {
      dialog.setTitle(title);
    }

    @Override
    public void open(ModalityType modalityType) {
      dialog.setModalityType(modalityType);
      if(dialog.getModalityType() == ModalityType.MODELESS) {
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
      }
      
      visible().set(true);
//      dialog.setVisible(true);
      dialog.setModalityType(ModalityType.MODELESS);
      dialog.dispose();
    }
    
    @Override
    public void setDefaultButton(AbstractButton<?> button) {
      dialog.getRootPane().setDefaultButton(button.getComponent());
    }

    @Override
    public boolean isMaximized() {
      throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void setMaximized(boolean maximized) {
      throw new UnsupportedOperationException("Method not implemented");
    }
  }
  
  public static class RootGroup extends VerticalGroup implements ComponentRoot {
    private final WindowFrame frame;

    public RootGroup(WindowFrame frame) {
      this.frame = frame;
    }
    
    @Override
    public Window getWindow() {
      return frame;
    }
  }
}

