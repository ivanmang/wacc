package Instructions.Operand2;

public class Operand2Char implements Operand2 {

  private char character;
  private char type;

  public Operand2Char(char character, char type) {
    this.character = character;
    this.type = type;
  }

  @Override
  public String toString() {
    return type + "\'" + character + '\'';
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Operand2Char)) {
      return false;
    } else {
      Operand2Char operand2Char = (Operand2Char) o;
      return this.toString().equals(operand2Char.toString());
    }
  }
}