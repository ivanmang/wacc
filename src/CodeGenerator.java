import CodeGeneration.*;
import Instructions.*;
import Instructions.Branch.BranchEqualInstruction;
import Instructions.Branch.BranchInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.CmpInstruction;
import Instructions.Labels.Label;
import Instructions.Labels.LtorgLabel;
import Instructions.Load.LoadByteInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Move.*;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.Operand2.Operand2String;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.*;
import Instructions.PopInstruction;
import Instructions.PushInstruction;
import Instructions.Store.StoreByteInstruction;
import Instructions.Store.StoreInstruction;
import antlr.WaccParser;
import Instructions.SubInstruction;
import antlr.WaccParser.Array_elemContext;
import antlr.WaccParser.Array_literContext;
import antlr.WaccParser.AssignStatContext;
import antlr.WaccParser.Assign_rhsContext;
import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.DoWhileStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.ForStatContext;
import antlr.WaccParser.FreeStatContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.InitAssignStatContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.New_pairContext;
import antlr.WaccParser.Pair_elemContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.SideEffectStatContext;
import antlr.WaccParser.Side_effectContext;
import antlr.WaccParser.SkipStatContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;
import Utils.*;

import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CodeGenerator extends WaccParserBaseVisitor<Register> {

  private CodeStringBuilder builder = new CodeStringBuilder("");

  public ARM11Machine getMachine() {
    return machine;
  }

  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();
  private int labelnumber = 0;
  private SymbolNode symbolNode;
  private Map<String, Function> functionList;
  private GetTypeFromExpr exprTypeGetter = new GetTypeFromExpr();
//  private String previousFunction;
//  private String currentFunction = "main";

  private Type intType = new BaseType(WaccParser.INT);
  private Type charType = new BaseType(WaccParser.CHAR);
  private Type boolType = new BaseType(WaccParser.BOOL);
  private Type stringType = new BaseType(WaccParser.STRING);

  public static final int MAX_STACK_SIZE = 1024;

  public CodeGenerator(SymbolNode symbolNode, Map<String, Function> functionList) {
    this.symbolNode = symbolNode;
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

    int address = 0;
    //get the symbol table with it's address and type
    Map<String, SymbolInfo> dict = symbolNode.getDictionary();
    //iterate all variables and assign a address to it
    for (String name : dict.keySet()) {
      dict.get(name).setAddress(address);
      address += dict.get(name).getType().getSize();
    }

    //get the size of the variable store
    int reserveByte = symbolNode.getSize();

    //if size exceed max stack size reserve, Push max_size first
    if(reserveByte != 0) {
      while (reserveByte > MAX_STACK_SIZE) {
        machine.add(
            new SubInstruction(Registers.sp, Registers.sp,
                new Operand2Int('#', MAX_STACK_SIZE)));
        reserveByte -= MAX_STACK_SIZE;
      }
      machine.add(new SubInstruction(Registers.sp, Registers.sp,
          new Operand2Int('#', reserveByte)));
      reserveByte = symbolNode.getSize();
    }

    visitChildren(ctx);

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(
          new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    //Pop the variables
    machine.add(new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));

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
//    previousFunction = currentFunction;
//    currentFunction = ctx.getChild(1).getText();
    SymbolNode main = symbolNode;
    symbolNode = functionList.get(ctx.getChild(1).getText()).getSymbolNode();

    int address = 4;
    int size = 0;

    //get the symbol table with it's address and type
    Map<String, SymbolInfo> dict = symbolNode.getDictionary();
    //iterate all variables and assign a address to it
    for (String name : dict.keySet()) {
      dict.get(name).setAddress(address);
      address += dict.get(name).getType().getSize();
      if (!functionList.get(ctx.getChild(1).getText()).getIdentList().contains(name)) {
        size += dict.get(name).getType().getSize();
      }
    }

    //get the size of the variable store
    int reserveByte = size;

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(
          new SubInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    machine.addFunctionStart("f_" + ctx.getChild(1).getText());
    machine.add(new PushInstruction(Registers.lr));
    if (reserveByte != 0) {
      machine
          .add(new SubInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));
    }
    reserveByte = symbolNode.getSize();

    visit(ctx.stat());

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(
          new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    //Pop the variables
    if (reserveByte != 0) {
      machine
          .add(new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));
    }

    machine.add(new PopInstruction(Registers.pc));
    machine.add(new PopInstruction(Registers.pc));
    machine.add(new LtorgLabel());
    machine.addFunctionEnd();
//    currentFunction = previousFunction;
//    previousFunction = null;
    symbolNode = main;
    return null;
  }

  @Override
  public Register visitParam_list(WaccParser.Param_listContext ctx) {
    int address = 4;
    System.out.println(ctx.getChild(0).getChild(1).getText());
    System.out.println(ctx.getChildCount());
    for (int i = 0; i <= (ctx.getChildCount()); i = i + 2) {
//      functionList.get(currentFunction).setAddress(ctx.getChild(i).getChild(1).getText(),address);
      if (ctx.getChild(i).getChild(0).getText().equals("char") || ctx.getChild(i).getChild(0)
          .getText().equals("bool")) {
        address += 1;
      } else {
        address += 4;
      }
    }
    return null;
  }

  @Override
  public Register visitReturnStat(WaccParser.ReturnStatContext ctx) {
    Register reg = visit(ctx.expr());
    //Register reg = Registers.r4;
    Register rreg = registers.getReturnRegister();
    machine.add(new MovInstruction(rreg, new Operand2Reg(reg)));
    registers.free(reg);
    return null;
  }


  @Override
  public Register visitExitStat(ExitStatContext ctx) {
    Register returnReg = visit(ctx.expr());

    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(returnReg)));
    machine.add(new BranchLinkInstruction("exit"));

    registers.free(returnReg);
    return null;
  }


  @Override
  public Register visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    if (ctx.type().base_type() != null) { //base type

      Register reg = visit(ctx.assign_rhs());

      if (ctx.type().base_type().STRING() != null) { //string
        machine.add((new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ctx.ident().getText())))));
      } else if (ctx.type().base_type().CHAR() != null || ctx.type().base_type().BOOL() != null) {
        machine.add(new StoreByteInstruction(reg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ctx.ident().getText()))));
      } else {
        machine.add(new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ctx.ident().getText()))));
      }
      registers.free(reg);
    } else {
      Register reg = visit(ctx.assign_rhs());
      machine.add(new StoreInstruction(reg,
          new Operand2Reg(Registers.sp, symbolNode.getAddress(ctx.ident().getText()))));
      registers.free(reg);
    }

    return null;
  }

  @Override
  public Register visitAssignStat(AssignStatContext ctx) {
    Register srcReg = visit(ctx.assign_rhs());
    if (ctx.assign_lhs().ident() != null) {
      String ident = ctx.assign_lhs().ident().getText();
//      if (currentFunction.equals("main")) {
//        machine.add(new StoreInstruction(srcReg, new Operand2Reg(Registers.sp, symbolNode.getAddress(ident))));
//      }
//      else {
//        machine.add(new StoreInstruction(srcReg, new Operand2Reg(Registers.sp, functionList.get(currentFunction).getAddress(ident))));
//      }
      Type type = symbolNode.lookupAll(ident);
      if (type.equals(boolType) || type.equals(charType)) {
        machine.add(new StoreByteInstruction(srcReg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ident))));
      } else {
        machine.add(new StoreInstruction(srcReg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ident))));
      }
    } else if (ctx.assign_lhs().array_elem() != null) {
      Register destReg = visit(ctx.assign_lhs().array_elem());
      Type type = exprTypeGetter.visitArray_elem(ctx.assign_lhs().array_elem(),
          symbolNode);
      machine.removeLastInstruciton();
      if (type.equals(boolType) || type.equals(charType)) {
        machine.add(new StoreByteInstruction(srcReg, new Operand2Reg(destReg, true)));
      } else {
        machine.add(new StoreInstruction(srcReg, new Operand2Reg(destReg, true)));
      }
      registers.free(destReg);
    } else if (ctx.assign_lhs().pair_elem() != null) {
      Register destReg = visit(ctx.assign_lhs().pair_elem());
      Type type = exprTypeGetter.visitPair_elem(ctx.assign_lhs().pair_elem());
      if (type.equals(boolType) || type.equals(charType)) {
        machine.add(new StoreByteInstruction(srcReg, new Operand2Reg(destReg, true)));
      } else {
        machine.add(new StoreInstruction(srcReg, new Operand2Reg(destReg, true)));
      }
      registers.free(destReg);
    }
    registers.free(srcReg);
    return null;
  }

  @Override
  public Register visitAssign_rhs(Assign_rhsContext ctx) {
    if (ctx.expr() != null) {
      return visit(ctx.expr());
    } else if (ctx.function_call() != null) {
      String function_name = ctx.function_call().ident().getText();
      SymbolNode main = symbolNode;
      symbolNode = functionList.get(function_name).getSymbolNode();
      if (ctx.function_call().arg_list() != null) {
        visit(ctx.function_call().arg_list());
      }
      machine.add(new BranchLinkInstruction("f_" + function_name));
      Register register = registers.getRegister();
      machine.add(new MovInstruction(register, Registers.r0));
      symbolNode = main;
      return register;
    } else if (ctx.array_liter() != null) {
      return visit(ctx.array_liter());
    } else if (ctx.new_pair() != null) {
      return visit(ctx.new_pair());
    } else if (ctx.pair_elem() != null) {
      Register addressRegister = visit(ctx.pair_elem());
      if (exprTypeIsCharOrBool(ctx.pair_elem().expr())) {
        //Get content from address
        machine
            .add(new LoadByteInstruction(addressRegister, new Operand2Reg(addressRegister, true)));
      } else {
        machine.add(new LoadInstruction(addressRegister, new Operand2Reg(addressRegister, true)));
      }
      return addressRegister;
    }
    return null;
  }

  @Override
  public Register visitSide_effect(Side_effectContext ctx) {
    String identName = ctx.ident().getText();
    int offset = symbolNode.getAddress(identName);

    if (ctx.INC() != null || ctx.DEC() != null) {
      Register register = registers.getRegister();

      machine.add(new LoadInstruction(register, new Operand2Reg(Registers.sp, offset)));
      if (ctx.getChild(0).getChild(0) != null) {
        Register register1 = registers.getRegister();
        if (ctx.INC() != null) {
          machine.add(new AddInstruction(register1, register, new Operand2Int('#', 1)));
        } else {
          machine.add(new SubInstruction(register1, register, new Operand2Int('#', 1)));
        }
        machine.add(new StoreInstruction(register1, new Operand2Reg(Registers.sp, offset)));
        registers.free(register1);
      } else {
        if (ctx.INC() != null) {
          machine.add(new AddInstruction(register, register, new Operand2Int('#', 1)));
        } else {
          machine.add(new SubInstruction(register, register, new Operand2Int('#', 1)));
        }
        machine.add(new StoreInstruction(register, new Operand2Reg(Registers.sp, offset)));
      }

      return register;
    } else if (ctx.INCNUM() != null || ctx.DECNUM() != null) {
      Register register = registers.getRegister();
      machine.add(new LoadInstruction(register, new Operand2Reg(Registers.sp, offset)));
      Register exprReg = visit(ctx.expr());
      if (ctx.INCNUM() != null) {
        machine.add(new AddInstruction(register, register, new Operand2Reg(exprReg, false)));
      } else {
        machine.add(new SubInstruction(register, register, new Operand2Reg(exprReg, false)));
      }
      machine.add(new StoreInstruction(register, new Operand2Reg(Registers.sp, offset)));
      registers.free(exprReg);
      return register;
    } else if (ctx.EQUAL() != null) {
      Register exprReg = visit(ctx.expr());
      machine.add(new StoreInstruction(exprReg, new Operand2Reg(Registers.sp, offset)));
      return exprReg;
    }
    return null;
  }

  @Override
  public Register visitArg_list(WaccParser.Arg_listContext ctx) {
    Map<String, SymbolInfo> dict = symbolNode.getDictionary();
    for (int i = 0; i <= (ctx.getChildCount()); i = i + 2) {
//      functionList.get(currentFunction).setAddress(ctx.getChild(i).getChild(1).getText(),address);
      System.out.println(ctx.getChild(i).getChild(0).getText());
      if (exprTypeIsCharOrBool((WaccParser.ExprContext) ctx.getChild(i))) {
        Register reg = visit(ctx.getChild(i));
        machine.add(new StoreByteInstruction(reg, new Operand2Reg(Registers.sp, -1), true));
        registers.free(reg);
      } else {
        Register reg = visit(ctx.getChild(i));
        machine.add(new StoreInstruction(reg, new Operand2Reg(Registers.sp, -4), true));
        registers.free(reg);
      }
    }
    return null;
  }


  @Override
  public Register visitPair_elem(Pair_elemContext ctx) {
    //Store the result of the expression to the register
    Register exprRegister = visit(ctx.expr());

    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(exprRegister)));
    machine.addcheckNullPointerInstruction();

    if (exprTypeIsCharOrBool(ctx.expr())) {
      if (ctx.FST() != null) {
        machine.add(new LoadByteInstruction(exprRegister, new Operand2Reg(exprRegister, true)));
      } else if (ctx.SND() != null) {
        machine.add(new LoadByteInstruction(exprRegister, new Operand2Reg(exprRegister, 4)));
      }
    } else {
      if (ctx.FST() != null) {
        machine.add(new LoadInstruction(exprRegister, new Operand2Reg(exprRegister, true)));
      } else if (ctx.SND() != null) {
        machine.add(new LoadInstruction(exprRegister, new Operand2Reg(exprRegister, 4)));
      }
    }

    return exprRegister;
  }

  @Override
  public Register visitArray_liter(Array_literContext ctx) {
    //Getting the size of the array literal: e.g. [0,0,0] has a size of 3
    int size = 0;
    if (ctx.expr() != null) {
      size = ctx.expr().size();
    }
    System.out.println("Size = " + size);
    Register addressRegister = registers.getRegister();
    if (size > 0) {
      //Getting the size of the type of the elements in the array: integer size -> 4
      int typeSize = getSizeFromExpr(ctx.expr(0));

      //Load the size of the array to r0 and call malloc to allocate memory on the heap for the array
      machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', typeSize * size + 4)));
      machine.add(new BranchLinkInstruction("malloc"));

      //Get the first available register to store the address of the array (address of the first elemnt)
      machine.add(new MovInstruction(addressRegister, Registers.r0));

      int pos = 0;
      boolean isCharOrBool = exprTypeIsCharOrBool(ctx.expr(0));

      //For each element in the array literal, load the expression to the register and store it to the corresponding address in the heap
      for (ExprContext exprContext : ctx.expr()) {
        Register exprRegister = visit(exprContext);
        if (isCharOrBool) {
          machine.add(
              new StoreByteInstruction(exprRegister,
                  new Operand2Reg(addressRegister, pos * typeSize + 4)));
        } else {
          machine.add(
              new StoreInstruction(exprRegister, new Operand2Reg(addressRegister, pos * typeSize + 4)));
        }
        pos++;
        registers.free(exprRegister);
      }
    } else if (size == 0) {
      //Load the size of the array to r0 and call malloc to allocate memory on the heap for the array
      machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', 4)));
      machine.add(new BranchLinkInstruction("malloc"));

      //Get the first available register to store the address of the array (address of the first elemnt)
      machine.add(new MovInstruction(addressRegister, Registers.r0));
    }
    //Put the size of the array literal to the first element (first address) of the array
    Register sizeRegister = registers.getRegister();
    machine.add(new LoadInstruction(sizeRegister, new Operand2Int('=', size)));
    machine.add(new StoreInstruction(sizeRegister, new Operand2Reg(addressRegister, true)));
    registers.free(sizeRegister);

    //return the address register to represent the address of the array literal
    return addressRegister;
  }

  @Override
  public Register visitNew_pair(New_pairContext ctx) {
    //Allocate 8 bytes of memory on the heap for the pair, 4 for both the first address and the second address
    machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', 8)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Get the register for storing the address of the addresses
    Register addressRegister = registers.getRegister();
    machine.add(new MovInstruction(addressRegister, Registers.r0));

    //First expression
    //Get the result of the first expression to a register
    Register fstRegister = visit(ctx.expr(0));
    int fstTypeSize = getSizeFromExpr(ctx.expr(0));
    //Allocate memory for the first element
    machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', fstTypeSize)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Store the expression in the address allocated
    machine.add(new StoreInstruction(fstRegister, new Operand2Reg(Registers.r0, true)));
    machine.add(new StoreInstruction(Registers.r0, new Operand2Reg(addressRegister, true)));
    registers.free(fstRegister);

    //Second expression
    //Get the result of the second expression to a register
    Register sndRegister = visit(ctx.expr(1));
    int sndTypeSize = getSizeFromExpr(ctx.expr(1));
    //Allocate memory for the second element
    machine.add(new LoadInstruction(Registers.r0, new Operand2Int('=', sndTypeSize)));
    machine.add(new BranchLinkInstruction("malloc"));

    //Store the expression in the address allocated
    machine.add(new StoreInstruction(sndRegister, new Operand2Reg(Registers.r0, true)));
    machine.add(new StoreInstruction(Registers.r0, new Operand2Reg(addressRegister, 4)));
    registers.free(sndRegister);

    return addressRegister;
  }

  @Override
  public Register visitBeginStat(BeginStatContext ctx) {
    int address = 0;
//    System.out.println("Printing SymbolTable before entering scope");
//    symbolNode.printTable();
    symbolNode = symbolNode.enterScopeCodeGen(symbolNode);
//    System.out.println("Printing SymbolTable after entering scope");
//    symbolNode.printTable();
//    System.out.println("Printing finished");

    //get the symbol table with it's address and type
    Map<String, SymbolInfo> dict = symbolNode.getDictionary();
    //iterate all variables and assign a address to it
    for (String name : dict.keySet()) {
      dict.get(name).setAddress(address);
      address += dict.get(name).getType().getSize();
    }

    //get the size of the variable store
    int reserveByte = symbolNode.getSize();

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(
          new SubInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    machine.add(new SubInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));
    reserveByte = symbolNode.getSize();

    visit(ctx.stat());

    //if size exceed max stack size reserve, Push max_size first
    while (reserveByte > MAX_STACK_SIZE) {
      machine.add(
          new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    //Pop the variables
    machine.add(new AddInstruction(Registers.sp, Registers.sp, new Operand2Int('#', reserveByte)));

    symbolNode = symbolNode.exitScope();
    return null;
  }

  @Override
  public Register visitArray_elem(Array_elemContext ctx) {
    Register reg1 = registers.getRegister();
    Type type = symbolNode.lookupAll(ctx.ident().getText());
    Type elementType = null;
    if (type instanceof BaseType) {
      if (type.equals(stringType)) {
        elementType = charType;
      }
    } else if (type instanceof ArrayType) {
      ArrayType arrayType = (ArrayType) type;
      elementType = arrayType.getElementType();
    }
    int offset = symbolNode.lookupAllSymbol(ctx.ident().getText()).getAddress();
    machine.add(new AddInstruction(reg1, Registers.sp, new Operand2Int('#', offset)));
    Register reg2 = visit(ctx.getChild(2));
    machine.add(new LoadInstruction(reg1, new Operand2Reg(reg1, true)));
    Register rreg1 = registers.getReturnRegister();
    Register rreg2 = registers.getReturnRegister();
    machine.add(new MovInstruction(rreg1, new Operand2Reg(reg2)));
    machine.add(new MovInstruction(rreg2, new Operand2Reg(reg1)));
    machine.addCheckArrayBoundFunction();
    machine.add(new AddInstruction(reg1, reg1, new Operand2Int('#', 4)));
    if (elementType.equals(charType) || elementType.equals(boolType)) {
      machine.add(new AddInstruction(reg1, reg1, new Operand2Reg(reg2)));
    } else {
      machine.add(new AddInstruction(reg1, reg1, new Operand2Shift(reg2, "LSL", 2)));
    }

    machine.add(new LoadInstruction(reg1, new Operand2Reg(reg1, true)));
    registers.free(reg2);
    registers.free(rreg1);
    registers.free(rreg2);
    return reg1;
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
          machine.add(new OrInstruction(reg1, reg1, new Operand2Reg(reg2)));
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
          machine.add(new CompareInstruction(reg1, new Operand2Reg(reg2)));
          machine.add(new MovEqualInstruction(reg1, new Operand2Int('#', 1)));
          machine.add(new MovNotEqualInstruction(reg1, new Operand2Int('#', 0)));
          break;
        case WaccParser.NEQL:
          machine.add(new CompareInstruction(reg1, new Operand2Reg(reg2)));
          machine.add(new MovEqualInstruction(reg1, new Operand2Int('#', 0)));
          machine.add(new MovNotEqualInstruction(reg1, new Operand2Int('#', 1)));
          break;
        case WaccParser.LET:
          machine.add(new CompareInstruction(reg1, new Operand2Reg(reg2)));
          machine.add(new MovGreaterThanInstruction(reg1, new Operand2Int('#', 0)));
          machine.add(new MovLessEqualInstruction(reg1, new Operand2Int('#', 1)));
          break;
        case WaccParser.LT:
          machine.add(new CompareInstruction(reg1, new Operand2Reg(reg2)));
          machine.add(new MovGreaterEqualInstruction(reg1, new Operand2Int('#', 0)));
          machine.add(new MovLessThanInstruction(reg1, new Operand2Int('#', 1)));
          break;
        case WaccParser.GET:
          machine.add(new CompareInstruction(reg1, new Operand2Reg(reg2)));
          machine.add(new MovGreaterEqualInstruction(reg1, new Operand2Int('#', 1)));
          machine.add(new MovLessThanInstruction(reg1, new Operand2Int('#', 0)));
          break;
        case WaccParser.GT:
          machine.add(new CompareInstruction(reg1, new Operand2Reg(reg2)));
          machine.add(new MovGreaterThanInstruction(reg1, new Operand2Int('#', 1)));
          machine.add(new MovLessEqualInstruction(reg1, new Operand2Int('#', 0)));
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
          machine.add(new AddInstruction(reg1, reg1, new Operand2Reg(reg2), true));
          machine.addOverflowErrorFunction(false);
          break;
        case WaccParser.MINUS:
          machine.add(new SubInstruction(reg1, reg1, new Operand2Reg(reg2), true));
          machine.addOverflowErrorFunction(false);
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
        machine.add(new SMulInstruction(reg1, reg2));
        machine.add(new CompareInstruction(reg2, new Operand2Shift(reg1, "ASR", 31)));
        machine.addOverflowErrorFunction(true);
      } else if (op == WaccParser.DIV) {
        Register rreg1 = registers.getReturnRegister();
        Register rreg2 = registers.getReturnRegister();
        machine.add(new MovInstruction(rreg1, new Operand2Reg(reg1)));
        machine.add(new MovInstruction(rreg2, new Operand2Reg(reg2)));
        machine.CheckDividedByZeroFunction();
        machine.add(new BranchLinkInstruction("__aeabi_idiv"));
        machine.add(new MovInstruction(reg1, new Operand2Reg(rreg1)));
        registers.free(rreg1);
        registers.free(rreg2);
      } else if (op == WaccParser.MOD) {
        Register rreg1 = registers.getReturnRegister();
        Register rreg2 = registers.getReturnRegister();
        machine.add(new MovInstruction(rreg1, new Operand2Reg(reg1)));
        machine.add(new MovInstruction(rreg2, new Operand2Reg(reg2)));
        machine.CheckDividedByZeroFunction();
        machine.add(new BranchLinkInstruction("__aeabi_idivmod"));
        machine.add(new MovInstruction(reg1, new Operand2Reg(rreg2)));
        registers.free(rreg1);
        registers.free(rreg2);
      }
      registers.free(reg2);
      return reg1;
    } else if (ctx.pair_liter() != null) {
      Register reg = registers.getRegister();
      machine.add(new LoadInstruction(reg, new Operand2Int('=', 0)));
      return reg;
    } else if (ctx.unary_oper() != null) {
      Register reg1 = visit(ctx.getChild(1));
      int op = ((TerminalNode) ctx.getChild(0).getChild(0)).getSymbol().getType();
      switch (op) {
        case WaccParser.NOT:
          machine.add(new XorInstruction(reg1, reg1, new Operand2Int('#', 1)));
          break;
        case WaccParser.MINUS:
          machine.add(new SubInstruction(reg1, reg1, new Operand2Int('#', 0), true, true));
          machine.addOverflowErrorFunction(false);
          break;
        case WaccParser.LEN:
          machine.add(new LoadInstruction(reg1, new Operand2Reg(reg1, true)));
          break;
        case WaccParser.ORD:
//          machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
          break;
        case WaccParser.CHR:
//          machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
          break;
        default:
          break;
      }
      return reg1;
    } else if (ctx.ident() != null) {
      Register reg = registers.getRegister();
      int offset;
      Type type;
      offset = symbolNode.getAddress(ctx.ident().getText());
      type = symbolNode.lookupAll(ctx.ident().getText());
      if (type.getSize() == 1) {
        machine.add(new LoadByteInstruction(reg, new Operand2Reg(Registers.sp, offset)));
      } else {
        machine.add(new LoadInstruction(reg, new Operand2Reg(Registers.sp, offset)));
      }
      return reg;
    } else if (ctx.CHAR_LIT() != null) {
      Register reg = registers.getRegister();
      String text = ctx.CHAR_LIT().getText();
      String c_ = "";
//      System.out.println(text + " length = " + text.length());
      if (text.length() > 3) {
        switch (text) {
          case "\'\\0\'":
            c_ = Integer.toString(0);
            break;
          case "\'\\b\'":
            c_ = Integer.toString(8);
            break;
          case "\'\\t\'":
            c_ = Integer.toString(9);
            break;
          case "\'\\n\'":
            c_ = Integer.toString(10);
            break;
          case "\'\\f\'":
            c_ = Integer.toString(12);
            break;
          case "\'\\r\'":
            c_ = Integer.toString(13);
            break;
          case "\'\\'\'":
            c_ = "\'\\\'\'";
            break;
          case "\'\\\"\'":
            c_ = "\'\"\'";
            break;
          case "\'\\\\\'":
            c_ = "\'\\\'";
            break;
        }
      } else {
        char c = text.charAt(1);
        c_ = "'" + c + "'";
      }
      machine.add(new MovInstruction(reg, new Operand2String('#', c_)));
      return reg;
    } else if (ctx.CHARACTER_LIT() != null) {
      Register reg = registers.getRegister();
      int i = machine.addMsg(ctx.CHARACTER_LIT().getText());
      machine.add(new LoadInstruction(reg, new Operand2String('=', "msg_" + Integer.toString(i))));
      return reg;
    } else if (ctx.OPEN_PARENTHESES() != null) {
      return visit(ctx.expr(0));
    } else if (ctx.side_effect() != null) {
      return visit(ctx.side_effect());
    }
    return null;
  }

  @Override
  public Register visitFreeStat(FreeStatContext ctx) {
    Register reg = visit(ctx.expr());
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(reg)));
    machine.add(new BranchLinkInstruction("p_free_pair"));
    machine.addFreePairFunction();
    registers.free(reg);
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

//  @Override
//  public Register visitUnary_oper(WaccParser.Unary_operContext ctx) {
//    Register reg1 = visit(ctx.getChild(0));
//    int op = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
//    switch (op) {
//      case WaccParser.NOT:
//        machine.add(new XorInstruction(reg1,reg1,new Operand2Int('#',1)));
//        break;
//      case WaccParser.MINUS:
//        machine.add(new SubInstruction(reg1,reg1,new Operand2Int('#',1),true,true));
//        break;
//      case WaccParser.LEN:
//        machine.add(new LoadInstruction(reg1,new Operand2Reg(reg1,true)));
//        break;
//      case WaccParser.ORD:
//        machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
//        break;
//      case WaccParser.CHR:
//        machine.add(new LoadByteInstruction(reg1,new Operand2Reg(reg1,true)));
//        break;
//      default:
//        break;
//    }
//    return reg1;
//  }


  @Override
  public Register visitIfStat(IfStatContext ctx) {
    symbolNode = symbolNode.enterScopeCodeGen(symbolNode);
    Register lastRegister = visit(ctx.expr());
    machine.add(new CmpInstruction(lastRegister, new Operand2Int('#', 0)));
    Label elseLabel = new Label(labelnumber++); //else label
    Label thenLabel = new Label(labelnumber++); //then label
    machine.add(new BranchEqualInstruction(elseLabel.toString()));
    visit(ctx.stat(0));
    symbolNode = symbolNode.exitScope();
    machine.add(new BranchInstruction(thenLabel.toString()));
    machine.add(elseLabel);
    symbolNode = symbolNode.enterScopeCodeGen(symbolNode);
    visit(ctx.stat(1));
    machine.add(thenLabel);
    registers.free(lastRegister);
    symbolNode = symbolNode.exitScope();
    return null;
  }

  @Override
  public Register visitWhileStat(WhileStatContext ctx) {
    symbolNode = symbolNode.enterScopeCodeGen(symbolNode);
    Label startLabel = new Label(labelnumber++);
    Label loopLabel = new Label(labelnumber++);
    machine.add(new BranchInstruction(startLabel.toString()));
    machine.add(loopLabel);
    visit(ctx.stat());
    machine.add(startLabel);
    Register lastRegister = visitExpr(ctx.expr());
    machine.add(new CmpInstruction(lastRegister, new Operand2Int('#', 1)));
    machine.add(new BranchEqualInstruction(loopLabel.toString()));
    registers.free(lastRegister);
    symbolNode = symbolNode.exitScope();
    return null;
  }

  @Override
  public Register visitDoWhileStat(DoWhileStatContext ctx) {
    symbolNode = symbolNode.enterScopeCodeGen(symbolNode);
    Label startLabel = new Label(labelnumber++);
    Label loopLabel = new Label(labelnumber++);
    visit(ctx.stat());
    machine.add(new BranchInstruction(startLabel.toString()));
    machine.add(loopLabel);
    visit(ctx.stat());
    machine.add(startLabel);
    Register lastRegister = visitExpr(ctx.expr());
    machine.add(new CmpInstruction(lastRegister, new Operand2Int('#', 1)));
    machine.add(new BranchEqualInstruction(loopLabel.toString()));
    registers.free(lastRegister);
    symbolNode = symbolNode.exitScope();
    return null;
  }

  @Override
  public Register visitForStat(ForStatContext ctx) {
    symbolNode = symbolNode.enterScopeCodeGen(symbolNode);
    Label startLabel = new Label(labelnumber++);
    Label loopLabel = new Label(labelnumber++);
    visit(ctx.init_stat());
    machine.add(new BranchInstruction(startLabel.toString()));
    machine.add(loopLabel);
    visit(ctx.stat(1));
    visit(ctx.stat(0));
    machine.add(startLabel);
    Register condRegister = visitExpr(ctx.expr());
    machine.add(new CmpInstruction(condRegister, new Operand2Int('#', 1)));
    machine.add(new BranchEqualInstruction(loopLabel.toString()));
    registers.free(condRegister);
    symbolNode = symbolNode.exitScope();
    return null;
  }

  @Override
  public Register visitInitAssignStat(InitAssignStatContext ctx) {
    Register srcReg = visit(ctx.assign_rhs());
    if (ctx.assign_lhs().ident() != null) {
      String ident = ctx.assign_lhs().ident().getText();
      Type type = symbolNode.lookupAll(ident);
      if (type.equals(boolType) || type.equals(charType)) {
        machine.add(new StoreByteInstruction(srcReg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ident))));
      } else {
        machine.add(new StoreInstruction(srcReg,
            new Operand2Reg(Registers.sp, symbolNode.getAddress(ident))));
      }
    } else if (ctx.assign_lhs().array_elem() != null) {
      Register destReg = visit(ctx.assign_lhs().array_elem());
      Type type = exprTypeGetter.visitArray_elem(ctx.assign_lhs().array_elem(), symbolNode);
      machine.removeLastInstruciton();
      if (type.equals(boolType) || type.equals(charType)) {
        machine.add(new StoreByteInstruction(srcReg, new Operand2Reg(destReg, true)));
      } else {
        machine.add(new StoreInstruction(srcReg, new Operand2Reg(destReg, true)));
      }
      registers.free(destReg);
    } else if (ctx.assign_lhs().pair_elem() != null) {
      Register destReg = visit(ctx.assign_lhs().pair_elem());
      Type type = exprTypeGetter.visitPair_elem(ctx.assign_lhs().pair_elem());
      if (type.equals(boolType) || type.equals(charType)) {
        machine.add(new StoreByteInstruction(srcReg, new Operand2Reg(destReg, true)));
      } else {
        machine.add(new StoreInstruction(srcReg, new Operand2Reg(destReg, true)));
      }
      registers.free(destReg);
    }
    registers.free(srcReg);
    return null;
  }

  @Override
  public Register visitSkipStat(SkipStatContext ctx) {
    return null;
  }

  @Override
  public Register visitPrintStat(PrintStatContext ctx) {
    Register exprRegister = visit(ctx.expr());
//    System.out.println("died after this");
    Type exprType = exprTypeGetter.visitExpr(ctx.expr(), symbolNode);
//    System.out.println("Expression type = " + exprType);
    System.out.println(exprType.equals(new ArrayType(charType)));
    machine.add(new MovInstruction(Registers.r0, exprRegister));
    if (exprType instanceof PairType) {
      machine.add(new BranchLinkInstruction("p_print_reference"));
      machine.addPrintReferenceFunction();
    } else if (exprType instanceof ArrayType) {
      if (exprType.equals(new ArrayType(charType))) {
        machine.add(new BranchLinkInstruction("p_print_string"));
        machine.addPrintStringFunction();
      } else {
        machine.add(new BranchLinkInstruction("p_print_reference"));
        machine.addPrintReferenceFunction();
      }
    } else if (exprType.equals(intType)) {
      machine.add(new BranchLinkInstruction("p_print_int"));
      machine.addPrintIntFunction();
    } else if (exprType.equals(charType)) {
      machine.add(new BranchLinkInstruction("putchar"));
    } else if (exprType.equals(stringType)) {
      System.out.println("adding branch print string");
      machine.add(new BranchLinkInstruction("p_print_string"));
      machine.addPrintStringFunction();
    } else if (exprType.equals(boolType)) {
      machine.add(new BranchLinkInstruction("p_print_bool"));
      machine.addPrintBoolFunction();
    }
    registers.free(exprRegister);
    return null;
  }

  @Override
  public Register visitPrintlnStat(PrintlnStatContext ctx) {
    Register exprRegister = visit(ctx.expr());
    Type exprType = exprTypeGetter.visitExpr(ctx.expr(), symbolNode);
//    System.out.println(exprType);
    machine.add(new MovInstruction(Registers.r0, exprRegister));
//    System.out.println(exprType.equals(intType));
    if (exprType instanceof PairType) {
      machine.add(new BranchLinkInstruction("p_print_reference"));
      machine.add(new BranchLinkInstruction("p_print_ln"));
      machine.addPrintReferenceFunction();
    } else if (exprType instanceof ArrayType) {
      if (exprType.equals(new ArrayType(charType))) {
        machine.add(new BranchLinkInstruction("p_print_string"));
        machine.add(new BranchLinkInstruction("p_print_ln"));
        machine.addPrintStringFunction();
      } else {
        machine.add(new BranchLinkInstruction("p_print_reference"));
        machine.add(new BranchLinkInstruction("p_print_ln"));
        machine.addPrintReferenceFunction();
      }
    } else if (exprType.equals(intType)) {
      machine.add(new BranchLinkInstruction("p_print_int"));
      machine.add(new BranchLinkInstruction("p_print_ln"));
      machine.addPrintIntFunction();
    } else if (exprType.equals(charType)) {
      machine.add(new BranchLinkInstruction("putchar"));
      machine.add(new BranchLinkInstruction("p_print_ln"));
    } else if (exprType.equals(stringType)) {
      machine.add(new BranchLinkInstruction("p_print_string"));
      machine.add(new BranchLinkInstruction("p_print_ln"));
      machine.addPrintStringFunction();
    } else if (exprType.equals(boolType)) {
      machine.add(new BranchLinkInstruction("p_print_bool"));
      machine.add(new BranchLinkInstruction("p_print_ln"));
      machine.addPrintBoolFunction();
    }
    machine.addPrintlnFunction();
    registers.free(exprRegister);
    return null;
  }

  @Override
  public Register visitReadStat(ReadStatContext ctx) {
    Type readType = exprTypeGetter.visitAssign_lhs(ctx.assign_lhs(), symbolNode);
    if (ctx.assign_lhs().ident() != null) {
      String ident = ctx.assign_lhs().ident().getText();
      Register readRegister = registers.getRegister();
      machine.add(new AddInstruction(readRegister, Registers.sp,
          new Operand2Int('#', symbolNode.getAddress(ident))));
      machine.add(new MovInstruction(Registers.r0, readRegister));
      registers.free(readRegister);
    } else if (ctx.assign_lhs().array_elem() != null) {
      Register destReg = visit(ctx.assign_lhs().array_elem());
      machine.add(new MovInstruction(Registers.r0, destReg));
      registers.free(destReg);
    } else if (ctx.assign_lhs().pair_elem() != null) {
      Register destReg = visit(ctx.assign_lhs().pair_elem());
      machine.add(new MovInstruction(Registers.r0, destReg));
      machine.addcheckNullPointerInstruction();
      registers.free(destReg);
    }
    if (readType.equals(intType)) {
      machine.add(new BranchLinkInstruction("p_read_int"));
      machine.addReadIntFunction();
    } else if (readType.equals(charType)) {
      machine.add(new BranchLinkInstruction("p_read_char"));
      machine.addReadCharFunction();
    }
    return null;
  }

  @Override
  public Register visitSideEffectStat(SideEffectStatContext ctx) {
    Register returnReg = visit(ctx.side_effect());
    registers.free(returnReg);
    return null;
  }

  private int getSizeFromExpr(ExprContext ctx) {
    return exprTypeGetter.visitExpr(ctx, symbolNode).getSize();
  }

  private boolean exprTypeIsCharOrBool(ExprContext ctx) {
    Type type = exprTypeGetter.visitExpr(ctx, symbolNode);
    return type.equals(boolType) || type.equals(charType);
  }
}


