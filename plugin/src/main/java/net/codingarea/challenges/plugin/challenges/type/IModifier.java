package net.codingarea.challenges.plugin.challenges.type;

public interface IModifier {

  int getValue();

  void setValue(int value);

  int getMinValue();

  int getMaxValue();

  void playValueChangeTitle();

}
