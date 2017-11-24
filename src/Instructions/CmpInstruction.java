package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class CmpInstruction extends Instruction{
  private Register dest;
  private Operand2 operand2;
  private Register curr;

  public CmpInstruction(Register dest, Operand2 operand2) {
    this.dest = dest;
    this.operand2 = operand2;
  }

  public CmpInstruction(Register dest, Register curr) {
    this.dest = dest;
    this.curr = curr;
  }

  @Override
  public String toCode() {
    if(curr != null){
      return "CMP " + dest + ", " + curr;
    }
    return "CMP " + dest + ", " + operand2;
  }
}
