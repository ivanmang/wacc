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
}