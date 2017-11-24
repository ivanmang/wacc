package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;
import Instructions.Operand2.Operand2Empty;

public class AddInstruction extends Instruction{
  private Register dest;
  private Operand2 operand2;
  private Operand2 operand3 = new Operand2Empty();

  public AddInstruction(Register dest, Operand2 operand2) {
    this.dest = dest;
    this.operand2 = operand2;
  }

  public AddInstruction(Register dest, Operand2 operand2, Operand2 operand3) {
    this.dest = dest;
    this.operand2 = operand2;
    this.operand3 = operand3;
  }

  @Override
  public String toCode() {
    return "ADD " + dest + ", " + operand2 + ", " + operand3;
  }
}
