package Instructions.Move;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class MovNotEqualInstruction extends MovInstruction {

  public MovNotEqualInstruction(Register dest, Operand2 operand2) {
    super(dest, operand2, "MOVEQ");
  }
}
