package Instructions;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class SubInstruction extends Instruction {
  private Register dest;
  private Register src;
  private Operand2 operand2;
  private Boolean carry;
  private Boolean reverse;
  
  public SubInstruction(Register dest,Register src, Operand2 operand2) {
    this.dest = dest;
    this.operand2 = operand2;
    this.src = src;
    this.carry = false;
    this.reverse = false;
  }
  
  public SubInstruction(Register dest,Register src,Operand2 operand2,Boolean carry) {
    this.dest = dest;
    this.src = src;
    this.operand2 = operand2;
    this.carry = carry;
    this.reverse = false;
  }
  
  public SubInstruction(Register dest,Register src,Operand2 operand2,Boolean carry,Boolean reverse) {
    this.dest = dest;
    this.src = src;
    this.operand2 = operand2;
    this.carry = carry;
    this.reverse = reverse;
    
  }
  
  @Override
  public String toCode() {
    
    if (!carry) {
      return "SUB " + dest + ", " +src+", " + operand2;
    } else {
      if (!reverse) {
        return "SUBS " + dest + ", " + src + ", " + operand2;
      } else {
        return "RSBS " + dest + ", " + src + ", " + operand2;
      }
    }
  }
}

