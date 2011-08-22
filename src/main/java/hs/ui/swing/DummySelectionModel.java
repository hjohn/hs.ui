package hs.ui.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class DummySelectionModel implements ListSelectionModel {
  private final List<ListSelectionListener> listeners = new ArrayList<ListSelectionListener>();
  private final DefaultListSelectionModel delegate = new DefaultListSelectionModel();
  private final SortedTableModel sortedModel;
  
  private boolean isAdjusting;

  public DummySelectionModel(SortedTableModel sortedModel) {
    this.sortedModel = sortedModel;
    
    sortedModel.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        if(e.getType() == TableModelEvent.DELETE) {
//          System.err.println("DELETE event : " + e.getFirstRow() + "-" + e.getLastRow() + ": first row select status = " + delegate.isSelectedIndex(e.getFirstRow()));
          delegate.removeIndexInterval(e.getFirstRow(), e.getLastRow());
        }
        else if(e.getType() == TableModelEvent.INSERT) {
          delegate.insertIndexInterval(e.getFirstRow(), e.getLastRow() - e.getFirstRow() + 1, true);
        }
      }
    });
  }

  @Override
  public void addListSelectionListener(ListSelectionListener x) {
    listeners.add(x);
  }

  @Override
  public void removeListSelectionListener(ListSelectionListener x) {
    listeners.remove(x);
  }

  @Override
  public void addSelectionInterval(int index0, int index1) {
    addSelectionInterval(index0, index1, false);
  }

  @Override
  public void clearSelection() {
    System.err.println("DSM: clearSelection()");
    // new Exception().printStackTrace();
    delegate.clearSelection();
    fireSelectionChanged(new ListSelectionEvent(this, 0, sortedModel.getRowCount() - 1, getValueIsAdjusting()));
  }

  @Override
  public int getAnchorSelectionIndex() {
    return sortedModel.convertRowIndexToView(delegate.getAnchorSelectionIndex());
  }

  @Override
  public int getLeadSelectionIndex() {
    return sortedModel.convertRowIndexToView(delegate.getLeadSelectionIndex());
  }

  @Override
  public int getMinSelectionIndex() {
    int min = delegate.getMinSelectionIndex();
    
    if(min >= 0) {
      int max = delegate.getMaxSelectionIndex();
      int viewMinIndex = Integer.MAX_VALUE;
      
      for(int i = min; i <= max; i++) {
        int viewIndex = sortedModel.convertRowIndexToView(i);
        
        if(viewIndex >= 0 && viewIndex < viewMinIndex) {
          viewMinIndex = viewIndex;
        }
      }
      
      return viewMinIndex;
    }
    
    return -1;
  }
  
  @Override
  public int getMaxSelectionIndex() {
    int min = delegate.getMinSelectionIndex();
    
    if(min >= 0) {
      int max = delegate.getMaxSelectionIndex();
      int viewMaxIndex = 0;
      
      for(int i = min; i <= max; i++) {
        int viewIndex = sortedModel.convertRowIndexToView(i);
        
        if(viewIndex > viewMaxIndex) {
          viewMaxIndex = viewIndex;
        }
      }
      
      return viewMaxIndex;
    }
    
    return -1;
  }

  @Override
  public int getSelectionMode() {
    return delegate.getSelectionMode();
  }

  @Override
  public boolean getValueIsAdjusting() {
    return isAdjusting;
  }

  @Override
  public void insertIndexInterval(int index, int length, boolean before) {
    // Ignored, handled by listener on the model
  }

  @Override
  public void removeIndexInterval(int index0, int index1) {
    // Ignored, handled by listener on the model
  }

  @Override
  public boolean isSelectedIndex(int index) {
    return delegate.isSelectedIndex(sortedModel.convertRowIndexToModel(index));
  }

  @Override
  public boolean isSelectionEmpty() {
    return delegate.isSelectionEmpty();
  }

  @Override
  public void removeSelectionInterval(int index0, int index1) {
    System.err.println("DSM: removeSelectionInterval(" + index0 + " - " + index1 + ")");

    int start = Math.min(index0, index1);
    int end = Math.max(index0, index1);
    boolean oldValue = getValueIsAdjusting();

    setValueIsAdjusting(true);

    for(int i = start; i <= end; i++) {
      int modelIndex = sortedModel.convertRowIndexToModel(i);
      delegate.removeSelectionInterval(modelIndex, modelIndex);
    }
    
    setValueIsAdjusting(oldValue);
    fireSelectionChanged(new ListSelectionEvent(this, 0, sortedModel.getRowCount() - 1, getValueIsAdjusting()));
  }

  @Override
  public void setAnchorSelectionIndex(int index) {
    delegate.setAnchorSelectionIndex(sortedModel.convertRowIndexToModel(index));
  }

  @Override
  public void setLeadSelectionIndex(int index) {
    delegate.setLeadSelectionIndex(sortedModel.convertRowIndexToModel(index));
  }

  @Override
  public void setSelectionInterval(int index0, int index1) {
    addSelectionInterval(index0, index1, true);
  }
  
  private void addSelectionInterval(int index0, int index1, boolean clear) {
    System.err.println("DSM: addSelectionInterval(" + index0 + " - " + index1 + ": clear = " + clear + ")");

    int start = Math.min(index0, index1);
    int end = Math.max(index0, index1);
    boolean oldValue = getValueIsAdjusting();

    setValueIsAdjusting(true);
    
    if(clear) {
      delegate.clearSelection();
    }
    
    for(int i = start; i <= end; i++) {
      int modelIndex = sortedModel.convertRowIndexToModel(i);
      delegate.addSelectionInterval(modelIndex, modelIndex);
    }
    
    setValueIsAdjusting(oldValue);
    fireSelectionChanged(new ListSelectionEvent(this, 0, sortedModel.getRowCount() - 1, getValueIsAdjusting()));
  }
  
  private void fireSelectionChanged(ListSelectionEvent event) {
    for(ListSelectionListener listener : listeners) {
      listener.valueChanged(event);
    }
  }

  @Override
  public void setSelectionMode(int selectionMode) {
    delegate.setSelectionMode(selectionMode);
  }

  @Override
  public void setValueIsAdjusting(boolean valueIsAdjusting) {
    if(valueIsAdjusting != isAdjusting) {
      isAdjusting = valueIsAdjusting;
      fireSelectionChanged(new ListSelectionEvent(this, 0, sortedModel.getRowCount() - 1, getValueIsAdjusting()));
    }
  }
}
