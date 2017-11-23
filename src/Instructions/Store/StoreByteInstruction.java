package Instructions.Store;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class StoreByteInstruction extends StoreInstruction{


  public StoreByteInstruction(Register dest, Operand2 operand2, String type) {
    super(dest,operand2,type);
  }

  public StoreByteInstruction(Register dest, Operand2 operand2) {
    this(dest, operand2, "STRB");
  }

  @Override
  public String toCode() {
    return "STRB " + dest + ", " + operand2;
  }
}
