package hs.ui.controls;


public class HorizontalGroup extends AbstractGroup<HorizontalGroup> {

  public HorizontalGroup(int lines, int horizontalSpacing, int verticalSpacing) {
    super(false, lines, horizontalSpacing, verticalSpacing);
  }

  public HorizontalGroup(int lines) {
    super(false, lines);
  }
  
  public HorizontalGroup() {
    super(false, 1);
  }
  
  @Override
  protected HorizontalGroup self() {
    return this;
  }
}
