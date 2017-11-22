package Instructions.Labels;

import Instructions.Instruction;

public class DataLabel extends Instruction {
  public DataLabel() {
    setIndentation(0);
  }

  @Override
  public String toCode() {
    return ".data";
  }
}
