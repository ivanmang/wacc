package Instructions;

import CodeGeneration.Register;

public class PushInstruction extends Instruction{

  private Register pushReg;

  public PushInstruction(Register pushReg) {
    setIndentation(1);
    this.pushReg = pushReg;
  }

  @Override
  public String toCode() {
    return "PUSH {" + pushReg + "}";
  }
}
