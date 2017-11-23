package Instructions.Labels;

import Instructions.Instruction;

public class Label extends Instruction {

  private String name;
  public Label(String name) {
    setIndentation(0);
    this.name = name;
  }

  public Label(int number) {
    setIndentation(0);
    this.name = "L" + number;
  }



  @Override
  public String toCode() {
    return name + ":";
  }

  public String toString(){
    return name;
  }
}
