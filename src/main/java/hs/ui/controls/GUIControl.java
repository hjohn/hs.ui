package hs.ui.controls;

import hs.smartlayout.Constraints;
import hs.ui.Control;

public interface GUIControl extends Control<GUIControl> {
  Constraints getLayoutConstraints();
}
