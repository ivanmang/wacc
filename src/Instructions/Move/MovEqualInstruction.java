package Instructions.Move;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class MovEqualInstruction extends MovInstruction {

  public MovEqualInstruction(Register dest, Operand2 operand2) {
    super(dest, operand2, "MOVEQ");
  }
}
