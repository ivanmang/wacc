package Instructions.Branch;

import Instructions.Instruction;

public class BranchInstruction extends Instruction {

  private String label;
  private String type;

  public BranchInstruction(String type, String label) {
    this.type = type;
    this.label = label;
  }

  public BranchInstruction(String label) {
    this("B", label);
  }

  @Override
  public String toCode() {
    return type + " " + label;
  }
}
