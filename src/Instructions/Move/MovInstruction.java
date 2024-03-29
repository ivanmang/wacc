package Instructions.Move;

import CodeGeneration.Register;
import Instructions.Instruction;
import Instructions.Operand2.Operand2;

public class MovInstruction extends Instruction {
  private Register dest;
  private Operand2 operand2;
  private String type;
  private Register ret;

  public MovInstruction(Register dest, Operand2 operand2, String type) {
    this.dest = dest;
    this.operand2 = operand2;
    this.type = type;
  }

  public Register getDest() {
    return dest;
  }

  public Operand2 getOperand2() {
    return operand2;
  }

  public Register getRet() {
    return ret;
  }

  public MovInstruction(Register dest, Operand2 operand2) {
    this(dest, operand2, "MOV");
  }

  public MovInstruction(Register dest, Register ret){
    this.dest = dest;
    this.ret = ret;
    this.type = "MOV";
  }

  @Override
  public String toCode() {
    if(ret != null){
      return type + " " + dest + ", " + ret;
    }
    return type + " " + dest + ", " + operand2;
  }
}
