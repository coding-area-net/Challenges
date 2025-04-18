package net.codingarea.challenges.plugin.challenges.type;

import javax.annotation.Nonnegative;

public interface IModifier {

  @Nonnegative
  int getValue();

  void setValue(int value);

  @Nonnegative
  int getMinValue();

  @Nonnegative
  int getMaxValue();

  void playValueChangeTitle();

}
