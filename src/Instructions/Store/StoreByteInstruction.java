package Instructions.Store;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class StoreByteInstruction extends StoreInstruction{

  public StoreByteInstruction(Register dest, Operand2 operand2) {
    super(dest, operand2, "STRB");
  }
}
