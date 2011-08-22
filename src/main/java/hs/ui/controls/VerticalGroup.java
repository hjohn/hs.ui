package hs.ui.controls;

public class VerticalGroup extends AbstractGroup<VerticalGroup> {

  public VerticalGroup(int lines, int horizontalSpacing, int verticalSpacing) {
    super(true, lines, horizontalSpacing, verticalSpacing);
  }
  
  public VerticalGroup(int lines) {
    super(true, lines);
  }
  
  public VerticalGroup() {
    super(true, 1);
  }
  
  @Override
  protected VerticalGroup self() {
    return this;
  }
}
