package hs.ui.image;

import java.awt.image.BufferedImage;

public class ImageHandle {
  private final byte[] imageData;
  private final String key;

  public ImageHandle(byte[] imageData, String key) {
    this.imageData = imageData;
    this.key = key;
  }

  public BufferedImage getImage() {
    return ImageCache.loadImage(key, imageData);
  }
  
  public BufferedImage getImage(int w, int h, boolean keepAspect) {
    return ImageCache.loadImage(key, imageData, w, h, keepAspect);
  }
}
