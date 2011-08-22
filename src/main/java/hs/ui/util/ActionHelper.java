package hs.ui.util;

import hs.models.events.EventListener;
import hs.ui.events.ActionEvent;

import java.awt.KeyboardFocusManager;

import javax.swing.Action;
import javax.swing.TransferHandler;
import javax.swing.text.DefaultEditorKit;

public class ActionHelper {
  private static final DefaultEditorKit.CutAction cutAction = new DefaultEditorKit.CutAction();
  private static final DefaultEditorKit.PasteAction pasteAction = new DefaultEditorKit.PasteAction();
  
  private static final EventListener<ActionEvent> CUT_LISTENER = new EventListener<ActionEvent>() {
    @Override
    public void onEvent(ActionEvent event) {
      System.err.println("Woohoo, Cut was called!");

      cutAction.actionPerformed(new java.awt.event.ActionEvent(event.getSource().getComponent(), java.awt.event.ActionEvent.ACTION_PERFORMED, null));
      
      KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      System.err.println("PFO = " + manager.getPermanentFocusOwner());
      
      java.awt.Component pfo = manager.getPermanentFocusOwner();

      Action transferHandlerAction = TransferHandler.getCutAction();
      
      transferHandlerAction.actionPerformed(new java.awt.event.ActionEvent(pfo, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
    }
  };

  private static final EventListener<ActionEvent> COPY_LISTENER = new EventListener<ActionEvent>() {
    @Override
    public void onEvent(ActionEvent event) {
      System.err.println("Woohoo, Copy was called!");

      cutAction.actionPerformed(new java.awt.event.ActionEvent(event.getSource().getComponent(), java.awt.event.ActionEvent.ACTION_PERFORMED, null));
      
      KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      System.err.println("PFO = " + manager.getPermanentFocusOwner());
      
      java.awt.Component pfo = manager.getPermanentFocusOwner();

      Action transferHandlerAction = TransferHandler.getCopyAction();
      
      transferHandlerAction.actionPerformed(new java.awt.event.ActionEvent(pfo, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
    }
  };

  private static final EventListener<ActionEvent> PASTE_LISTENER = new EventListener<ActionEvent>() {
    @Override
    public void onEvent(ActionEvent event) {
      System.err.println("Woohoo, Paste was called!");

      pasteAction.actionPerformed(new java.awt.event.ActionEvent(event.getSource().getComponent(), java.awt.event.ActionEvent.ACTION_PERFORMED, null));
      
      KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      System.err.println("PFO = " + manager.getPermanentFocusOwner());
      
      java.awt.Component pfo = manager.getPermanentFocusOwner();

      Action transferHandlerAction = TransferHandler.getPasteAction();
      
      transferHandlerAction.actionPerformed(new java.awt.event.ActionEvent(pfo, java.awt.event.ActionEvent.ACTION_PERFORMED, null));
      
//      String action = e.getActionCommand();
//      Action a = focusOwner.getActionMap().get(action);
//      
//      System.err.println("TAL: " + focusOwner + " -> " + action);
//      if(a != null) {
//        a.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
//      }

    }
  };

  public static EventListener<ActionEvent> cutListener() {
    return CUT_LISTENER; 
  }
  
  public static EventListener<ActionEvent> copyListener() {
    return COPY_LISTENER; 
  }
  
  public static EventListener<ActionEvent> pasteListener() {
    return PASTE_LISTENER; 
  }
}
