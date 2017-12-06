package Instructions.Operand2;

public class Operand2Empty implements Operand2{
  public Operand2Empty() { }

  @Override
  public String toString() {
    return "";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand2Empty)) {
      return false;
    } else {
      Operand2Empty operand2Empty = (Operand2Empty) o;
      return this.toString().equals(operand2Empty.toString());
    }
  }
}
