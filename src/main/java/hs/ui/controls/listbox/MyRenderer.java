package hs.ui.controls.listbox;

import java.awt.Component;

import hs.models.Convertor;
import hs.models.ListModel;
import hs.ui.HorizontalAlignment;
import hs.ui.controls.Column;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class MyRenderer<T> implements TableCellRenderer {
  private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
  private final ListModel<Column<T>> columns;
  
  public MyRenderer(ListModel<Column<T>> columns) {
    this.columns = columns;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public Component getTableCellRendererComponent(JTable table, Object valueParameter, boolean isSelectedParameter, boolean hasFocusParameter, int row, int column) {
    int columnIndex = table.convertColumnIndexToModel(column);

    boolean isSelected = columnIndex == 0 ? isSelectedParameter : false;
    boolean hasFocus = columnIndex == 0 ? hasFocusParameter : false;
      
    T value = (T)valueParameter;
    Convertor<T, Object> convertor = columns.get(columnIndex).convertor().get();
    
    if(convertor != null) {
      value = (T)convertor.convert(value);
    }

    TableCellRenderer columnRenderer = columns.get(columnIndex).renderer().get();
    TableCellRenderer parentRenderer = columnRenderer != null ? columnRenderer : renderer;

    Component renderComponent = parentRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    
    if(renderComponent instanceof JLabel) {
      HorizontalAlignment alignment = columns.get(columnIndex).alignment().get();
      
      if(alignment == HorizontalAlignment.RIGHT) {
        ((JLabel)renderComponent).setHorizontalAlignment(SwingConstants.RIGHT);
      }
      else if(alignment == HorizontalAlignment.LEFT) {
        ((JLabel)renderComponent).setHorizontalAlignment(SwingConstants.LEFT);
      }
      else {
        ((JLabel)renderComponent).setHorizontalAlignment(SwingConstants.CENTER);
      }
    }
    
    return renderComponent;
  }
}