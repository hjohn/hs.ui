package hs.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Provides a list with multiple columns, also know as a ListView.  It is distinguished from a table by:
 * 
 * - Using a list as the underlying model
 * - Only allowing selections on the first column
 * 
 * It also features:
 * 
 * - Drag selection (draw a box to select items)
 * - Item drop target highlighting
 * 
 * JTable is used as a basis, but modified in the following way:
 * 
 * - An extra hidden column ensures the table (and header) is always "full width"
 */
public class JExplorerTable extends JTable {
  private static final AlphaComposite TRANSPARENT = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);

  private Rectangle selectionRect;
  private Point start;
  private int highlightedRow = -1;
  private final Color selectionForeground;
  private final Color selectionBackground;
  
  public JExplorerTable() {
    selectionForeground = getSelectionForeground();
    selectionBackground = getSelectionBackground();
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    setShowGrid(false);
    setRowMargin(0);   // Done to prevent headaches... row margin gets substracted from row height (so a 16 point label is renderer at 15 points if margin is 1 -- WTF!)
       
//    setTableHeader(new JExplorerTableHeader(new MyTableColumnModel(getColumnModel())));
//    setTableHeader(new JExplorerTableHeader(getColumnModel()));
//    setColumnModel(getColumnModel());
    setFillsViewportHeight(true);
  }
  
  @Override
  public void setSelectionModel(ListSelectionModel newModel) {
    super.setSelectionModel(newModel);
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    if(selectionRect != null) {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setComposite(TRANSPARENT);
      g2d.setColor(UIManager.getColor("Table.selectionBackground"));
      g2d.fill(selectionRect);
      g2d.setComposite(AlphaComposite.SrcOver);
      g2d.setColor(UIManager.getColor("Table.selectionBackground"));
      g2d.draw(selectionRect);
    }
  }

  @Override
  public void validate() {
    setWidthsFromPreferredWidths();
    super.validate();
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void doLayout() {


    if(getResizingColumn() == null) {
      setWidthsFromPreferredWidths();
    }
    
    super.layout();
  }
  
  private TableColumn getResizingColumn() {
    return (tableHeader == null) ? null : tableHeader.getResizingColumn();
  }
  
  private void setWidthsFromPreferredWidths() {
    TableColumnModel cm = getColumnModel();
    int totalColumns = cm.getColumnCount();
    int totalColumnWidth = 0;
    
    for(int columnIndex = 0; columnIndex < totalColumns - 1; columnIndex++) {
      TableColumn column = cm.getColumn(columnIndex);
      column.setWidth(column.getPreferredWidth());
      totalColumnWidth += column.getPreferredWidth();
    }  

    Component parent = getParent();
    if(parent != null) {
      int w = parent.getWidth() - totalColumnWidth;
      if(w < 1) {
        w = 1;
      }
      
      cm.getColumn(totalColumns - 1).setMinWidth(1);
      cm.getColumn(totalColumns - 1).setWidth(w);
      cm.getColumn(totalColumns - 1).setPreferredWidth(w);
      
      int lastColumnWidth = getColumnModel().getColumn(getColumnModel().getColumnCount() - 1).getPreferredWidth();

      Container grandParent = parent.getParent();
      if(grandParent != null && grandParent instanceof JScrollPane) {
        ((JScrollPane)parent.getParent()).setHorizontalScrollBarPolicy(parent.getWidth() > totalColumnWidth - lastColumnWidth ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      }
    }
  }
  
  @Override
  public void setColumnModel(TableColumnModel columnModel) {
    super.setColumnModel(new FakeTableColumnModel(columnModel));

    setTableHeader(new JExplorerTableHeader(columnModel));
    
    columnModel.addColumnModelListener(new TableColumnModelListener() {
      @Override
      public void columnAdded(TableColumnModelEvent e) {
      }

      @Override
      public void columnMarginChanged(ChangeEvent e) {
        Component parent = getParent();

        if(parent != null) {
          TableColumnModel cm = getColumnModel();
          int totalColumns = cm.getColumnCount();
          int totalColumnWidth = 0;
  
          for(int columnIndex = 0; columnIndex < totalColumns - 1; columnIndex++) {
            TableColumn column = cm.getColumn(columnIndex);
            totalColumnWidth += column.getPreferredWidth();
          }  
  
          int w = parent.getWidth() - totalColumnWidth;
          if(w < 1) {
            w = 1;
          }
          cm.getColumn(totalColumns - 1).setWidth(w);
          cm.getColumn(totalColumns - 1).setPreferredWidth(w);
        }
      }

      @Override
      public void columnMoved(TableColumnModelEvent e) {
      }

      @Override
      public void columnRemoved(TableColumnModelEvent e) {
      }

      @Override
      public void columnSelectionChanged(ListSelectionEvent e) {
      } 
    });
  }

  private int scrollDistance;
  
  private final Timer scrollTimer = new Timer(5, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if(scrollDistance != 0) {
        Rectangle visibleRect = getVisibleRect();
        visibleRect.y += scrollDistance;
        scrollRectToVisible(visibleRect);
        
        /*
         * Recalculate the selection rectangle because the scrolling may have
         * affected its size.  
         */
        
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, JExplorerTable.this);
        updateSelectionRect(p.x, p.y);
        repaint();
      }
    }
  });
  
  @Override
  protected void processMouseEvent(MouseEvent e) {
    if(e.getID() == MouseEvent.MOUSE_RELEASED) {
      System.err.println("RELEASED");
      if(selectionRect != null) {
        selectItemsInSelectionRect();
        selectionRect = null;
        scrollTimer.stop();
        repaint();
      }
    }
    else if(e.getID() == MouseEvent.MOUSE_PRESSED) {
      System.err.println("PRESSED");
      requestFocusInWindow();
      int row = rowAtPoint(e.getPoint());
      
      if(isOverItem(e.getPoint())) {
        start = null;
        if(!e.isControlDown() && !e.isShiftDown()) {
          if(!getSelectionModel().isSelectedIndex(row)) {
            getSelectionModel().setSelectionInterval(row, row);
            getSelectionModel().setLeadSelectionIndex(row);
            getColumnModel().getSelectionModel().setSelectionInterval(0, 0);
            getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
          }
        }
        else {
          return;  // Consume
        }
      }
      else {
        start = e.getPoint();
        return;  // Consume
      }
    }
    else if(e.getID() == MouseEvent.MOUSE_CLICKED && e.getClickCount() == 1) {
      System.err.println("CLICKED");

      if(!isOverItem(e.getPoint())) {
        if(!e.isControlDown() && !e.isShiftDown()) {

          /*
           * Clicked on an empty area 
           */
          
          clearSelection();
          return;  // Consume
        }
      }
      else if(e.isControlDown() || e.isShiftDown()) {
        int row = rowAtPoint(e.getPoint());
        int leadRow = e.isShiftDown() ? getSelectionModel().getLeadSelectionIndex() : row;

        if(!e.isControlDown()) {
          clearSelection();
        }
        
        if(!getSelectionModel().isSelectedIndex(row)) {
          getSelectionModel().addSelectionInterval(leadRow, row);
        }
        else {
          getSelectionModel().removeSelectionInterval(leadRow, row);
        }
        getSelectionModel().setLeadSelectionIndex(row);
        getColumnModel().getSelectionModel().setSelectionInterval(0, 0);
        getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
        return;  // Consume
      }
    }
    
    super.processMouseEvent(e);
  }

  private void updateSelectionRect(int mx, int my) {
    int x = start.x < mx ? start.x : mx;
    int y = start.y < my ? start.y : my;
    int w = Math.abs(start.x - mx);
    int h = Math.abs(start.y - my);
   
    selectionRect = new Rectangle(x, y, w, h);
  }
  
  @Override
  protected void processMouseMotionEvent(MouseEvent e) {
    if(e.getID() == MouseEvent.MOUSE_DRAGGED) {
      if(start != null) {
        if(!e.isControlDown() && selectionRect == null) {
          clearSelection();
        }
        
        updateSelectionRect(e.getX(), e.getY());

        Rectangle visibleRect = getVisibleRect();
        scrollDistance = e.getY() < visibleRect.y ? e.getY() - visibleRect.y : 
                         e.getY() > visibleRect.y + visibleRect.height ? e.getY() - visibleRect.y - visibleRect.height : 0;
        scrollTimer.start();
        repaint();
        return;  // Consume
      }
      
      if(e.isControlDown() || e.isShiftDown()) {
        return;  // Consume
      }
    }
    
    super.processMouseMotionEvent(e);
  }

  private void selectItemsInSelectionRect() {
    Point endPoint = new Point(selectionRect.x + selectionRect.width - 1, selectionRect.y + selectionRect.height - 1);
    int startRow = rowAtPoint(selectionRect.getLocation());
    int endRow = rowAtPoint(endPoint);
    
    if(startRow == -1) {
      if(endRow == -1) {
        // Selection was completely out of bounds
        return;
      }
      startRow = selectionRect.getLocation().y < 0 ? 0 : getRowSorter().getViewRowCount() - 1;
    }
    if(endRow == -1) {
      endRow = getRowSorter().getViewRowCount() - 1;
    }
    
    if(startRow > endRow) {
      int tempRow = startRow;
      startRow = endRow;
      endRow = tempRow;
    }

    getColumnModel().getSelectionModel().setSelectionInterval(0, 0);

    ListSelectionModel sm = getSelectionModel();
    
    sm.setValueIsAdjusting(true);
    for(int row = startRow; row <= endRow; row++) {
      if(isRowInSelectionRect(getCellWidth(row, convertColumnIndexToView(0)), row)) {
        if(sm.isSelectedIndex(row)) {
          sm.removeSelectionInterval(row, row);
        }
        else {
          sm.addSelectionInterval(row, row);
        }
      }
    }
    sm.setValueIsAdjusting(false);
  }
  
  @Override
  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    if(e.getID() == FocusEvent.FOCUS_GAINED) {
      setSelectionForeground(selectionForeground);
      setSelectionBackground(selectionBackground);
    }
    else {
      setSelectionForeground(UIManager.getColor("Table.foreground"));
      setSelectionBackground(UIManager.getColor("Slider.background"));  // control, inactiveCaption, menu, scrollBar, windowBorder
    }
  }  
  
  /**
   * Returns the row number of the item indicated by the Point p. 
   */
  public int itemAtPoint(Point p) {
    int row = rowAtPoint(p);
    int column = columnAtPoint(p);
    
    if(row >= 0 && column == convertColumnIndexToView(0)) {
      Component comp = getCellRenderer(row, column).getTableCellRendererComponent(this, getValueAt(row, column), false, false, row, column);
      Rectangle r = getCellRect(row, column, true);
      r.width = comp.getWidth();
      if(r.contains(new Point(p.x, r.y))) {
        return row;
      }
    }
    
    return -1;
  }
  
  private boolean isOverItem(Point p) {
    return itemAtPoint(p) >= 0;
  }

  /**
   * Sets the row to be highlighted for special actions, for example when it is available as a drop target.
   */
  public void setHighlightedRow(int row) {
    if(row != highlightedRow) {
      int oldRow = highlightedRow;
      this.highlightedRow = row;
      
      if(oldRow >= 0) {
        repaint(getCellRect(oldRow, 0, false));
      }
      if(highlightedRow >= 0) {
        repaint(getCellRect(highlightedRow, 0, false));
      }
    }
  }
  
  public int getHighlightedRow() {
    return highlightedRow;
  }
  
  private final JLabel dummyRenderer = new JLabel();
  
  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
    int modelColumn = convertColumnIndexToModel(column);

    if(modelColumn != getColumnCount() - 1) {
      // System.err.println("Getting value for " + row + ", " + column + ": " + getRowCount());
      Object value = getValueAt(row, column);

      boolean isSelected = false;
      boolean hasFocus = false;

      // Only indicate the selection and focused cell if not printing and it is the first column
      if(!isPaintingForPrint() && modelColumn == 0) {
        isSelected = isCellSelected(row, column);

        boolean rowIsLead = selectionModel.getLeadSelectionIndex() == row;
        boolean colIsLead = columnModel.getSelectionModel().getLeadSelectionIndex() == column;

        hasFocus = rowIsLead && colIsLead && isFocusOwner();
        
        if(!isSelected) {
          isSelected = highlightedRow == row && modelColumn == 0;
        }
      }

      Component renderComponent = renderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);

      if(isRowInSelectionRect(renderComponent.getWidth(), row)) {
        renderComponent = renderer.getTableCellRendererComponent(this, value, !isSelected, hasFocus, row, column);
      }
      
      return renderComponent;
    }
    else {
      return dummyRenderer;
    }
  }
  
  private boolean isRowInSelectionRect(int cellWidth, int row) {
    if(selectionRect == null) {
      return false;
    }
    Rectangle cellRect = getCellRect(row, convertColumnIndexToView(0), true);
    cellRect.width = cellWidth;
    return cellRect.intersects(selectionRect);
  }

  private int getCellWidth(int row, int column) {
    Component comp = getCellRenderer(row, column).getTableCellRendererComponent(this, getValueAt(row, column), false, false, row, column);
    return comp.getWidth();
  }
  
//  @Override
//  public void setModel(final TableModel dataModel) {
//    super.setModel(new TableModel() {
//      @Override
//      public void addTableModelListener(TableModelListener l) {
//        dataModel.addTableModelListener(l);
//      }
//
//      @Override
//      public Class<?> getColumnClass(int columnIndex) {
//        return columnIndex == dataModel.getColumnCount() ? String.class : dataModel.getColumnClass(columnIndex);
//      }
//
//      @Override
//      public int getColumnCount() {
//        return dataModel.getColumnCount() + 1;
//      }
//
//      @Override
//      public String getColumnName(int columnIndex) {
//        return columnIndex == dataModel.getColumnCount() ? "FAKE" : dataModel.getColumnName(columnIndex);
//      }
//
//      @Override
//      public int getRowCount() {
//        return dataModel.getRowCount();
//      }
//
//      @Override
//      public Object getValueAt(int rowIndex, int columnIndex) {
//        return columnIndex == dataModel.getColumnCount() ? "" : dataModel.getValueAt(rowIndex, columnIndex);
//      }
//
//      @Override
//      public boolean isCellEditable(int rowIndex, int columnIndex) {
//        return columnIndex == dataModel.getColumnCount() ? false : dataModel.isCellEditable(rowIndex, columnIndex);
//      }
//
//      @Override
//      public void removeTableModelListener(TableModelListener l) {
//        dataModel.removeTableModelListener(l);
//      }
//
//      @Override
//      public void setValueAt(Object value, int rowIndex, int columnIndex) {
//        if(columnIndex == dataModel.getColumnCount()) {
//          throw new UnsupportedOperationException();
//        }
//        dataModel.setValueAt(value, rowIndex, columnIndex);
//      }
//    });
//  }
  
//  /**
//   * Provides a "fake" column model for the TableHeader which removes the presence of the fake hidden column.
//   * The TableHeader therefore won't try to render the hidden column or enable resize/move operations.
//   */
//  private static class MyTableColumnModel implements TableColumnModel {
//    private final TableColumnModel delegate;
//
//    public MyTableColumnModel(TableColumnModel delegate) {
//      this.delegate = delegate;
//    }
//
//    public void addColumn(TableColumn column) {
//      delegate.addColumn(column);
//    }
//
//    public void addColumnModelListener(TableColumnModelListener x) {
//      delegate.addColumnModelListener(x);
//    }
//
//    public TableColumn getColumn(int columnIndex) {
//      return delegate.getColumn(columnIndex);
//    }
//
//    public int getColumnCount() {
//      return delegate.getColumnCount() - 1; // Remove fake column
//    }
//
//    public int getColumnIndex(Object columnIdentifier) {
//      return delegate.getColumnIndex(columnIdentifier);
//    }
//
//    public int getColumnIndexAtX(int position) {
//      int columnIndex = delegate.getColumnIndexAtX(position);
//      if(columnIndex == getColumnCount()) {
//        return -1;
//      }
//      return columnIndex;
//    }
//
//    public int getColumnMargin() {
//      return delegate.getColumnMargin();
//    }
//
//    public Enumeration<TableColumn> getColumns() {
//      return delegate.getColumns();
//    }
//
//    public boolean getColumnSelectionAllowed() {
//      return delegate.getColumnSelectionAllowed();
//    }
//
//    public int getSelectedColumnCount() {
//      return delegate.getSelectedColumnCount();
//    }
//
//    public int[] getSelectedColumns() {
//      return delegate.getSelectedColumns();
//    }
//
//    public ListSelectionModel getSelectionModel() {
//      return delegate.getSelectionModel();
//    }
//
//    public int getTotalColumnWidth() {
//      return delegate.getTotalColumnWidth();
//    }
//
//    public void moveColumn(int columnIndex, int newIndex) {
//      delegate.moveColumn(columnIndex, newIndex);
//    }
//
//    public void removeColumn(TableColumn column) {
//      delegate.removeColumn(column);
//    }
//
//    public void removeColumnModelListener(TableColumnModelListener x) {
//      delegate.removeColumnModelListener(x);
//    }
//
//    public void setColumnMargin(int newMargin) {
//      delegate.setColumnMargin(newMargin);
//    }
//
//    public void setColumnSelectionAllowed(boolean flag) {
//      delegate.setColumnSelectionAllowed(flag);
//    }
//
//    public void setSelectionModel(ListSelectionModel newModel) {
//      delegate.setSelectionModel(newModel);
//    }
//  }
  
  private class FakeTableColumnModel implements TableColumnModel {
    private final TableColumnModel delegate;
    private final TableColumn fakeColumn;
    
    private boolean hasFake = true;
    
    public FakeTableColumnModel(TableColumnModel delegate) {
      this.delegate = delegate;
      fakeColumn = new TableColumn();
      fakeColumn.setCellRenderer(new TableCellRenderer() {
        final JLabel label = new JLabel();
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          return label;
        }
      });
    }
    
    @Override
    public void addColumn(TableColumn column) {
      delegate.addColumn(column);
      hasFake = true;
    }

    @Override
    public void addColumnModelListener(TableColumnModelListener x) {
      delegate.addColumnModelListener(x);
    }

    @Override
    public TableColumn getColumn(int columnIndex) {
      return columnIndex == delegate.getColumnCount() && hasFake ? fakeColumn : delegate.getColumn(columnIndex);
    }

    @Override
    public int getColumnCount() {
      return hasFake ? delegate.getColumnCount() + 1 : delegate.getColumnCount();  // Add Fake Column
    }

    @Override
    public int getColumnIndex(Object columnIdentifier) {
      return delegate.getColumnIndex(columnIdentifier);
    }

    @Override
    public int getColumnIndexAtX(int position) {
      return delegate.getColumnIndexAtX(position);
    }

    @Override
    public int getColumnMargin() {
      return delegate.getColumnMargin();
    }

    @Override
    public Enumeration<TableColumn> getColumns() {
      final Enumeration<TableColumn> columns = delegate.getColumns();
      
      return !hasFake ? columns : new Enumeration<TableColumn>() {
        private boolean fakeEnumerated = false;
        
        @Override
        public boolean hasMoreElements() {
          return !fakeEnumerated;
        }

        @Override
        public TableColumn nextElement() {
          if(!columns.hasMoreElements() && !fakeEnumerated) {
            fakeEnumerated = true;
            return fakeColumn;
          }
          return columns.nextElement();
        }
        
      };
    }

    @Override
    public boolean getColumnSelectionAllowed() {
      return delegate.getColumnSelectionAllowed();
    }

    @Override
    public int getSelectedColumnCount() {
      return delegate.getSelectedColumnCount();
    }

    @Override
    public int[] getSelectedColumns() {
      return delegate.getSelectedColumns();
    }

    @Override
    public ListSelectionModel getSelectionModel() {
      return delegate.getSelectionModel();
    }

    @Override
    public int getTotalColumnWidth() {
      return hasFake ? delegate.getTotalColumnWidth() + fakeColumn.getWidth() : delegate.getTotalColumnWidth();
    }

    @Override
    public void moveColumn(int columnIndex, int newIndex) {
      delegate.moveColumn(columnIndex, newIndex);
    }

    @Override
    public void removeColumn(TableColumn column) {
      if(column == fakeColumn) {
        hasFake = false;
      }
      else {
        delegate.removeColumn(column);
      }
    }

    @Override
    public void removeColumnModelListener(TableColumnModelListener x) {
      delegate.removeColumnModelListener(x);
    }

    @Override
    public void setColumnMargin(int newMargin) {
      delegate.setColumnMargin(newMargin);
    }

    @Override
    public void setColumnSelectionAllowed(boolean flag) {
      delegate.setColumnSelectionAllowed(flag);
    }

    @Override
    public void setSelectionModel(ListSelectionModel newModel) {
      delegate.setSelectionModel(newModel);
    }
  }
}
