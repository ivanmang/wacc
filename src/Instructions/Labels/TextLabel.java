package Instructions.Labels;

import Instructions.Instruction;

public class TextLabel extends Instruction {
  public TextLabel() {
    setIndentation(0);
  }

  @Override
  public String toCode() {
    return ".text";
  }
}
