package Instructions;

public class BranchLinkInstruction extends Instruction {

  private String label;

  public BranchLinkInstruction(String label) {
    this.label = label;
  }

  @Override
  public String toCode() {
    return "BL " + label;
  }
}
