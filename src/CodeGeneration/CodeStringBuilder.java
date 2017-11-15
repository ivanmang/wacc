package CodeGeneration;

public class CodeStringBuilder {

  private String outputString = "";
  public CodeStringBuilder(String outputString) {
    this.outputString = outputString;
  }

  @Override
  public String toString() {
    return outputString;
  }

  public CodeStringBuilder appendLabel(String label) {
    outputString += "\t" + label + ":\n";
    return this;
  }

  public CodeStringBuilder appendDataStart() {
    outputString += "\t.data\n\n";
    return this;
  }

  public CodeStringBuilder appendData(String label, String content) {
    appendLabel(label);
    outputString += "\t\t.word " + content.length() + "\n";
    outputString += "\t\t.ascii  \"" + content + "\"\n";
    return this;
  }

  public CodeStringBuilder appendProgramStart() {
    outputString += "\n\t.text\n\n\t.global main\n";
    return this;
  }

  public CodeStringBuilder appendInstructions(String label, String elem) {
    outputString += "\t\t" + label + " " + elem + "\n";
    return this;
  }

  public CodeStringBuilder appendInstructions(String label, String elem1, String elem2) {
    outputString += "\t\t" + label + " " + elem1 + ", " + elem2 + "\n";
    return this;
  }

  public CodeStringBuilder appendInstructions(String label, String elem1, String elem2, String elem3) {
    outputString += "\t\t" + label + " " + elem1 + ", " + elem2 + ", " + elem3 + "\n";
    return this;
  }

  public CodeStringBuilder appendFunctionEnd() {
    outputString += "\t\t.ltorg\n";
    return this;
  }
}