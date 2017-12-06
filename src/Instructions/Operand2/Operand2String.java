package Instructions.Operand2;

public class Operand2String implements Operand2 {

  private String string;
  private char type;

  public Operand2String(char type, String string) {
    this.type = type;
    this.string = string;
  }

  @Override
  public String toString() {
    return type + "" + string;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand2String)) {
      return false;
    } else {
      Operand2String operand2String = (Operand2String) o;
      return this.toString().equals(operand2String.toString());
    }
  }
}

