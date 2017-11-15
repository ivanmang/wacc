package CodeGeneration;

import java.util.ArrayList;
import java.util.List;

public class InstructionStringBuilder {
  private String instr = "";
  private String label = "";
  private List<String> elements = new ArrayList<>();

  public InstructionStringBuilder(String instr) {
    this.instr = instr;
  }

  public InstructionStringBuilder addLabel(String label) {
    this.label = label;
    instr += "\t\t" + label;
    return this;
  }

  public InstructionStringBuilder addElem(String elem) {
    if(elements.size() == 0) {
      instr += " " + elem;
    } else {
      instr += ", " + elem;
    }
    elements.add(elem);
    return this;
  }

  @Override
  public String toString() {
   return instr;
  }
}
