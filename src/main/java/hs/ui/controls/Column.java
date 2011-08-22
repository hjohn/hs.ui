package hs.ui.controls;

import hs.models.Convertor;
import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.Listener;
import hs.ui.HorizontalAlignment;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class Column<T> {
  private final TableColumn column = new TableColumn();
  
  public Column() {
    text.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        column.setHeaderValue(text.get());
      }
    });

    width.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        column.setPreferredWidth(width.get());
      }
    });
  }
  
  public TableColumn getDelegate() {
    return column;
  }

  private final Model<String> text = new ValueModel<String>();
  public Model<String> text() { return text; }
  
  private final Model<Integer> width = new ValueModel<Integer>(100);
  public Model<Integer> width() { return width; }
  
  private final Model<Comparator<T>> order = new ValueModel<Comparator<T>>();
  public Model<Comparator<T>> order() { return order; }
  
  private final Model<Convertor<T, Object>> convertor = new ValueModel<Convertor<T, Object>>();
  public Model<Convertor<T, Object>> convertor() { return convertor; }
  
  private final Model<HorizontalAlignment> alignment = new ValueModel<HorizontalAlignment>();
  public Model<HorizontalAlignment> alignment() { return alignment; }

  private final Model<TableCellRenderer> renderer = new ValueModel<TableCellRenderer>();
  public Model<TableCellRenderer> renderer() { return renderer; }
}
