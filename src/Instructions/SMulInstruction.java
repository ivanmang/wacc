package Instructions;

import CodeGeneration.Register;

public class SMulInstruction extends Instruction {
  private Register dest;
  private Register src;
  
  public SMulInstruction(Register dest,Register src) {
    this.dest = dest;
    this.src = src;
  }
  
  
  @Override
  public String toCode() {
    return "SMULL " + dest + ", " +src +", "+dest+", "+src;
  }
}
