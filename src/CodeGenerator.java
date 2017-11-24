import CodeGeneration.*;
import Instructions.*;
import Instructions.Branch.BranchEqualInstruction;
import Instructions.Branch.BranchInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.LtorgLabel;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadByteInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Move.*;
import Instructions.Operand2.Operand2;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.Operand2.Operand2String;
import Instructions.Store.StoreByteInstruction;
import Instructions.Store.StoreInstruction;
import antlr.WaccParser;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;
import Utils.*;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.swing.*;
import java.util.Arrays;

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

  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    Register returnReg = visit(ctx.expr());

    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(returnReg)));
    machine.add(new BranchLinkInstruction("exit"));

    registers.freeReturnRegisters();
    return null;
  }
  

  @Override
  public Register visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    if (ctx.type().base_type() != null) { //base type

      int reserveByte = symbolTable.getSize();

      machine.add(
          new SubInstruction(Registers.sp,Registers.sp, new Operand2Int('#',reserveByte )));
      Register reg = visit(ctx.assign_rhs().expr());

      if(ctx.type().base_type().CHAR()!= null || ctx.type().base_type().BOOL()!=null){
        machine.add(new StoreByteInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      }else{
        machine.add(new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      }
      
      registers.free(reg);

      machine.add(
          new AddInstruction(Registers.sp,Registers.sp, new Operand2Int('#', reserveByte)));
    }


    return null;
  }
  
  @Override
  public Register visitExpr(ExprContext ctx) {
    if (ctx.int_liter() != null) {
      Register reg = registers.getRegister();
      int number = Integer.parseInt(ctx.int_liter().getText());
      machine.add(new LoadInstruction(reg, new Operand2Int('=', number)));
      return reg;
    } else if (ctx.bool_liter() != null) {
      Register reg = registers.getRegister();
      int value = ctx.bool_liter().getText().equals("true") ? 1 : 0;
      machine.add(new MovInstruction(reg, new Operand2Int('#', value)));
      return reg;
    } else if (ctx.array_elem() != null) {
      return visit(ctx.array_elem());
    } else if (ctx.binary_oper_and_or() != null) {
      Register reg1 = visit(ctx.getChild(0));
      Register reg2 = visit(ctx.getChild(2));
      int op = ((TerminalNode) ctx.getChild(1).getChild(0)).getSymbol().getType();
      switch (op) {
        case WaccParser.AND:
          machine.add(new AndInstruction(reg1, reg1, new Operand2Reg(reg2)));
          break;
        case WaccParser.OR:
          machine.add(new OrInstruction(reg1,reg1, new Operand2Reg(reg2)));
          break;
        default:
          break;
          
      }
      registers.free(reg2);
      return reg1;
    } else if (ctx.binary_oper_eql() != null) {
      Register reg1 = visit(ctx.getChild(0));
      Register reg2 = visit(ctx.getChild(2));
//      int op = 34;
      int op = ((TerminalNode) ctx.getChild(1).getChild(0)).getSymbol().getType();
//      System.out.printf((ctx.getChild(1)).getChild(0).getText());
      switch (op) {
        case WaccParser.EQL:
          machine.add(new CompareInstruction(reg1,new Operand2Reg(reg2)));
          machine.add(new MovEqualInstruction(reg1,new Operand2Int('#',1)));
          machine.add(new MovNotEqualInstruction(reg1,new Operand2Int('#',0)));
          break;
        case WaccParser.NEQL:
          machine.add(new CompareInstruction(reg1,new Operand2Reg(reg2)));
          machine.add(new MovEqualInstruction(reg1,new Operand2Int('#',0)));
          machine.add(new MovNotEqualInstruction(reg1,new Operand2Int('#',1)));
          break;
        case WaccParser.LET:
          machine.add(new CompareInstruction(reg1,new Operand2Reg(reg2)));
          machine.add(new MovGreaterThanInstruction(reg1,new Operand2Int('#',0)));
          machine.add(new MovLessEqualInstruction(reg1,new Operand2Int('#',1)));
          break;
        case WaccParser.LT:
          machine.add(new CompareInstruction(reg1,new Operand2Reg(reg2)));
          machine.add(new MovGreaterEqualInstruction(reg1,new Operand2Int('#',0)));
          machine.add(new MovLessThanInstruction(reg1,new Operand2Int('#',1)));
          break;
        case WaccParser.GET:
          machine.add(new CompareInstruction(reg1,new Operand2Reg(reg2)));
          machine.add(new MovGreaterEqualInstruction(reg1,new Operand2Int('#',1)));
          machine.add(new MovLessThanInstruction(reg1,new Operand2Int('#',0)));
          break;
        case WaccParser.GT:
          machine.add(new CompareInstruction(reg1,new Operand2Reg(reg2)));
          machine.add(new MovGreaterThanInstruction(reg1,new Operand2Int('#',1)));
          machine.add(new MovLessEqualInstruction(reg1,new Operand2Int('#',0)));
          break;
        default:
          break;
      }
      registers.free(reg2);
      return reg1;
    } else if (ctx.binary_oper_plus() != null) {
      Register reg1 = visit(ctx.getChild(0));
      Register reg2 = visit(ctx.getChild(2));
//      int op = 34;
      int op = ((TerminalNode) ctx.getChild(1).getChild(0)).getSymbol().getType();
//      System.out.printf((ctx.getChild(1)).getChild(0).getText());
      switch (op) {
        case WaccParser.PLUS:
          machine.add(new AddInstruction(reg1,reg1,new Operand2Reg(reg2),true));
          break;
        case WaccParser.MINUS:
          machine.add(new SubInstruction(reg1,reg1,new Operand2Reg(reg2),true));
          break;
        default:
          break;
      }
      registers.free(reg2);
      return reg1;
    } else if (ctx.binary_oper_mul() != null) {
      Register reg1 = visit(ctx.getChild(0));
      Register reg2 = visit(ctx.getChild(2));
//      int op = 34;
      int op = ((TerminalNode) ctx.getChild(1).getChild(0)).getSymbol().getType();
//      System.out.printf((ctx.getChild(1)).getChild(0).getText());
      if (op == WaccParser.MUL) {
        machine.add(new SMulInstruction(reg1,reg2));
      }else if(op == WaccParser.DIV){
        Register rreg1= registers.getReturnRegister();
        Register rreg2= registers.getReturnRegister();
        machine.add(new MovInstruction(rreg1,new Operand2Reg(reg1)));
        machine.add(new MovInstruction(rreg2,new Operand2Reg(reg2)));
        machine.add(new BranchLinkInstruction("__aeabi_idiv"));
        machine.add(new MovInstruction(reg1,new Operand2Reg(rreg1)));
        registers.free(rreg1);
        registers.free(rreg2);
      }else if(op == WaccParser.MOD){
        Register rreg1= registers.getReturnRegister();
        Register rreg2= registers.getReturnRegister();
        machine.add(new MovInstruction(rreg1,new Operand2Reg(reg1)));
        machine.add(new MovInstruction(rreg2,new Operand2Reg(reg2)));
        machine.add(new BranchLinkInstruction("__aeabi_idivmod"));
        machine.add(new MovInstruction(reg1,new Operand2Reg(rreg2)));
        registers.free(rreg1);
        registers.free(rreg2);
      }
      registers.free(reg2);
      return reg1;
    } else if (ctx.pair_liter() != null) {
      return null;
    } else if (ctx.unary_oper() != null) {
      Register reg1 = registers.getRegister();
      int op = ((TerminalNode) ctx.getChild(1).getChild(0)).getSymbol().getType();
      switch (op) {
        case WaccParser.NOT:
          machine.add(new XorInstruction(reg1,reg1,new Operand2Int('#',1)));
          break;
        case WaccParser.MINUS:
          machine.add(new SubInstruction(reg1,reg1,new Operand2Int('#',1),true,true));
          break;
        case WaccParser.LEN:
          machine.add(new LoadInstruction(reg1,new Operand2Reg(reg1,true)));
          break;
        case WaccParser.ORD:
          machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
          break;
        case WaccParser.CHR:
          machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
          break;
        default:
          break;
      }
      return visit(ctx.unary_oper());
    } else if (ctx.ident() != null) {
      Register reg = registers.getRegister();
      int offset = symbolTable.getAddress(ctx.getChild(0).getText());
      System.out.printf((ctx.getChild(0)).getText());
      System.out.printf(Integer.toString(offset));
      machine.add(new LoadInstruction(reg,new Operand2Reg(Registers.sp,offset)));
      return reg;
    } else if (ctx.CHAR_LIT() != null) {
      Register reg = registers.getRegister();
      char c = ctx.CHAR_LIT().getText().charAt(0);
      String c_ = "'" + c + "'";
      machine.add(new MovInstruction(reg, new Operand2String('#', c_)));
      return reg;
    } else if (ctx.CHARACTER_LIT() != null) {
      return null;
    } else if (ctx.OPEN_PARENTHESES() != null) {
      return visit(ctx.expr(0));
    }
    return null;
  }
  
  
//  @Override
//  public Register visitBinary_oper_plus(WaccParser.Binary_oper_plusContext ctx) {
////    System.out.println("1"+ctx.getChild(0).toString());
////    Register reg1 = visit(ctx.getChild(0));
////    System.out.println("2");
////    Register reg2 = visit(ctx.getChild(0));
////    System.out.println("3");
//    Register reg1 = registers.getRegister();
//    Register reg2 = registers.getRegister();
//    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
//    switch (op) {
//      case WaccParser.PLUS:
//        machine.add(new AddInstruction(reg1,reg1,new Operand2Reg(reg2),true));
//        break;
//      case WaccParser.MINUS:
//        machine.add(new SubInstruction(reg1,reg1,new Operand2Reg(reg2),true));
//        break;
//      default:
//        break;
//    }
//    return reg1;
//  }
  
  @Override
  public Register visitUnary_oper(WaccParser.Unary_operContext ctx) {
    Register reg1 = visit(ctx.getChild(0));
    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    switch (op) {
      case WaccParser.NOT:
        machine.add(new XorInstruction(reg1,reg1,new Operand2Int('#',1)));
        break;
      case WaccParser.MINUS:
        machine.add(new SubInstruction(reg1,reg1,new Operand2Int('#',1),true,true));
        break;
      case WaccParser.LEN:
        machine.add(new LoadInstruction(reg1,new Operand2Reg(reg1,true)));
        break;
      case WaccParser.ORD:
        machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
        break;
      case WaccParser.CHR:
        machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
        break;
      default:
        break;
    }
    return reg1;
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
  
}
