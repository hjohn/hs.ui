package hs.ui.events;

public class ClosingEvent {
  private boolean veto;
  
  public void veto() {
    veto = true;
  }
  
  public boolean isVetoed() {
    return veto;
  }
}
