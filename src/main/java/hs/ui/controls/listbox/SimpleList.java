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
import hs.ui.controls.AbstractJComponent;
import hs.ui.events.ItemsEvent;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SimpleList<T> extends AbstractJComponent<SimpleList<T>, JScrollPane> {

  private final Notifier<ItemsEvent<T>> doubleClickNotifier = new Notifier<ItemsEvent<T>>();

  private final MyListModel swingModel = new MyListModel();
  
  private final JList<T> list; 
  
  public SimpleList() {
    super(new JScrollPane(), new JList<T>());
    
    list = (JList<T>)getSecondaryComponent();
    getComponent().setViewportView(list);
    
    model.onItemsInserted().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        swingModel.fireIntervalAdded(list, event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    model.afterItemsRemoved().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        swingModel.fireIntervalRemoved(list, event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    model.onItemsChanged().call(new EventListener<ItemRangeEvent>() {
      @Override
      public void onEvent(ItemRangeEvent event) {
        swingModel.fireContentsChanged(list, event.getFirstIndex(), event.getLastIndex());
      }
    });
    
    /*
     * Properties -> Swing
     */

    rowHeight.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        list.setFixedCellHeight(rowHeight.get());
      }
    });
    
    cellRenderer.set(list.getCellRenderer());
    cellRenderer.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        list.setCellRenderer(cellRenderer.get());
      }
    });
    
    firstSelectedIndex.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        list.setSelectedIndex(firstSelectedIndex.get());
      }
    });
    
    visibleRectangle.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        list.scrollRectToVisible(visibleRectangle.get());
      }
    });
    
    getComponent().getViewport().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        visibleRectangle.set(list.getVisibleRect());
      }
    });
    
    prototypeCellValue.onChange().call(new Listener() {
      @Override
      public void onEvent() {
        list.setPrototypeCellValue(prototypeCellValue.get());
      }
    });
    
    /*
     * Item Focus handling
     */
    
    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
          int leadIndex = list.getSelectionModel().getLeadSelectionIndex();
          
          if(leadIndex < model.size() && leadIndex >= 0) {
            itemFocusedNotifier.notifyListeners(new ItemsEvent<T>(SimpleList.this, model.get(leadIndex), null));
          }
          
          firstSelectedIndex.set(list.getSelectedIndex());
        }
      }
    });
    
    /*
     * Double click handling.
     */

    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {
          int row = list.locationToIndex(e.getPoint());
          
          if(row >= 0) {
            doubleClickNotifier.notifyListeners(new ItemsEvent<T>(SimpleList.this, model.get(row), e));
          }
        }
      }
    });
    
    list.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
          int index = list.getSelectedIndex();
          
          if(index >= 0) {
            doubleClickNotifier.notifyListeners(new ItemsEvent<T>(SimpleList.this, model.get(index), e));
            e.consume();
          }
        }
      }
    });

    /*
     * JList settings 
     */
    
    list.setModel(swingModel);
  }
  
  @Override
  protected SimpleList<T> self() {
    return this;
  }
  
  public final ListModel<T> model = new BasicListModel<>(new ArrayList<T>());
  public final Model<Integer> rowHeight = new ValueModel<>(16);
  public final Model<ListCellRenderer<? super T>> cellRenderer = new ValueModel<>();
  public final Model<Rectangle> visibleRectangle = new ValueModel<>();
  public final Model<Integer> firstSelectedIndex = new ValueModel<>();
  public final Model<T> prototypeCellValue = new ValueModel<>();
  
  public ListenerList<ItemsEvent<T>> onItemDoubleClick() {  // TODO Need better name for this.. activated?
    return doubleClickNotifier.getListenerList();
  }
  
  private final Notifier<ItemsEvent<T>> itemFocusedNotifier = new Notifier<ItemsEvent<T>>();
  
  public ListenerList<ItemsEvent<T>> onItemFocused() {
    return itemFocusedNotifier.getListenerList();
  }
  
  @SuppressWarnings("unchecked")
  public void selectFirstItem() {
    ((JList<T>)getSecondaryComponent()).setSelectedIndex(0);
  }
  
  public T getActiveRow() {
    return list.getSelectedValue();
  }

  private class MyListModel extends AbstractListModel<T> {
    @Override
    public int getSize() {
      return model.size();
    }

    @Override
    public T getElementAt(int index) {
      return model.get(index);
    }
    
    @Override
    public void fireContentsChanged(Object source, int index0, int index1) {
      super.fireContentsChanged(source, index0, index1);
    }
    
    @Override
    public void fireIntervalAdded(Object source, int index0, int index1) {
      super.fireIntervalAdded(source, index0, index1);
    }
    
    @Override
    public void fireIntervalRemoved(Object source, int index0, int index1) {
      super.fireIntervalRemoved(source, index0, index1);
    }
  }

}
