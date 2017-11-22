package Instructions.Labels;

import Instructions.Instruction;

public class LtorgLabel extends Instruction {
  public LtorgLabel() {
    setIndentation(1);
  }

  @Override
  public String toCode() {
    return ".ltorg";
  }
}
