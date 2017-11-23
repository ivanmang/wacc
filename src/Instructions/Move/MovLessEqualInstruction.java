package Instructions.Move;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class MovLessEqualInstruction extends MovInstruction {
  
  public MovLessEqualInstruction(Register dest,
                                 Operand2 operand2) {
    super(dest, operand2, "MOVLE");
  }
}


