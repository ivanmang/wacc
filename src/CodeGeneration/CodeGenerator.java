package CodeGeneration;

import Instructions.AddInstruction;
import Instructions.Branch.BranchEqualInstruction;
import Instructions.Branch.BranchInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.CmpInstruction;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.LtorgLabel;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.Operand2;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.Operand2.Operand2String;
import Instructions.PopInstruction;
import Instructions.PushInstruction;
import Instructions.Store.StoreByteInstruction;
import Instructions.Store.StoreInstruction;
import Instructions.StringInstruction;
import Instructions.SubInstruction;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;

public class CodeGenerator extends WaccParserBaseVisitor<Register>{

  private CodeStringBuilder builder = new CodeStringBuilder("");
  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();
  private int string_num = 0;
  private int labelnumber = 0;

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
//    builder.appendInstructions("PUSH", "{lr}");
    machine.add(new PushInstruction(Registers.lr));
    visitChildren(ctx);
//    builder.appendInstructions("LDR", "r0", "=0");
//    builder.appendInstructions("POP", "{pc}");
    machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', 0)));
    machine.add(new PopInstruction(Registers.pc));
    machine.add(new LtorgLabel());
    machine.endMsg();
    return null;
  }

  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    Register returnReg = visit(ctx.expr());

    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(returnReg)));
    machine.add(new BranchLinkInstruction("exit"));

    registers.freeReturnRegisters();
    return null;
  }

  @Override
  public Register visitExpr(ExprContext ctx) {
    return registers.r0;
  }

  @Override
  public Register visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    if (ctx.type().base_type() != null) { //base type
      int reserveByte = (ctx.type().base_type().BOOL() != null
          || ctx.type().base_type().CHAR() != null) ? 1 : 4;


      Register reg = registers.getRegister();
      machine.add(
          new SubInstruction(Registers.sp, new Operand2Int('#', reserveByte)));

      if (ctx.type().base_type().CHAR() != null) { //char
        String chr = ctx.assign_rhs().getText();
        machine.add(
            new MovInstruction(reg, new Operand2String('#', chr)));
      } else if (ctx.type().base_type().BOOL() != null) { //bool
        int value = ctx.assign_rhs().getText().equals("true") ? 1 : 0;

        machine
            .add(new MovInstruction(reg, new Operand2Int('#', value)));
      }


      if (ctx.type().base_type().INT() != null) { //int
        int number = Integer.parseInt(ctx.assign_rhs().getChild(0).getText());
        machine.add(
            new LoadInstruction(reg, new Operand2Int('=', number)));
      } else if(ctx.type().base_type().STRING()!=null){ //string
        String string = ctx.assign_rhs().getText();
        machine.addMsg(string);
        machine.add(new LoadInstruction(reg,
            new Operand2String('=', "msg_"+string_num)));
        string_num++;
      }

      if(ctx.type().base_type().CHAR()!= null || ctx.type().base_type().BOOL()!=null){
        machine.add(new StoreByteInstruction(reg,
            new Operand2Reg(Registers.sp, 0)));
      }else{
        machine.add(new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, 0)));
      }



      machine.add(
          new AddInstruction(Registers.sp, new Operand2Int('#', reserveByte)));
    }


    return null;
  }



  @Override
  public Register visitIfStat(IfStatContext ctx) {
    Register lastRegister = visit(ctx.expr());
    machine.add(new CmpInstruction(lastRegister,new Operand2Int('#',0)));
    Label elseLabel = new Label(labelnumber++); //else label
    Label thenLabel = new Label(labelnumber); //then label
    machine.add(new BranchEqualInstruction(elseLabel.toString()));
    machine.add(new BranchInstruction(thenLabel.toString()));
    machine.add(elseLabel);
    visit(ctx.stat(1));
    machine.add(thenLabel);
    visit(ctx.stat(0));
    return null;
  }

  @Override
  public Register visitWhileStat(WhileStatContext ctx) {
    Label startLabel = new Label(labelnumber++);
    Label loopLabel = new Label(labelnumber);
    machine.add(new BranchInstruction(startLabel.toString()));
    machine.add(loopLabel);
    visit(ctx.stat());
    machine.add(startLabel);
    Register lastRegister = visitExpr(ctx.expr());
    machine.add(new CmpInstruction(lastRegister,new Operand2Int('#',1)));
    machine.add(new BranchEqualInstruction(loopLabel.toString()));
    return null;
  }
}
