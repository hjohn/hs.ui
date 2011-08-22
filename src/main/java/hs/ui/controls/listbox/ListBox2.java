package hs.ui.controls.listbox;

import hs.models.BasicListModel;
import hs.models.ListModel;
import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.EventListener;
import hs.models.events.ItemRangeEvent;
import hs.models.events.Listener;
import hs.models.events.ListenerList;
import hs.models.events.Notifier;
import hs.ui.ClipHandler;
import hs.ui.DefaultTransferHandler;
import hs.ui.controls.AbstractJComponent;
import hs.ui.controls.Column;
import hs.ui.events.ItemsEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class ListBox2<T> extends AbstractJComponent<ListBox2<T>, JScrollPane> {
  private final JTable table;
  
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
  
  public ListBox2() {
    super(new JScrollPane(), new JTable());
    
    table = (JTable)getSecondaryComponent();
    
    getComponent().setViewportView(table);
//    getComponent().getViewport().setBackground(Color.WHITE); // TODO hardcoded
//    getComponent().setBackground(Color.WHITE);  // TODO hardcoded..
//    getComponent().setViewportBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));  // TODO hardcoded...
  
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
            doubleClickNotifier.notifyListeners(new ItemsEvent<T>(ListBox2.this, model.get(table.convertRowIndexToModel(row)), e));
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
        }
      }
    };
    
    columns.onItemsInserted().call(columnsListener);
    columns.afterItemsRemoved().call(columnsListener);
    
    /*
     * Item Selection handling
     */
    
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
          itemSelectedNotifier.notifyListeners(new ItemsEvent<T>(ListBox2.this, model.get(table.getSelectionModel().getLeadSelectionIndex()), null));
        }
      }
    });
    
    /*
     * Configure JTable. 
     */

    table.setTableHeader(null);
    table.setModel(swingModel);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setShowGrid(false);
    table.setDefaultRenderer(MyRenderer.class, new MyRenderer<T>(columns()));
//    rowSorter = new TableRowSorter<TableModel>(swingModel);
//    table.setRowSorter(rowSorter);
    table.setRowMargin(0);   // Done to prevent headaches... row margin gets substracted from row height (so a 16 point label is renderer at 15 points if margin is 1 -- WTF!)

//    table.setTableHeader(new JExplorerTableHeader(table.getColumnModel()));
    table.setFillsViewportHeight(true);
   
    
    //table.setDropMode(DropMode.ON);
 //   table.setDropTarget(new DropTarget(table, DnDConstants.ACTION_COPY_OR_MOVE, new MyDropTargetListener(table), true));
  }
  
  private final ListModel<T> model = new BasicListModel<T>(new ArrayList<T>());
  public ListModel<T> items() { return model; }

  private final ListModel<Column<T>> columns = new BasicListModel<Column<T>>(new ArrayList<Column<T>>());
  public ListModel<Column<T>> columns() { return columns; }
  
  private final Model<Integer> rowMargin = new ValueModel<Integer>(1);
  public Model<Integer> rowMargin() { return rowMargin; }
  
  private final Model<Integer> rowHeight = new ValueModel<Integer>(16);
  public Model<Integer> rowHeight() { return rowHeight; }
  
  private final Notifier<ItemsEvent<T>> doubleClickNotifier = new Notifier<ItemsEvent<T>>();

  public ListenerList<ItemsEvent<T>> onItemDoubleClick() {  // TODO Need better name for this.. activated?
    return doubleClickNotifier.getListenerList();
  }
  
  private final Notifier<ItemsEvent<T>> itemSelectedNotifier = new Notifier<ItemsEvent<T>>();
  
  public ListenerList<ItemsEvent<T>> onItemSelected() {
    return itemSelectedNotifier.getListenerList();
  }
  
  public List<T> getSelectedItems() {
    List<T> selectedItems = new ArrayList<T>();
    
    for(int rowIndex : table.getSelectedRows()) {
      selectedItems.add(items().get(table.convertRowIndexToModel(rowIndex)));
    }
    
    return selectedItems;
  }
  
  public T getActiveRow() {
    return items().get(table.convertRowIndexToModel(table.getSelectionModel().getLeadSelectionIndex()));
  }
  
  public void setClipHandler(ClipHandler<ListBox2<T>> clipHandler) {
    table.setTransferHandler(new DefaultTransferHandler<ListBox2<T>>(self(), clipHandler));
  }
  
  

  @Override
  protected ListBox2<T> self() {
    return this;
  }
}
