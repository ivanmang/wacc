package Instructions.Labels;

import Instructions.Instruction;

public class GlobalMainLabel extends Instruction {

  public GlobalMainLabel() {
    setIndentation(0);
  }

  @Override
  public String toCode() {
    return ".global main";
  }
}
