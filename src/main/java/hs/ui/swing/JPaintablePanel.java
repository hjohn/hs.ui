package hs.ui.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class JPaintablePanel extends JPanel {
  private Painter painter;

  public JPaintablePanel() {
    super();
  }

  public JPaintablePanel(boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  public JPaintablePanel(LayoutManager layout, boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  public JPaintablePanel(LayoutManager layout) {
    super(layout);
  }
  
  public Painter getPainter() {
    return painter;
  }

  public void setPainter(Painter painter) {
    this.painter = painter;
  }
  
  @Override
  public void paint(Graphics g) {
    super.paint(g);
  }
  
  
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    if(painter != null) {
//      Insets insets = getInsets();
//      Rectangle clipBounds = g.getClipBounds();
      
      Graphics2D g2d = (Graphics2D)g.create();
//      System.err.println(">> " + clipBounds + " > " + width + " " + height + " >> " + g2d.getClipBounds() + " " + getWidth() + " " + getHeight());
      painter.paint(g2d, getWidth(), getHeight());
      g2d.dispose();
    }
  }
}
