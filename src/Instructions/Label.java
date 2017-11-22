package Instructions;

public class Label extends Instruction {

  String name;
  public Label(String name) {
    setIndentation(0);
    this.name = name;
  }

  @Override
  public String toCode() {
    return name;
  }
}
