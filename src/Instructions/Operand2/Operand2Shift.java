package Instructions.Operand2;

import CodeGeneration.Register;

public class Operand2Shift implements Operand2 {
  
  private Register reg;
  private String shift;
  private int i;
  
  public Operand2Shift(Register reg,String shift, int i) {
    this.reg = reg;
    this.shift = shift;
    this.i = i;
  }
  
  @Override
  public String toString() {
    return reg + ", " + shift + " #" + Integer.toString(i);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand2Shift)) {
      return false;
    } else {
      Operand2Shift operand2Shift = (Operand2Shift) o;
      return this.toString().equals(operand2Shift.toString());
    }
  }
}
