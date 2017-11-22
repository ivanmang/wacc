package Instructions;

import CodeGeneration.Register;

public class Operand2Reg implements Operand2{

  private Register reg;
  private int offset;
  private boolean isImmediateOffset;

  public Operand2Reg(Register reg, boolean isImmediateOffset) {
    this.reg = reg;
    this.isImmediateOffset = isImmediateOffset;
  }

  public Operand2Reg(Register reg, int offset) {
    this(reg, true);
    this.offset = offset;
  }

  public Operand2Reg(Register reg) {
    this(reg, false);
  }

  @Override
  public String toString() {
    if(isImmediateOffset) {
      if(offset != 0) {
        return "[" + reg + ", #" + offset + "]";
      } else {
        return "[" + reg + "]";
      }
    }
    return reg.toString();
  }
}
