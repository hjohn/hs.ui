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
import hs.ui.events.ItemsEvent;
import hs.ui.swing.JExplorerTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public abstract class AbstractListViewRowSorter<O, T> extends AbstractJComponent<O, JScrollPane> {
  private final JExplorerTable table;
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

  private long lastKeyTypedTime;
  private int startIndex;
  private String prefix;
  
  public AbstractListViewRowSorter() {
    super(new JScrollPane(), new JExplorerTable());
    
    table = (JExplorerTable)getSecondaryComponent();
    table.setAutoCreateColumnsFromModel(false);
    getComponent().setViewportView(table);
    getComponent().getViewport().setBackground(Color.WHITE); // TODO hardcoded, needed to prevent gray color when resizing
  
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
//        System.err.println("Rows inserted " + event.getFirstIndex() + "-" + event.getLastIndex());
        swingModel.fireTableRowsInserted(event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    items().afterItemsRemoved().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
//        System.err.println("Rows removed " + event.getFirstIndex() + "-" + event.getLastIndex());
        swingModel.fireTableRowsDeleted(event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    items().onItemsChanged().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
//        System.err.println("Rows changed " + event.getFirstIndex() + "-" + event.getLastIndex());
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
            doubleClickNotifier.notifyListeners(new ItemsEvent<T>(AbstractListViewRowSorter.this, model.get(table.convertRowIndexToModel(row)), e));
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

        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        
        for(int i = 0; i < columns().size(); i++) {
          Column<T> column = columns.get(i);
          columnModel.addColumn(column.getDelegate());
          column.getDelegate().setModelIndex(i);

//          TableColumn tableColumn = table.getColumnModel().getColumn(i);

//          tableColumn.setPreferredWidth(column.getInitialWidth());
          rowSorter.setComparator(i, column.order().get());
        }
        
        table.setColumnModel(columnModel);
      }
    };
    
    columns.onItemsInserted().call(columnsListener);
    columns.onItemsChanged().call(columnsListener);
    columns.afterItemsRemoved().call(columnsListener);

    /*
     * Type to search feature
     */
    
    table.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if(e.getModifiers() == 0) {
          startIndex = table.getSelectionModel().getLeadSelectionIndex();

          if(e.getWhen() - lastKeyTypedTime > 500) {
            prefix = "";
          }
          prefix += Character.toLowerCase(e.getKeyChar());

          if(startIndex == -1 || prefix.length() == 1 || !items().get(table.convertRowIndexToModel(startIndex)).toString().toLowerCase().startsWith(prefix)) {
            startIndex++;
          }
          
          lastKeyTypedTime = e.getWhen();
          
          int row = getNextMatch(startIndex);
          
          if(row == -1 && startIndex > 0) {
            row = getNextMatch(0);
          }
          
          if(row >= 0) {
            table.getSelectionModel().setSelectionInterval(row, row);
            table.getSelectionModel().setLeadSelectionIndex(row);
            table.getColumnModel().getSelectionModel().setSelectionInterval(0, 0);
            table.getColumnModel().getSelectionModel().setLeadSelectionIndex(0);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
            startIndex = row + 1;
          }
        }
      }

      private int getNextMatch(int startIndex) {
        for(int row = startIndex; row < table.getRowCount(); row++) {
          String displayValue = items().get(table.convertRowIndexToModel(row)).toString();

          if(displayValue.toLowerCase().startsWith(prefix)) {
            return row;
          }
        }
        
        return -1;
      }
    });
    
    /*
     * Configure JTable. 
     */
    
    table.setModel(swingModel);
    
    final MyRenderer renderer = new MyRenderer();
    table.setDefaultRenderer(MyRenderer.class, renderer);
    rowSorter = new TableRowSorter<TableModel>(swingModel);
    table.setRowSorter(rowSorter);

    // TODO make this generic
    table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F2"), "none");
    table.setDragEnabled(true);
    table.setDropMode(DropMode.ON);
  }
  
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
  
  public Point getViewPosition() {
    return getComponent().getViewport().getViewPosition();
  }
  
  public O setViewPosition(Point p) {
    getComponent().getViewport().setViewPosition(p);
    return self();
  }

  public ListSelectionModel getSelectionModel() {
    return table.getSelectionModel();
  }

  public O setSelectionModel(ListSelectionModel selectionModel) {
    table.setSelectionModel(selectionModel);
    return self();
  }

  public T getItemAt(Point p) {
    int row = table.itemAtPoint(p);
    
    if(row >= 0) {
      return items().get(table.getRowSorter().convertRowIndexToModel(row));
    }
    
    return null;
  }

  public O setHighlightedItem(T item) {
    if(item == null) {
      table.setHighlightedRow(-1);
    }
    else {
      table.setHighlightedRow(table.getRowSorter().convertRowIndexToView(items().indexOf(item)));
    }
    return self();
  }
  
  public O setSortKeys(SortKey... keys) {
    rowSorter.setSortKeys(Arrays.asList(keys));
    return self();
  }
  
  public TableRowSorter<TableModel> getSorter() {
    return rowSorter;
  }
  
  public int getFilteredCount() {
    return table.getRowSorter().getModelRowCount() - table.getRowSorter().getViewRowCount();
  }

  public int getSelectedCount() {
    
    return table.getSelectedRowCount();
  }

  public List<T> getSelectedItems() {
    List<T> selectedItems = new ArrayList<T>();

    for(int rowIndex : table.getSelectedRows()) {
      selectedItems.add(items().get(table.convertRowIndexToModel(rowIndex)));
    }
    
    return selectedItems;
  }
  
  public boolean isItemSelected(T item) {
    return table.getSelectionModel().isSelectedIndex(table.getRowSorter().convertRowIndexToView(items().indexOf(item)));
  }
  
  public O setClipHandler(ClipHandler<O> clipHandler) {
    final DefaultTransferHandler<O> th = new DefaultTransferHandler<O>(self(), clipHandler);
    table.setTransferHandler(th);
    System.out.println(table.getDropTarget());
    //table.setDropTarget(new DropTarget(table, null));
    DropTarget dropTarget = table.getDropTarget();
    
    try {
      dropTarget.addDropTargetListener(new DropTargetListener() {
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
          setHighlightedItem(null);
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
          setHighlightedItem(null);
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }
      });
    }
    catch(TooManyListenersException e) {
      throw new RuntimeException(e);
    }
  
    return self();
  }
  
  @SuppressWarnings("unchecked")
  private Object convertColumnValue(Object valueParameter, Column<T> column) {
    Convertor<T, Object> convertor = column.convertor().get();
    return convertor != null ? convertor.convert((T)valueParameter) : valueParameter;
  }
  
  
  public class MyRenderer implements TableCellRenderer {
    private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object valueParameter, boolean isSelectedParameter, boolean hasFocusParameter, int viewRow, int viewColumn) {
      int modelColumn = table.convertColumnIndexToModel(viewColumn);

      boolean isSelected = isSelectedParameter;
      boolean hasFocus = hasFocusParameter;

      Object value = convertColumnValue(valueParameter, columns().get(modelColumn));

      TableCellRenderer columnRenderer = columns().get(modelColumn).renderer().get();
      TableCellRenderer parentRenderer = columnRenderer != null ? columnRenderer : renderer;

      Component renderComponent = parentRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, viewRow, viewColumn);
      if(!isSelected) {
        renderComponent.setBackground(table.getBackground());
      }

      if(renderComponent instanceof JLabel) {
        switch(columns().get(modelColumn).alignment().get()) {
        case RIGHT:
          ((JLabel)renderComponent).setHorizontalAlignment(SwingConstants.RIGHT);
          break;
        case LEFT:
          ((JLabel)renderComponent).setHorizontalAlignment(SwingConstants.LEFT);
          break;
        default:
          ((JLabel)renderComponent).setHorizontalAlignment(SwingConstants.CENTER);
        }
      }
      return renderComponent;
    }
  }
}
