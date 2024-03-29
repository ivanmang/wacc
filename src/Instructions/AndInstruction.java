package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class AndInstruction extends Instruction{
  private Register dest;
  private Register src;
  private Operand2 operand2;
  
  public AndInstruction(Register dest,Register src, Operand2 operand2) {
    this.dest = dest;
    this.src = src;
    this.operand2 = operand2;
  }
  
  @Override
  public String toCode() {
    return "AND " + dest + ", " + src + ", " + operand2;
  }
}

