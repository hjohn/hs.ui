package hs.ui;

import hs.ui.ClipHandler.Action;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class DefaultTransferHandler<T> extends TransferHandler {
  private final T comp;
  private final ClipHandler<T> clipHandler;

  public DefaultTransferHandler(T comp, ClipHandler<T> clipHandler) {
    this.comp = comp;
    this.clipHandler = clipHandler;
  }
  
  @Override
  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }
  
  @Override
  protected Transferable createTransferable(JComponent c) {
    
    /*
     * Never called for Ctrl-C, Ctrl-X as exportToClipboard was overriden, but will be called for D&D operations.
     */
    
    return clipHandler.createTransferable(comp, Action.COPY);
  }
  
  @Override
  public boolean canImport(TransferSupport support) {
    return clipHandler.canImport(comp, support);
  }
  
  @Override
  public boolean importData(TransferSupport support) {
    return clipHandler.importData(comp, support);
  }
  
  @Override
  public void exportToClipboard(JComponent c, Clipboard clip, int action) throws IllegalStateException {
    if((action == COPY || action == MOVE) && (getSourceActions(c) & action) != 0) {

//      Transferable t = createTransferable(c);
      Transferable t = clipHandler.createTransferable(comp, action == COPY ? Action.COPY : Action.MOVE);
      
      if(t != null) {
        try {
//          clip.setContents(t, clipHandler.getClipboardOwner());
          clip.setContents(t, null);
          exportDone(c, t, action);
          return;
        }
        catch(IllegalStateException ise) {
          exportDone(c, t, NONE);
          throw ise;
        }
      }
    }

    exportDone(c, null, NONE);
  }
}
