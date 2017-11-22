package Instructions;

import CodeGeneration.Register;

public class PopInstruction extends Instruction{

  private Register pushReg;

  public PopInstruction(Register pushReg) {
    setIndentation(1);
    this.pushReg = pushReg;
  }

  @Override
  public String toCode() {
    return "POP {" + pushReg + "}";
  }
}
