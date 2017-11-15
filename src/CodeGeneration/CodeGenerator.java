package CodeGeneration;

import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParserBaseVisitor;

public class CodeGenerator extends WaccParserBaseVisitor<Register>{

  private CodeStringBuilder builder = new CodeStringBuilder("");

  @Override
  public String toString() {
    return builder.toString();
  }

  @Override
  public Register visitProg(ProgContext ctx) {
    InstructionStringBuilder instr = new InstructionStringBuilder("");
    builder.appendProgramStart().appendLabel("main");
    builder.appendInstructions("PUSH", "{lr}");
    visitChildren(ctx);
    builder.appendInstructions("POP", "{pc}");
    builder.appendFunctionEnd();
    return null;
  }

  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    builder.appendInstructions("BL", "exit");
    return super.visitExitStat(ctx);
  }
}
