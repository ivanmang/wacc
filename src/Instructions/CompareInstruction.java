package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class CompareInstruction extends Instruction{
  private Register dest;
  private Operand2 operand2;
  
  public CompareInstruction(Register dest, Operand2 operand2) {
    this.dest = dest;
    this.operand2 = operand2;
  }
  
  @Override
  public String toCode() {
    return "CMP " + dest + ", " + operand2;
  }
}

