package Instructions.Store;

import CodeGeneration.Register;
import Instructions.Instruction;
import Instructions.Operand2.Operand2;

public class StoreInstruction extends Instruction {
  public Register dest;
  public Operand2 operand2;
  private String type;

  public StoreInstruction(Register dest, Operand2 operand2, String type) {
    this.dest = dest;
    this.operand2 = operand2;
    this.type = type;
  }

  public StoreInstruction(Register dest, Operand2 operand2) {
    this(dest, operand2, "STR");
  }

  @Override
  public String toCode() {
    return "STR " + dest + ", " + operand2;
  }
}
