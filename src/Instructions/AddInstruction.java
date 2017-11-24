package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class AddInstruction extends Instruction{
  private Register dest;
  private Register src;
  private Operand2 operand2;
  private Boolean carry;

  public AddInstruction(Register dest,Register src, Operand2 operand2) {
    this.dest = dest;
    this.operand2 = operand2;
    this.src = src;
    this.carry = false;
  }

  public AddInstruction(Register dest,Register src,Operand2 operand2,Boolean carry) {
    this.dest = dest;
    this.src = src;
    this.operand2 = operand2;
    this.carry = carry;
  }

  @Override
  public String toCode() {
    if (!carry) {
      return "ADD " + dest + ", " +src+", " + operand2;
    } else {
      return "ADDS " + dest + ", "+src+", " + operand2;
    }
  }
}
