package Instructions.Load;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class LoadCSInstruction extends LoadInstruction {
  public LoadCSInstruction(Register reg, Operand2 operand2) {
    super(reg, operand2, "LDRCS");
  }
}
