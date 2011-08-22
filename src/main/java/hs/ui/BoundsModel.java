package hs.ui;

import hs.models.Accessor;
import hs.models.PluggableOwnedModel;

import java.awt.Rectangle;

public class BoundsModel<O> extends PluggableOwnedModel<O, Rectangle> {
  
  public BoundsModel(O owner) {
    super(owner, new Accessor<Rectangle>() {
      private final Rectangle rectangle = new Rectangle();
      
      @Override
      public Rectangle read() {
        return new Rectangle(rectangle);
      }

      @Override
      public void write(Rectangle value) {
        rectangle.setBounds(value);
      }
    });
  }
 
  public O setWidth(int width) {
    Rectangle rectangle = get();
    rectangle.width = width;
    set(rectangle);
    return getOwner();
  }
  
  public O setHeight(int height) {
    Rectangle rectangle = get();
    rectangle.height = height;
    set(rectangle);
    return getOwner();
  }

  public O setSize(int width, int height) {
    Rectangle rectangle = get();
    rectangle.width = width;
    rectangle.height = height;
    System.err.println("Setting to " + rectangle);
    set(rectangle);
    return getOwner();
  }
  
  public O setLocation(int x, int y) {
    Rectangle rectangle = get();
    rectangle.x = x;
    rectangle.y = y;
    set(rectangle);
    return getOwner();
  }
}
