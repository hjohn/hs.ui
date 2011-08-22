package hs.styles;

import hs.models.Model;
import hs.ui.controls.GUIControl;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class SwingStyler implements Styler {
  private final StyleSheet styleSheet;
  private final LinkedList<GUIControl> stack = new LinkedList<GUIControl>();
  
  public SwingStyler(StyleSheet styleSheet) {
    this.styleSheet = styleSheet;
  }
  
  @Override
  public void visit(GUIControl[] controls) {
    int index = 0;
    
    for(GUIControl control : controls) {
      for(Rule rule : styleSheet) {
        if(rule.isApplicable(controls, index, stack)) { // TODO deal with 'more specific' rules etc..
          for(@SuppressWarnings("unused") Style style : rule) {
            Method method = getMethod(control.getClass(), "bgColor");
            
            if(method != null) {
              @SuppressWarnings("unchecked")
              Model<Object> model = (Model<Object>)callMethod(method, control);
              
              model.set(Color.RED);
            }
//            JComponent component = control.getComponent();
//            component.setBackground(Color.RED);
          }
        }
      }
      index++;
    }
  }

  @Override
  public void preVisitGroup(GUIControl group) {
    stack.push(group);
  }

  @Override
  public void postVisitGroup(GUIControl group) {
    stack.pop();
  }
  
  public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
    try {
      return cls.getMethod(methodName, parameterTypes);
    }
    catch(NoSuchMethodException e) {
      return null;
    }
  }
    
  public static Object callMethod(Method method, Object instance, Object... args) {
    try {
      return method.invoke(instance, args);
    }
    catch(IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    catch(InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
