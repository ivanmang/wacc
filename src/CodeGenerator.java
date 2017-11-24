import CodeGeneration.*;
import Instructions.*;
import Instructions.Branch.BranchEqualInstruction;
import Instructions.Branch.BranchInstruction;
import Instructions.Branch.BranchLinkEqualInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.Branch.BranchLinkVSInstruction;
import Instructions.CmpInstruction;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.LtorgLabel;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadByteInstruction;
import Instructions.Load.LoadEqualInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Move.*;
import Instructions.Operand2.Operand2;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.Operand2.Operand2String;
import Instructions.Load.LoadNotEqualInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.*;
import Instructions.PopInstruction;
import Instructions.PushInstruction;
import Instructions.Store.StoreByteInstruction;
import Instructions.Store.StoreInstruction;
import antlr.WaccParser;
import Instructions.StringInstruction;
import Instructions.SubInstruction;
import antlr.WaccParser.Array_literContext;
import antlr.WaccParser.AssignStatContext;
import antlr.WaccParser.Assign_rhsContext;
import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.New_pairContext;
import antlr.WaccParser.Pair_elemContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.ReturnStatContext;
import antlr.WaccParser.SkipStatContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;
import Utils.*;
import java.util.Map;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CodeGenerator extends WaccParserBaseVisitor<Register> {

  private CodeStringBuilder builder = new CodeStringBuilder("");
  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();
  private int labelnumber = 0;
  private SymbolTable symbolTable;
  private Map<String, Function> functionList;

  public static final int MAX_STACK_SIZE = 1024;

  public CodeGenerator(SymbolTable symbolTable, Map<String, Function> functionList) {
    this.symbolTable = symbolTable;
    this.functionList = functionList;
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
  public Register visitFunc(WaccParser.FuncContext ctx) {
    machine.addFunctionStart("f_"+ctx.getChild(1).getText());
    machine.add(new PushInstruction(Registers.lr));
    visit(ctx.getChild(5));
    machine.add(new PopInstruction(Registers.pc));
    machine.add(new PopInstruction(Registers.pc));
    machine.add(new LtorgLabel());
    machine.addFunctionEnd();
    return null;
  }

  @Override
  public Register visitReturnStat(WaccParser.ReturnStatContext ctx) {
    Register reg = visit(ctx.getChild(1));
    //Register reg = Registers.r4;
    Register rreg = registers.getReturnRegister();
    machine.add(new MovInstruction(rreg, new Operand2Reg(reg)));
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

      Register reg = visit(ctx.assign_rhs());

      if (ctx.type().base_type().STRING() != null) { //string
        String string = ctx.assign_rhs().getText();
        int msg_num = machine.addMsg(string);
        machine.add(new LoadInstruction(reg,
            new Operand2String('=', "msg_" + msg_num)));
      }

      if (ctx.type().base_type().CHAR() != null || ctx.type().base_type().BOOL() != null) {
        machine.add(new StoreByteInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      } else {
        machine.add(new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      }

      registers.free(reg);

    }

    return null;
  }

  @Override
  public Register visitAssign_rhs(Assign_rhsContext ctx) {
    if (ctx.expr() != null) {
      return visit(ctx.expr());
    } else if (ctx.function_call() != null) {
      //TODO: implemnt function call for visit assign
      String function_name = ctx.function_call().ident().getText();
      machine.add(new BranchLinkInstruction("f_" + function_name));
      Register register = registers.getRegister();
      machine.add(new MovInstruction(register, registers.getReturnRegister()));
      return register;
    } else if (ctx.array_liter() != null) {
      return visit(ctx.new_pair());
    } else if (ctx.new_pair() != null) {
      return visit(ctx.new_pair());
    } else if (ctx.pair_elem() != null) {
      Register addressRegister = visit(ctx.pair_elem());
      //TODO: check whether expression is a bool or character to determine whether it is LDR or LDRSB
      //Get content from address
      machine.add(new LoadInstruction(addressRegister, new Operand2Reg(addressRegister, true)));
      return addressRegister;
    }
    return null;
  }

  @Override
  public Register visitPair_elem(Pair_elemContext ctx) {
    //Store the result of the expression to the register
    Register exprRegister = visit(ctx.expr());

    machine.add(new MovInstruction(registers.r0, new Operand2Reg(exprRegister)));
    //TODO check for null pointer

    //TODO: check whether expression is a bool or character to determine whether it is LDR or LDRSB
    if(ctx.FST() != null) {
      machine.add(new LoadInstruction(exprRegister, new Operand2Reg(exprRegister)));
    } else if(ctx.SND() != null) {
      machine.add(new LoadInstruction(exprRegister, new Operand2Reg(exprRegister, 4)));
    }

    return exprRegister;
  }

  @Override
  public Register visitArray_liter(Array_literContext ctx) {
    //Getting the size of the array literal: e.g. [0,0,0] has a size of 3
    int size = ctx.expr().size();

    //Getting the size of the type of the elements in the array: integer size -> 4
    //TODO: different size for different types
    int typeSize = 4;

    //Load the size of the array to r0 and call malloc to allocate memory on the heap for the array
    machine.add(new LoadInstruction(registers.r0, new Operand2Int('=', typeSize * size + 4)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Get the first available register to store the address of the array (address of the first elemnt)
    Register addressRegister = registers.getRegister();
    machine.add(new MovInstruction(addressRegister, registers.r0));

    int pos = 1;
    //For each element in the array literal, load the expression to the register and store it to the corresponding address in the heap
    for (ExprContext exprContext : ctx.expr()) {
      Register exprRegister = visit(exprContext);
      //TODO: use storeByte for char and bool
      machine.add(
          new StoreInstruction(exprRegister, new Operand2Reg(addressRegister, pos * typeSize)));
      registers.free(exprRegister);
    }
    //Put the size of the array literal to the first element (first address) of the array
    Register sizeRegister = registers.getRegister();
    machine.add(new LoadInstruction(sizeRegister, new Operand2Int('=', size)));
    machine.add(new StoreInstruction(sizeRegister, new Operand2Reg(addressRegister, true)));

    //return the address register to represent the address of the array literal
    return addressRegister;
  }

  @Override
  public Register visitNew_pair(New_pairContext ctx) {
    //Allocate 8 bytes of memory on the heap for the pair, 4 for both the first address and the second address
    machine.add(new LoadInstruction(registers.r0, new Operand2Int('=', 8)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Get the register for storing the address of the addresses
    Register addressRegister = registers.getRegister();
    machine.add(new MovInstruction(addressRegister, registers.r0));

    //First expression
    //Get the result of the first expression to a register
    Register fstRegister = visit(ctx.expr(0));
    int fstTypeSize = 4; //TODO: get the size of the expression base on the type of the expression
    //Allocate memory for the first element
    machine.add(new LoadInstruction(registers.r0, new Operand2Int('=', fstTypeSize)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Store the expression in the address allocated
    machine.add(new StoreInstruction(fstRegister, new Operand2Reg(registers.r0, true)));
    machine.add(new StoreInstruction(registers.r0, new Operand2Reg(addressRegister, true)));

    //Second expression
    //Get the result of the second expression to a register
    Register sndRegister = visit(ctx.expr(1));
    int sndTypeSize = 4; //TODO: get the size of the expression base on the type of the expression
    //Allocate memory for the second element
    machine.add(new LoadInstruction(registers.r0, new Operand2Int('=', sndTypeSize)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Store the expression in the address allocated
    machine.add(new StoreInstruction(sndRegister, new Operand2Reg(registers.r0, true)));
    machine.add(new StoreInstruction(registers.r0, new Operand2Reg(addressRegister, 4)));

    return addressRegister;
  }

  @Override
  public Register visitBeginStat(BeginStatContext ctx) {
    int address = 0;

    symbolTable = symbolTable.enterScope(symbolTable);
    //get the symbol table with it's address and type
    Map<String, SymbolInfo> dict = symbolTable.getDictionary();
    //iterate all variables and assign a address to it
    for (String name : dict.keySet()) {
      dict.get(name).setAddress(address);
      address += dict.get(name).getType().getSize();
    }
    //get the size of the variable store
    int reserveByte = symbolTable.getSize();

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(new SubInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    machine.add(new SubInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));
    reserveByte = symbolTable.getSize();

    visit(ctx.stat());

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    //Pop the variables
    machine.add(new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));

    symbolTable = symbolTable.exitScope(symbolTable);
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
      char c = ctx.CHAR_LIT().getText().charAt(1);
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
    machine.add(new CmpInstruction(lastRegister, new Operand2Int('#', 0)));
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
    machine.add(new CmpInstruction(lastRegister, new Operand2Int('#', 1)));
    machine.add(new BranchEqualInstruction(loopLabel.toString()));
    return null;
  }

  public int CheckArrayIndexNegErrorMsg() {
    machine.add(new BranchLinkInstruction("p_check_array_bounds"));
    return machine.addMsg("ArrayIndexOutOfBoundsError: negative index\n\0");
  }

  public int CheckArrayIndexTooLargeErrorMsg() {
    machine.add(new BranchLinkInstruction("p_check_array_bounds"));
    return machine.addMsg("ArrayIndexOutOfBoundsError: index too large\n\0");
  }

  public int CheckDividedByZeroMsg() {
    machine.add(new BranchLinkInstruction("p_check_divide_by_zero"));
    return machine.addMsg("DivideByZeroError: divide or modulo by zero\n\0");
  }

  public int CheckOverFlowErrorMsg() {
    machine.add(new BranchLinkVSInstruction("p_throw_overflow_error"));
    return machine
        .addMsg("OverflowError: the result is too small/large to store in a 4-byte signed-integer");
  }

  public int CheckNullReferenceMsg() {
    machine.add(new BranchLinkInstruction("p_check_null_pointer"));
    return machine.addMsg("NullReferenceError: dereference a null reference\\n\\0");
  }

  public void pairThrowRunTimeError(){
    machine.add(new BranchLinkEqualInstruction("p_throw_runtime_error"));

  }

  @Override
  public Register visitSkipStat(SkipStatContext ctx) {
    return null;
  }

  @Override
  public Register visitPrintStat(PrintStatContext ctx) {
//  if the number behind print is int then use Operand2Int
//    int num = ctx.getChild.get(1);
//  if the number behind print is char then use Operand2Char
//    char chr = ctx.getChild.get(1);
//  if the number behind print is string then use Operand2String
//    String str = ctx.getChild.get(1);
    int num = 0;
    char chr = 'N';
    String str = "NOT YET IMPLEMENTED";
// TODO: integrate expression into the implementation of print
    int msg_num = machine.addMsg(str);
    Register current = registers.getRegister();
    machine.add(new LoadInstruction(current, new Operand2String('=', "msg_" + msg_num)));
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(current)));
    registers.free(current);

//    if number behind print is int
    machine.add(new BranchLinkInstruction("p_print_int"));
//    if behind print is char
    Register current1 = registers.getRegister();
    machine.add(new MovInstruction(current, new Operand2Char('#', chr)));
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(current1)));
    registers.free(current1);
    machine.add(new BranchLinkInstruction("putchar"));

//    if number behind print is string
    machine.add(new BranchLinkInstruction("p_print_string"));

//    if (ctx.getChild(1) is of the type int) {
    machine.addPrintIntFunction(str);
//    } else if (ctx.getChild(1) is of the type string) {
    machine.addPrintStringFunction(str);
//    } else if (ctx.getChild(1) is of the type bool) {
    machine.addPrintBoolFunction();
//    }

        return null;
  }

  @Override
  public Register visitPrintlnStat(PrintlnStatContext ctx) {
    //  if the number behind print is int then use Operand2Int
//    String str = ctx.getChild.get(0);
    String str= "NOT YET IMPLEMENTED";
    int msg_num = machine.addMsg(str);
    Register current = registers.getRegister();
    machine.add(new LoadInstruction(current, new Operand2String('=', "msg_" + msg_num)));
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(current)));
    registers.free(current);

//    if number behind print is int
    machine.add(new BranchLinkInstruction("p_print_int"));
//    if number behind print is string
    machine.add(new BranchLinkInstruction("p_print_string"));
    machine.add(new BranchLinkInstruction("p_print_ln"));

//    if (ctx.getChild(1) is of the type int) {
    machine.addPrintIntFunction(str);
//    } else if (ctx.getChild(1) is of the type string) {
    machine.addPrintStringFunction(str);
//    } else if (ctx.getChild(1) is of the type bool) {
    machine.addPrintBoolFunction();
//    }


    machine.addPrintlnFunction();
    return null;
  }

  @Override
  public Register visitReadStat(ReadStatContext ctx) {
//    if (ctx.getChild(1) is an int type) {
    machine.addReadIntFunction();
//    }
//    else if (ctx.getChild(1) is a char type){
    machine.addReadCharFunction();
//    }
    return null;
  }

}


