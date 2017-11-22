package Instructions;

public abstract class Instruction {

  private int indentation = 0;

  public void setIndentation(int indentation) {
    this.indentation = indentation;
  }

  public int getIndentation() {
    return indentation;
  }

  public String toCode() {
    return "toCode not yet implemented for this instruction";
  }
}
