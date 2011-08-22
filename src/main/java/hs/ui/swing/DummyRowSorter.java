package hs.ui.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class DummyRowSorter extends RowSorter<SortedTableModel> {
  private final SortedTableModel model;
 
  public DummyRowSorter(SortedTableModel model) {
    this.model = model;
  }
  
  @Override
  public void allRowsChanged() {
  }

  @Override
  public int convertRowIndexToModel(int index) {
    return index;
  }

  @Override
  public int convertRowIndexToView(int index) {
    return index;
  }

  @Override
  public SortedTableModel getModel() {
    return model;
  }

  @Override
  public int getModelRowCount() {
    return model.getRowCount();
  }

  @Override
  public List<? extends javax.swing.RowSorter.SortKey> getSortKeys() {
    return model.getSortKeys();
  }

  @Override
  public int getViewRowCount() {
    return model.getRowCount();
  }

  @Override
  public void modelStructureChanged() {
  }

  @Override
  public void rowsDeleted(int firstRow, int endRow) {
  }

  @Override
  public void rowsInserted(int firstRow, int endRow) {
  }

  @Override
  public void rowsUpdated(int firstRow, int endRow) {
  }

  @Override
  public void rowsUpdated(int firstRow, int endRow, int column) {
  }

  @Override
  public void setSortKeys(List<? extends javax.swing.RowSorter.SortKey> keys) {
    model.setSortKeys(keys);
  }

  @Override
  public void toggleSortOrder(int column) {
    List<? extends SortKey> sortKeys = getSortKeys();
    List<SortKey> newSortKeys = new ArrayList<SortKey>();
    SortOrder order = SortOrder.ASCENDING;
    
    if(sortKeys.size() > 0) {
      SortKey sortKey = sortKeys.get(0);
      
      if(sortKey.getColumn() == column) {
        order = sortKey.getSortOrder() != SortOrder.ASCENDING ? SortOrder.ASCENDING : SortOrder.DESCENDING;
      }
    }

    newSortKeys.add(new SortKey(column, order));
    
    setSortKeys(newSortKeys);
  }
}
