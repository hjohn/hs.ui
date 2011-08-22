package hs.ui.controls;

import hs.models.BasicOwnedListModel;
import hs.models.Convertor;
import hs.models.OwnedListModel;
import hs.models.OwnedModel;
import hs.models.OwnedValueModel;
import hs.models.events.EventListener;
import hs.models.events.ItemRangeEvent;
import hs.models.events.Listener;
import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.ui.ClipHandler;
import hs.ui.DefaultTransferHandler;
import hs.ui.HorizontalAlignment;
import hs.ui.events.ItemsEvent;
import hs.ui.swing.JExplorerTableHeader;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public abstract class AbstractListBox<O, T> extends AbstractJComponent<O, JScrollPane> {
  private final JTable table;
  private final TableRowSorter<TableModel> rowSorter;
  
  private final AbstractTableModel swingModel = new AbstractTableModel() {
    
    @Override
    public String getColumnName(int column) {
      return columns().get(column).text().get();
    }
    
    @Override
    public int getColumnCount() {
      return columns().size();
    }

    @Override
    public int getRowCount() {
      return items().size();
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return MyRenderer.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return items().get(rowIndex);
    }
  };
  
  public AbstractListBox() {
    super(new JScrollPane(), new JTable());
    
    table = (JTable)getSecondaryComponent();
    getComponent().setViewportView(table);
    getComponent().getViewport().setBackground(Color.WHITE); // TODO hardcoded
    getComponent().setBackground(Color.WHITE);  // TODO hardcoded..
    getComponent().setViewportBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));  // TODO hardcoded...
  
    minWidth().set(10);
    
//    // TODO need call-on-register
//    items().onChange().call(new Listener() {
//      @Override
//      public void onEvent() {
//        swingModel.fireTableDataChanged();
//      }
//    });
    
    items().onItemsInserted().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        System.err.println("Rows inserted " + event.getFirstIndex() + "-" + event.getLastIndex());
        swingModel.fireTableRowsInserted(event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    items().afterItemsRemoved().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        System.err.println("Rows removed " + event.getFirstIndex() + "-" + event.getLastIndex());
        swingModel.fireTableRowsDeleted(event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    items().onItemsChanged().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        System.err.println("Rows changed " + event.getFirstIndex() + "-" + event.getLastIndex());
        swingModel.fireTableRowsUpdated(event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    /*
     * RowMargin / RowHeight -> Swing
     */
    
    Listener rowSizeListener = new Listener() {
      @Override
      public void onEvent() {
        table.setRowHeight(rowHeight.get() + rowMargin.get());
        table.setRowMargin(rowMargin.get());
      }
    };

    rowMargin.onChange().call(rowSizeListener);  // TODO need call on register...
    rowHeight.onChange().call(rowSizeListener);
    
    rowSizeListener.onEvent();  // TODO remove when got call on register.
    
    /*
     * Double click handling.
     */
    
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {
          int row = table.rowAtPoint(e.getPoint());
          
          if(row >= 0) {
            doubleClickNotifier.notifyListeners(new ItemsEvent<T>(AbstractListBox.this, model.get(table.convertRowIndexToModel(row)), e));
          }
        }
      }
    });
    
    /*
     * Register listeners for notifying Swing of Columns Model changes.
     */
    
    EventListener<ItemRangeEvent> columnsListener = new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        swingModel.fireTableStructureChanged();
        
        for(int i = 0; i < columns().size(); i++) {
          Column<T> column = columns.get(i);
          TableColumn tableColumn = table.getColumnModel().getColumn(i);

          tableColumn.setPreferredWidth(column.width().get());
          rowSorter.setComparator(i, column.order().get());
        }
      }
    };
    
    columns.onItemsInserted().call(columnsListener);
    columns.afterItemsRemoved().call(columnsListener);
    
    /*
     * Configure JTable. 
     */
    
    table.setModel(swingModel);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setShowGrid(false);
    table.setDefaultRenderer(MyRenderer.class, new MyRenderer());
//    table.setAutoCreateRowSorter(true);
    rowSorter = new TableRowSorter<TableModel>(swingModel);
    table.setRowSorter(rowSorter);
    table.setRowMargin(0);   // Done to prevent headaches... row margin gets substracted from row height (so a 16 point label is renderer at 15 points if margin is 1 -- WTF!)

    table.setTableHeader(new JExplorerTableHeader(table.getColumnModel()));
    table.setFillsViewportHeight(true);
   
    // TODO make this generic
    table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F2"), "none");
    table.setDragEnabled(true);
    //table.setDropMode(DropMode.ON);
 //   table.setDropTarget(new DropTarget(table, DnDConstants.ACTION_COPY_OR_MOVE, new MyDropTargetListener(table), true));
  }
  
//  private final OwnedBeanModel<O, Color> bgColor = new OwnedBeanModel<O, Color>(self(), table, "background");
//  private final OwnedBeanModel<O, Color> fgColor = new OwnedBeanModel<O, Color>(self(), table, "foreground");
//  
//  public OwnedModel<O, Color> bgColor() {
//    return bgColor;
//  }
//
//  public OwnedModel<O, Color> fgColor() {
//    return fgColor;
//  }
  
  private final OwnedListModel<O, T> model = new BasicOwnedListModel<O, T>(self(), new ArrayList<T>());
  
  public OwnedListModel<O, T> items() {
    return model;
  }

  private final OwnedListModel<O, Column<T>> columns = new BasicOwnedListModel<O, Column<T>>(self(), new ArrayList<Column<T>>());

  public OwnedListModel<O, Column<T>> columns() {
    return columns;
  }
  
  private final OwnedModel<O, Integer> rowMargin = new OwnedValueModel<O, Integer>(self(), 1);
  private final OwnedModel<O, Integer> rowHeight = new OwnedValueModel<O, Integer>(self(), 16);
  
  public OwnedModel<O, Integer> rowMargin() {
    return rowMargin;
  }
  
  public OwnedModel<O, Integer> rowHeight() {
    return rowHeight;
  }
  
  private final Notifier<ItemsEvent<T>> doubleClickNotifier = new Notifier<ItemsEvent<T>>();

  public ListenerList<ItemsEvent<T>> onItemDoubleClick() {  // TODO Need better name for this
    return doubleClickNotifier.getListenerList();
  }
  
  public O setSortKeys(SortKey... keys) {
    rowSorter.setSortKeys(Arrays.asList(keys));
    return self();
  }
  
  public TableRowSorter<TableModel> getSorter() {
    return rowSorter;
  }
  
  public List<T> getSelectedItems() {
    List<T> selectedItems = new ArrayList<T>();
    
    for(int rowIndex : table.getSelectedRows()) {
      selectedItems.add(items().get(table.convertRowIndexToModel(rowIndex)));
    }
    
    return selectedItems;
  }
  
  public O setClipHandler(ClipHandler<O> clipHandler) {
    table.setTransferHandler(new DefaultTransferHandler<O>(self(), clipHandler));
    return self();
  }
  
  public class MyRenderer implements TableCellRenderer {
    private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    
    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(JTable table, Object valueParameter, boolean isSelectedParameter, boolean hasFocusParameter, int row, int column) {
      int columnIndex = table.convertColumnIndexToModel(column);

      boolean isSelected = columnIndex == 0 ? isSelectedParameter : false;
      boolean hasFocus = columnIndex == 0 ? hasFocusParameter : false;
        
      T value = (T)valueParameter;
      Convertor<T, Object> convertor = columns().get(columnIndex).convertor().get();
      
      if(convertor != null) {
        value = (T)convertor.convert(value);
      }

      TableCellRenderer columnRenderer = columns().get(columnIndex).renderer().get();
      TableCellRenderer parentRenderer = columnRenderer != null ? columnRenderer : renderer;

      Component renderComponent = parentRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      
      if(renderComponent instanceof JLabel) {
        HorizontalAlignment alignment = columns().get(columnIndex).alignment().get();
        
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
}
