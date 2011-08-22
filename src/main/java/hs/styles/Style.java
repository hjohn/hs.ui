package hs.styles;

public class Style {
  @SuppressWarnings("unused")
  private final String propertyName;
  @SuppressWarnings("unused")
  private final Object value;

  public Style(String propertyName, Object value) {
    this.propertyName = propertyName;
    this.value = value;
  }
}
