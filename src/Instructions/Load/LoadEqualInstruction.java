package Instructions.Load;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class LoadEqualInstruction extends  LoadInstruction {

  public LoadEqualInstruction(Register reg, Operand2 operand2) {
    super(reg, operand2, "LDREQ");
  }
}
