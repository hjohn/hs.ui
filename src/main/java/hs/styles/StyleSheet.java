package hs.styles;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StyleSheet implements Iterable<Rule> {
  private final List<Rule> rules;

  public StyleSheet(Rule... rules) {
    this.rules = Arrays.asList(rules);
  }

  @Override
  public Iterator<Rule> iterator() {
    return rules.iterator();
  }
}
