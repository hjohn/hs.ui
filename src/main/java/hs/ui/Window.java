package hs.ui;

import hs.models.OwnedModel;
import hs.models.events.ListenerList;
import hs.ui.events.ClosedEvent;

import java.awt.Container;

public interface Window extends Component {
  public Container getContainer();
  public BoundsModel<? extends Object> bounds();
  public Object pack();
  public ListenerList<ClosedEvent> onClosed();
  public OwnedModel<? extends Object, Boolean> visible();
}
