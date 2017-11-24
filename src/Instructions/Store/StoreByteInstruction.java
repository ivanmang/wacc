package Instructions.Store;

import CodeGeneration.Register;
import Instructions.Operand2.Operand2;

public class StoreByteInstruction extends StoreInstruction {
  
  private boolean ispreindexing;
  public StoreByteInstruction(Register dest, Operand2 operand2, String type) {
    super(dest, operand2, type);
  }
  
  public StoreByteInstruction(Register dest, Operand2 operand2) {
    this(dest, operand2, "STRB");
  }
  
  public StoreByteInstruction(Register dest, Operand2 operand2, boolean bool) {
    this(dest, operand2, "STRB");
    this.ispreindexing = bool;
  }
  
  @Override
  public String toCode() {
    if (!ispreindexing) {
      return "STRB " + dest + ", " + operand2;
    } else {
      return "STRB " + dest + ", " + operand2 + "!";
    }
  }
}
