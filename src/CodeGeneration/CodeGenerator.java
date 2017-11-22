package CodeGeneration;

import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParserBaseVisitor;

public class CodeGenerator extends WaccParserBaseVisitor<Register>{

  private CodeStringBuilder builder = new CodeStringBuilder("");
  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();

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
    builder.appendInstructions("LDR", "r0", "=0");
    builder.appendInstructions("POP", "{pc}");
    builder.appendFunctionEnd();
    return null;
  }

  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    Register usedReg = registers.getRegister();
    Register paramReg = registers.getReturnRegister();
    Register exprReg = visit(ctx.expr());
    builder.appendInstructions("LDR", usedReg.toString(), "=");
    builder.appendInstructions("MOV", paramReg.toString(), usedReg.toString());
    builder.appendInstructions("BL", "exit");
    return null;
  }
}
