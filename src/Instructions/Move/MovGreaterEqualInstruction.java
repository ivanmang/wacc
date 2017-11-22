package Instructions.Move;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class MovGreaterEqualInstruction extends MovInstruction {

  public MovGreaterEqualInstruction(Register dest,
      Operand2 operand2) {
    super(dest, operand2, "MOVGE");
  }
}
