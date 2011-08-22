package hs.styles;

import hs.ui.controls.GUIControl;

public interface Styler {

  public void visit(GUIControl[] controls);
  public void preVisitGroup(GUIControl group);
  public void postVisitGroup(GUIControl group);
}
