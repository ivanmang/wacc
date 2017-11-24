package Instructions.Store;

import CodeGeneration.Register;
import Instructions.Instruction;
import Instructions.Operand2.Operand2;

public class StoreInstruction extends Instruction {
  public Register dest;
  public Operand2 operand2;
  private String type;
  private boolean ispreindexing;

  public StoreInstruction(Register dest, Operand2 operand2, String type) {
    this.dest = dest;
    this.operand2 = operand2;
    this.type = type;
  }

  public StoreInstruction(Register dest, Operand2 operand2) {
    this(dest, operand2, "STR");
  }
  
  public StoreInstruction(Register dest, Operand2 operand2,boolean bool) {
    this(dest, operand2, "STR");
    this.ispreindexing = bool;
  }

  @Override
  public String toCode() {
    if(!ispreindexing) {
      return "STR " + dest + ", " + operand2;
    }
    else{
      return "STR " + dest + ", " + operand2 + "!";
    }
  }
}
