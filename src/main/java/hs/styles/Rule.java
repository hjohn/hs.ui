package hs.styles;

import hs.ui.controls.GUIControl;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Rule implements Iterable<Style> {
  private final Selector selector;
  private final List<Style> styles;

  @SuppressWarnings("unused")
  public static void main(String[] args) {
    
    
    
    
    new Rule(
      new Selector("", "Button", Relationship.ANCESTOR, new Selector("", "HorizontalGroup")),
      new Style("color", Color.RED)
    );
  }
  
  public Rule(Selector selector, Style... styles) {
    this.selector = selector;
    this.styles = Arrays.asList(styles);
  }

  public boolean isApplicable(GUIControl[] controls, int index, LinkedList<GUIControl> stack) {
    return selector.isApplicable(controls, index, stack);
  }

  @Override
  public Iterator<Style> iterator() {
    return styles.iterator();
  }
}
