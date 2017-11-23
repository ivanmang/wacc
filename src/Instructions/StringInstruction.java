package Instructions;

public class StringInstruction extends Instruction{
  private String msg;

  public StringInstruction(String msg) {
    this.msg = msg;
  }

  @Override
  public String toCode() {
    return ".ascii\t" + msg;
  }
}
