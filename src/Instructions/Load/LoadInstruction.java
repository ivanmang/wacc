package Instructions.Load;

import CodeGeneration.Register;
import Instructions.Instruction;
import Instructions.Operand2.Operand2;

public class LoadInstruction extends Instruction {
  private Register reg;
  private Operand2 operand2;
  private String type;

  public LoadInstruction(Register reg, Operand2 operand2, String type) {
    this.reg = reg;
    this.operand2 = operand2;
    this.type = type;
  }

  public Operand2 getOperand2() {
    return operand2;
  }

  public Register getReg() {
    return reg;
  }

  public LoadInstruction(Register reg, Operand2 operand2) {
    this(reg, operand2, "LDR");
  }

  @Override
  public String toCode() {
    return type + " " + reg + ", " + operand2;
  }
}
