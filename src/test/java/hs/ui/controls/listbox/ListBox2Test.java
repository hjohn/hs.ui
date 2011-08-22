package hs.ui.controls.listbox;

import hs.models.Model;
import hs.models.ValueModel;
import hs.models.events.Listener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ListBox2Test {
  private ListBox2<String> listBox;
  
  @Before
  public void before() {
    listBox = new ListBox2<String>();
  }

  @Test
  public void shouldWork() {
  }

  @Test
  public void shouldTriggerItemEvents() {
    final Model<Integer> beforeItemsRemoved = new ValueModel<Integer>(0);
    final Model<Integer> afterItemsRemoved = new ValueModel<Integer>(0);
    final Model<Integer> itemsInserted = new ValueModel<Integer>(0);
    final Model<Integer> itemsChanged = new ValueModel<Integer>(0);
    
    listBox.items().beforeItemsRemoved().call(new Listener() {
      @Override
      public void onEvent() {
        Assert.assertEquals((int)afterItemsRemoved.get(), (int)beforeItemsRemoved.get());
        beforeItemsRemoved.set(beforeItemsRemoved.get() + 1);
      }
    });
    listBox.items().afterItemsRemoved().call(new Listener() {
      @Override
      public void onEvent() {
        Assert.assertEquals((int)afterItemsRemoved.get(), beforeItemsRemoved.get() - 1);
        afterItemsRemoved.set(afterItemsRemoved.get() + 1);
      }
    });
    listBox.items().onItemsInserted().call(new Listener() {
      @Override
      public void onEvent() {
        itemsInserted.set(itemsInserted.get() + 1);
      }
    });
    listBox.items().onItemsChanged().call(new Listener() {
      @Override
      public void onEvent() {
        itemsChanged.set(itemsChanged.get() + 1);
      }
    });
    
    listBox.items().add("Ikke");
    
    Assert.assertEquals(0, (int)beforeItemsRemoved.get());
    Assert.assertEquals(0, (int)afterItemsRemoved.get());
    Assert.assertEquals(1, (int)itemsInserted.get());
    Assert.assertEquals(0, (int)itemsChanged.get());

    listBox.items().add("A");
    listBox.items().add("B");
    listBox.items().add("C");
    
    Assert.assertEquals(0, (int)beforeItemsRemoved.get());
    Assert.assertEquals(0, (int)afterItemsRemoved.get());
    Assert.assertEquals(4, (int)itemsInserted.get());
    Assert.assertEquals(0, (int)itemsChanged.get());

    listBox.items().remove(2);
    
    Assert.assertEquals(1, (int)beforeItemsRemoved.get());
    Assert.assertEquals(1, (int)afterItemsRemoved.get());
    Assert.assertEquals(4, (int)itemsInserted.get());
    Assert.assertEquals(0, (int)itemsChanged.get());

    listBox.items().set(1, "AA");

    Assert.assertEquals(1, (int)beforeItemsRemoved.get());
    Assert.assertEquals(1, (int)afterItemsRemoved.get());
    Assert.assertEquals(4, (int)itemsInserted.get());
    Assert.assertEquals(1, (int)itemsChanged.get());
  }
}
