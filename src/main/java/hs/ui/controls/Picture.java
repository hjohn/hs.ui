package hs.ui.controls;

import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.Listener;
import hs.ui.image.ImageCache;
import hs.ui.image.ImageHandle;
import hs.ui.swing.JPaintablePanel;
import hs.ui.swing.Painter;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

public class Picture extends AbstractJComponent<Picture, JPaintablePanel> {
  public final Model<ImageHandle> imageHandle = new ValueModel<ImageHandle>();
  public final Model<Boolean> scale = new ValueModel<Boolean>(true);
  public final Model<Boolean> keepAspect = new ValueModel<Boolean>(true);
  public final Model<Double> alignmentX = new ValueModel<Double>(0.5);
  public final Model<Double> alignmentY = new ValueModel<Double>(0.5);
    
  public Picture() {
    super(new JPaintablePanel());

    getComponent().setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g, int width, int height) {
        if(imageHandle.get() != null) {
          Insets insets = getComponent().getInsets();
          
          int w = getComponent().getWidth() - insets.left - insets.right;
          int h = getComponent().getHeight() - insets.top - insets.bottom;
          
          BufferedImage imageToRender = scale.get() ? ImageCache.loadImage(imageHandle.get(), w, h, keepAspect.get()) : ImageCache.loadImage(imageHandle.get());

          int x = (int)((w - imageToRender.getWidth()) * alignmentX.get()) + insets.left;
          int y = (int)((h - imageToRender.getHeight()) * alignmentY.get()) + insets.top;
          
          g.drawImage(imageToRender, x, y, null);
        }
      }
    });
    
    imageHandle.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        getComponent().repaint();
      }
    });
  }

  @Override
  protected Picture self() {
    return this;
  }
}
