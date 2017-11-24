import CodeGeneration.*;
import Instructions.AddInstruction;
import Instructions.Branch.BranchEqualInstruction;
import Instructions.Branch.BranchInstruction;
import Instructions.Branch.BranchLinkEqualInstruction;
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
import Instructions.Operand2.*;
import Instructions.PopInstruction;
import Instructions.PushInstruction;
import Instructions.Store.StoreByteInstruction;
import Instructions.Store.StoreInstruction;
import Instructions.StringInstruction;
import Instructions.SubInstruction;
import antlr.WaccParser.AssignStatContext;
import antlr.WaccParser.BeginStatContext;
import antlr.WaccParser.DeclareAndAssignStatContext;
import antlr.WaccParser.ExitStatContext;
import antlr.WaccParser.ExprContext;
import antlr.WaccParser.IfStatContext;
import antlr.WaccParser.PrintStatContext;
import antlr.WaccParser.PrintlnStatContext;
import antlr.WaccParser.ProgContext;
import antlr.WaccParser.ReadStatContext;
import antlr.WaccParser.ReturnStatContext;
import antlr.WaccParser.SkipStatContext;
import antlr.WaccParser.WhileStatContext;
import antlr.WaccParserBaseVisitor;
import Utils.*;
import java.util.Map;

public class CodeGenerator extends WaccParserBaseVisitor<Register>{

  private CodeStringBuilder builder = new CodeStringBuilder("");
  private ARM11Machine machine = new ARM11Machine();
  private Registers registers = new Registers();
  private int labelnumber = 0;
  private SymbolTable symbolTable;

  public static final int MAX_STACK_SIZE = 1024;

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
  public Register visitExpr(ExprContext ctx) {
    return registers.r0;
  }

  @Override
  public Register visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    if (ctx.type().base_type() != null) { //base type

      Register reg = visit(ctx.assign_rhs().expr());

      if(ctx.type().base_type().STRING()!=null){ //string
        String string = ctx.assign_rhs().getText();
        int msg_num = machine.addMsg(string);
        machine.add(new LoadInstruction(reg,
            new Operand2String('=', "msg_"+msg_num)));
      }

      if(ctx.type().base_type().CHAR()!= null || ctx.type().base_type().BOOL()!=null){
        machine.add(new StoreByteInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      }else{
        machine.add(new StoreInstruction(reg,
            new Operand2Reg(Registers.sp, symbolTable.getAddress(ctx.ident().getText()))));
      }

      registers.free(reg);


    }

    return null;
  }

  @Override
  public Register visitBeginStat(BeginStatContext ctx) {
    int address = 0;

    symbolTable = symbolTable.enterScope(symbolTable);
    //get the symbol table with it's address and type
    Map<String, SymbolInfo> dict = symbolTable.getDictionary();
    //iterate all variables and assign a address to it
    for(String name : dict.keySet()) {
      dict.get(name).setAddress(address);
      address += dict.get(name).getType().getSize();
    }
    //get the size of the variable store
    int reserveByte = symbolTable.getSize();

    //if size exceed max stack size reserve, Push max_size first
    while(reserveByte > MAX_STACK_SIZE) {
      machine.add(new SubInstruction(Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    machine.add(new SubInstruction(Registers.sp, new Operand2Int('#', reserveByte)));
    reserveByte = symbolTable.getSize();

    visit(ctx.stat());

    //if size exceed max stack size reserve, Push max_size first
    while(reserveByte > MAX_STACK_SIZE) {
      machine.add(new AddInstruction(Registers.sp, new Operand2Int('#', MAX_STACK_SIZE)));
      reserveByte -= MAX_STACK_SIZE;
    }
    //Pop the variables
    machine.add(new AddInstruction(Registers.sp, new Operand2Int('#', reserveByte)));

    symbolTable = symbolTable.exitScope(symbolTable);
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

  public int ArrayIndexNegErrorMsg(){
    return machine.addMsg("ArrayIndexOutOfBoundsError: negative index\n\0");
  }

  public int ArrayIndexTooLargeErrorMsg(){
    return machine.addMsg("ArrayIndexOutOfBoundsError: index too large\n\0");
  }

  public int DividedByZeroMsg(){
    return machine.addMsg("DivideByZeroError: divide or modulo by zero\n\0");
  }

  public int overFlowErrorMsg(){
    return machine.addMsg("OverflowError: the result is too small/large to store in a 4-byte signed-integer");
  }

  public int NullReferenceMsg(){
    return machine.addMsg("NullReferenceError: dereference a null reference\\n\\0");
  }

  public void throwRuntimeError(){
    machine.add(new Label("p_throw_runtime_error:"));
    machine.add(new BranchLinkInstruction("p_print_string"));
    machine.add(new MovInstruction(Registers.r0,new Operand2Int('#',-1)));
    machine.add(new BranchLinkInstruction("exit"));
  }


  public void checkNullPointer(int nullReferenceMsg){
    machine.add(new PushInstruction(Registers.lr));
    machine.add(new CmpInstruction(Registers.r0, new Operand2Int('#',0)));
    machine.add(new LoadEqualInstruction(Registers.r0, new Operand2String('=',"msg"+nullReferenceMsg)));
    machine.add(new BranchLinkEqualInstruction("p_throw_runtime_error:"));
    machine.add(new PopInstruction(Registers.pc));
  }


}

  @Override
  public Register visitReturnStat(ReturnStatContext ctx) {
//    if ctx is not leaf
    machine.add(new LoadInstruction(registers.getRegister(), new Operand2Reg(Registers.sp)));
    return null;
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
//  if the number behind print is int then use Operand2Int
//    int num = ctx.getChild.get(1);
//  if the number behind print is char then use Operand2Char
//    char chr = ctx.getChild.get(1);
//  if the number behind print is string then use Operand2String
//    String str = ctx.getChild.get(1);
// TODO: integrate expression into the implementation of print
    int msg_num = machine.addMsg(str);
    Register current = registers.getRegister();
    machine.add(new LoadInstruction(current, new Operand2String('=', "msg_" + msg_num)));
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(current)));
    registers.free(current);

//    if number behind print is int
    machine.add(new BranchLinkInstruction("p_print_int"));
//    if behind print is char
    Register current = registers.getRegister();
    machine.add(new MovInstruction(current, new Operand2Char('#', chr)));
    machine.add(new MovInstruction(Registers.r0, new Operand2Reg(current)));
    registers.free(current);
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
    String str = ctx.getChild.get(0);
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


