package hs.ui.util;

import javax.swing.JComponent;

import hs.ui.AcceleratorScope;

public class SwingTranslator {
  
  public static int toSwing(AcceleratorScope scope) {
    switch(scope) {
    case CONTROL:
      return JComponent.WHEN_FOCUSED;
    case WINDOW:
      return JComponent.WHEN_IN_FOCUSED_WINDOW;
    default:
      return JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
    }
  }
}
