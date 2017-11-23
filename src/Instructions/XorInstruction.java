package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class XorInstruction extends Instruction {
  private Register dest;
  private Operand2 operand2;
  private Register src;
  
  public XorInstruction(Register dest,Register src, Operand2 operand2) {
    this.dest = dest;
    this.src = src;
    this.operand2 = operand2;
  }
  
  @Override
  public String toCode() {
    return "EOR " + dest + ", " + src + ", " + operand2;
  }
}

