package CodeGeneration;

import Instructions.AddInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.LtorgLabel;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.Operand2;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.PopInstruction;
import Instructions.PushInstruction;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.SkipStatContext;
import antlr.WaccParser.StatToStatContext;
import antlr.WaccParserBaseVisitor;


public class CodeGenerator extends WaccParserBaseVisitor<Register>{

  private CodeStringBuilder builder = new CodeStringBuilder("");
  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();

  public String generateCode() {
    return machine.toCode();
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  @Override
  public Register visitProg(ProgContext ctx) {
    machine.addFunctionStart("main");
    builder.appendInstructions("PUSH", "{lr}");
    machine.add(new PushInstruction(Registers.lr));
    visitChildren(ctx);
    builder.appendInstructions("LDR", "r0", "=0");
    builder.appendInstructions("POP", "{pc}");
    machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', 0)));
    machine.add(new PopInstruction(Registers.pc));
    machine.add(new TextLabel());
    machine.add(new GlobalMainLabel());
    machine.add(new LtorgLabel());
    return null;
  }

  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    Register returnReg = visit(ctx.expr());
    builder.appendInstructions("LDR", Registers.r4.toString(), "=-1");
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(returnReg)));
    builder.appendInstructions("MOV", Registers.r0.toString(), Registers.r4.toString());
    machine.add(new BranchLinkInstruction("exit"));
    builder.appendInstructions("BL", "exit");
    registers.freeReturnRegisters();
    return null;
  }

  @Override
  public Register visitSkipStat(SkipStatContext ctx) {
    return null;
  }


  @Override
  public Register visitReadStat(ReadStatContext ctx) {
    builder.appendInstructions("PUSH", "{lr}");
    machine.add(new PushInstruction(Registers.lr));
//    machine.add(new MovInstruction(new Operand2Reg(returnReg), Registers.r0));
//    machine.add(new LoadInstruction(Registers.r0, new Operand2Reg('=', msg)));
//    machine.add(new AddInstruction(Register.r0, ));
    builder.appendInstructions("BL", "scanf");
    machine.add(new PopInstruction(Registers.pc));
    return null;
  }

  @Override
  public Register visitPrintStat(PrintStatContext ctx) {
    machine.add(new PushInstruction(Registers.lr));
//    machine.add(new LoadInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
//    machine.add(new AddInstruction(Register.r2, Register.r0, new Operand2Int('#', 4);
    machine.add(new LoadInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
//    machine.add(new AddInstruction(Register.r2, Register.r0, new Operand2Int('#', 4);
    builder.appendInstructions("BL", "printf");
    machine.add(new MovInstruction((Registers.r0), new Operand2Int('#', 0)));
    builder.appendInstructions("BL", "fflush");
    machine.add(new PopInstruction(Registers.pc));
    return null;
  }

  @Override
  public Register visitPrintlnStat(PrintlnStatContext ctx) {
    machine.add(new PushInstruction(Registers.lr));
//    machine.add(new LoadInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
//    machine.add(new AddInstruction(Register.r2, Register.r0, new Operand2Int('#', 4);
    machine.add(new LoadInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
//    machine.add(new AddInstruction(Register.r2, Register.r0, new Operand2Int('#', 4);
    builder.appendInstructions("BL", "printf");
    machine.add(new MovInstruction((Registers.r0), new Operand2Int('#', 0)));
    builder.appendInstructions("BL", "fflush");
    machine.add(new PopInstruction(Registers.pc));
    return null;
  }

}
