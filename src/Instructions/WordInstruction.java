package Instructions;

public class WordInstruction extends Instruction {

  private int length;

  public WordInstruction(int length) {
    this.length = length;
  }

  @Override
  public String toCode() {
    return ".word " + length;
  }
}
