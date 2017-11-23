import CodeGeneration.*;
import Instructions.AddInstruction;
import Instructions.Branch.BranchEqualInstruction;
import Instructions.Branch.BranchInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.CmpInstruction;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.LtorgLabel;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadEqualInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Load.LoadNotEqualInstruction;
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
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.SkipStatContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;
import Utils.*;
import com.sun.org.apache.regexp.internal.RE;

public class CodeGenerator extends WaccParserBaseVisitor<Register>{

  private CodeStringBuilder builder = new CodeStringBuilder("");
  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();
  private int string_num = 0;
  private int labelnumber = 0;
  private SymbolTable symbolTable;

  public CodeGenerator(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
  }

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

//  @Override
//  public Register visitExitStat(ExitStatContext ctx) {
//    Register returnReg = visit(ctx.expr());
//
//    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(returnReg)));
//    machine.add(new BranchLinkInstruction("exit"));
//
//    registers.freeReturnRegisters();
//    return null;
//  }



  @Override
  public Register visitExpr(ExprContext ctx) {
    return registers.r0;
  }

  @Override
  public Register visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    if (ctx.type().base_type() != null) { //base type

      int reserveByte = symbolTable.getSize();

      machine.add(
          new SubInstruction(Registers.sp, new Operand2Int('#',reserveByte )));

      Register reg = registers.getRegister();

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
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      }else{
        machine.add(new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
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

  public void ArrayIndexOutOfBoundsError(){
    String negIndex = "negative index\n\0";
    String largeIndex = "index too large\n\0";
    machine.addMsg("ArrayIndexOutOfBoundsError:");
  }

  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    Register returnReg = visit(ctx.expr());
    int number = Integer.parseInt(ctx.getChild(1).getText());
    machine.add(new LoadInstruction(registers.getRegister(), new Operand2Int('=', number)));
    machine.add(new MovInstruction(registers.getReturnRegister(), new Operand2Reg(registers.usedRegisters.getFirst())));
    machine.add(new BranchLinkInstruction("exit"));
    registers.freeReturnRegisters();
    return null;
  }

  @Override
  public Register visitSkipStat(SkipStatContext ctx) {
    return null;
  }

  @Override
  public Register visitPrintStat(PrintStatContext ctx) {
    machine.add(new LoadInstruction(registers.getRegister(), new Operand2String('=', "msg_0"))); // Need to know where to get the msg_0
//    LDR r4, =msg_0
//    21		MOV r0, r4
//    22		BL p_print_string
//    23		LDR r4, =189
//    24		MOV r0, r4
//    25		BL p_print_int
//    26		BL p_print_ln



//    if (thingsAfterPrint is String) {
    String msg = ctx.getChild(1).getText();
    machine.addMsg(msg);
    machine.addMsg("\"%.*s\\0\"");
    generate_printers("string");
//    }
//    if (thingsAfterPrint is Int) {
    machine.addMsg("\"%d\\0\"");
    generate_printers("int");
//    }
//      if (thingsAfterPrint is Bool) {
    machine.addMsg("\"true\\0\"");
    machine.addMsg("\"false\\0\"");
    generate_printers("bool");
//    }


    return null;
  }

  @Override
  public Register visitPrintlnStat(PrintlnStatContext ctx) {
    machine.addMsg("\"\\0\"");
    generate_printers("ln");
    return null;
  }

  @Override
  public Register visitReadStat(ReadStatContext ctx) {
//    if (ctx.getChild(1) is an int type) {
    machine.addMsg("\"%d\\0\"");
    generate_readers("int");
//    }
//    else if (ctx.getChild(1) is a char type){
    machine.addMsg("\" %c\\0\"");
    generate_readers("char");
//    }
    return null;
  }

  public void generate_readers (String str) {
    machine.add((new Label("p_read_" + str)));
    machine.add(new PushInstruction(Registers.lr));
    machine.add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
    machine.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg3")));
    machine.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4) ));
    machine.add(new BranchLinkInstruction("scanf"));
    machine.add(new PopInstruction(Registers.pc));

  }

  public void generate_printers (String str) {
    machine.add(new Label("p_print_" + str));
    if (str == "string") {
      machine.add(new PushInstruction(Registers.lr));
      machine.add(new LoadInstruction(Registers.r1, new Operand2Reg(registers.r0))); // LDR r1, [r0]
      machine.add(new AddInstruction(Registers.r2, new Operand2Reg(Registers.r1), new Operand2Int('#', 4)));
      machine.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg1")));
      machine.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4) ));
      machine.add(new BranchLinkInstruction("printf"));
      machine.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      machine.add(new BranchLinkInstruction("fflush"));
      machine.add(new PopInstruction(Registers.pc));

    } else if (str == "int") {
      machine.add(new PushInstruction(Registers.lr));
      machine.add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
      machine.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_2")));
      machine.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
      machine.add(new BranchLinkInstruction("printf"));
      machine.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      machine.add(new BranchLinkInstruction("fflush"));
      machine.add(new PopInstruction(Registers.pc));
    } else if (str == "ln") {
      machine.add(new PushInstruction(Registers.lr));
      machine.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_3")));
      machine.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
      machine.add(new BranchLinkInstruction("puts"));
      machine.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      machine.add(new BranchLinkInstruction("fflush"));
      machine.add(new PopInstruction(Registers.pc));
    } else if (str == "bool") {
      machine.add(new PushInstruction(Registers.lr));
      machine.add(new CmpInstruction(Registers.r0, new Operand2Int('#', 0)));
      machine.add(new LoadNotEqualInstruction(Registers.r0, new Operand2String('=', "msg_true")));
      machine.add(new LoadEqualInstruction(Registers.r0, new Operand2String('=', "msg_false")));
      machine.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
      machine.add(new BranchLinkInstruction("printf"));
      machine.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      machine.add(new BranchLinkInstruction("fflush"));
      machine.add(new PopInstruction(Registers.pc));
    }
  }

}


