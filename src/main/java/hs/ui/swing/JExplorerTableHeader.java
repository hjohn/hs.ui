package hs.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public final class JExplorerTableHeader extends JTableHeader {
  private static final JLabel stamp = new JLabel();
  private static final Border border = UIManager.getBorder("TableHeader.cellBorder");  // TODO what about L&F changes?
  
  public JExplorerTableHeader(TableColumnModel cm) {
    super(cm);
    stamp.setBorder(border);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    /*
     * Paints a fake extra column cell header 
     */
    
    Rectangle r = getHeaderRect(getColumnModel().getColumnCount() - 1);
    Dimension d = getSize();
    int emptySpaceX = r.x + r.width;
    stamp.setBounds(new Rectangle(emptySpaceX, r.y, d.width - emptySpaceX + 10, d.height));
    
    Graphics gCopy = g.create();
    gCopy.translate(emptySpaceX, r.y);
    stamp.setOpaque(true);
    stamp.paint(gCopy);
  }
}