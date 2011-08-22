package hs.styles;

import hs.ui.controls.GUIControl;

import java.util.LinkedList;

public class Selector {
  @SuppressWarnings("unused")
  private final String id;
  private final String className;
  @SuppressWarnings("unused")
  private final Relationship relationShip;
  @SuppressWarnings("unused")
  private final Selector relation;
  
  public Selector(String id, String className, Relationship relationShip, Selector relation) {
    this.id = id;
    this.className = className;
    this.relationShip = relationShip;
    this.relation = relation;
  }
  
  public Selector(String id, String className) {
    this(id, className, null, null);
  }
  
  public boolean isApplicable(GUIControl[] controls, int index, LinkedList<GUIControl> stack) {
    GUIControl control = controls[index];
    
    if(className != null) {
      System.err.println("Testing if " + control.getClass().getSimpleName() + " matches " + className);
      if(control.getClass().getSimpleName().equals(className)) {
        System.err.println("TRUE");
        return true;
      }
    }
    
    return false;
  }
}
