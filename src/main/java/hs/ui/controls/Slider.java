package hs.ui.controls;

public class Slider extends AbstractSlider<Slider> {

  public Slider(int min, int max) {
    super(min, max);
  }

  public Slider() {
    super();
  }

  @Override
  protected Slider self() {
    return this;
  }

}
