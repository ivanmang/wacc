package Instructions.Move;

import CodeGeneration.Register;
import Instructions.Instruction;
import Instructions.Operand2.Operand2;

public class MovInstruction extends Instruction {
  private Register dest;
  private Operand2 operand2;
  private String type;

  public MovInstruction(Register dest, Operand2 operand2, String type) {
    this.dest = dest;
    this.operand2 = operand2;
    this.type = type;
  }

  public MovInstruction(Register dest, Operand2 operand2) {
    this(dest, operand2, "MOV");
  }

  @Override
  public String toCode() {
    return type + " " + dest + ", " + operand2;
  }
}