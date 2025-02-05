package hs.ui.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageCache {
  private static Map<String, BufferedImage> cache = new HashMap<String, BufferedImage>();
  
  public static BufferedImage loadImage(ImageHandle handle) {
    BufferedImage image = cache.get(handle.getKey());
    
    if(image == null && handle.getImageData() != null) {
      try {
        image = ImageIO.read(new ByteArrayInputStream(handle.getImageData()));
        cache.put(handle.getKey(), image);
        cache.put(createKey(handle.getKey(), image.getWidth(), image.getHeight(), true), image);
      }
      catch(IOException e) {
        // Ignore
      }
    }
    
    return image;
  }
  
  private static String createKey(String key, int w, int h, boolean keepAspect) {
    return key + "-" + w + "x" + h + "-" + (keepAspect ? "T" : "F");
  }

  public static BufferedImage loadImage(ImageHandle handle, int w, int h, boolean keepAspect) {
    String key = createKey(handle.getKey(), w, h, keepAspect);
    BufferedImage image = cache.get(key);

    if(image == null && handle.getImageData() != null) {
      try {
        image = ImageIO.read(new ByteArrayInputStream(handle.getImageData()));
        image = resize(image, w, h, keepAspect);
        
        cache.put(key, image);
        if(image.getWidth() == w && image.getHeight() == h) {
          cache.put(handle.getKey(), image);
        }
      }
      catch(IOException e) {
        // Ignore
      }
    }
    
    return image;
  }
  
  private static BufferedImage resize(BufferedImage source, int newW, int newH, boolean keepAspect) {
    if(newH == source.getHeight() && newW == source.getWidth()) {
      return source;
    }
    
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
