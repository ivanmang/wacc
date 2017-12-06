package Instructions.Operand2;

public class Operand2Int implements Operand2 {
  private int number;
  private char type;

  public Operand2Int(char type, int number) {
    this.type = type;
    this.number = number;
  }

  @Override
  public String toString() {
    return type + "" + String.valueOf(number);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand2Int)) {
      return false;
    } else {
      Operand2Int operand2Int = (Operand2Int) o;
      return this.toString().equals(operand2Int.toString());
    }
  }
}
