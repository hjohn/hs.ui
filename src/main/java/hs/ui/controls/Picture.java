package hs.ui.controls;

import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.Listener;
import hs.ui.swing.JPaintablePanel;
import hs.ui.swing.Painter;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Picture extends AbstractJComponent<Picture, JPaintablePanel> {
  public final Model<BufferedImage> image = new ValueModel<BufferedImage>();
  public final Model<Boolean> scale = new ValueModel<Boolean>(true);
  public final Model<Boolean> keepAspect = new ValueModel<Boolean>(true);
  public final Model<Double> alignmentX = new ValueModel<Double>(0.5);
  public final Model<Double> alignmentY = new ValueModel<Double>(0.5);
  
  private BufferedImage scaledImage;
  
  public Picture() {
    super(new JPaintablePanel());

    getComponent().setPainter(new Painter() {
      @Override
      public void paint(Graphics2D g, int width, int height) {
        if(image.get() != null) {
          BufferedImage imageToRender = image.get();
          int w = getComponent().getWidth();
          int h = getComponent().getHeight();
          
          if(scale.get() && scaledImage == null) {
            imageToRender = resize(image.get(), w, h, keepAspect.get());
          }
          
          int x = (int)((w - imageToRender.getWidth()) * alignmentX.get());
          int y = (int)((h - imageToRender.getHeight()) * alignmentY.get());
          
          g.drawImage(imageToRender, x, y, null);
        }
      }
    });
    
    image.onChange().call(new Listener() {
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
  
  private static BufferedImage resize(BufferedImage source, int newW, int newH, boolean keepAspect) {
    double scalex = (double)newW / source.getWidth();
    double scaley = (double)newH / source.getHeight();
    
    if(keepAspect) {
      if(scalex < scaley) {
        scaley = scalex;
        newH = (int)(source.getHeight() * scaley);
      }
      else if(scaley < scalex) {
        scalex = scaley;
        newW = (int)(source.getWidth() * scalex);
      }
    }
    
    AffineTransform at = AffineTransform.getScaleInstance(scalex, scaley);

    AffineTransformOp affineTransformOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
    BufferedImage target = new BufferedImage(newW, newH, source.getType());
    
    affineTransformOp.filter(source, target);
    
    return target;
  }
}
