package hs.ui;

import java.awt.datatransfer.Transferable;

import javax.swing.TransferHandler.TransferSupport;

public interface ClipHandler<T> {
  public enum Action {COPY, MOVE}
  
  public Transferable createTransferable(T component, Action action);
  public boolean canImport(T component, TransferSupport support);
  public boolean importData(T component, TransferSupport support);
//  public ClipboardOwner getClipboardOwner();
}
