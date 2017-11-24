package CodeGeneration;

import Instructions.AddInstruction;
import Instructions.Branch.BrachLinkCSInstrcution;
import Instructions.Branch.BranchLinkEqualInstruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.Branch.BranchLinkLargerThanInstruction;
import Instructions.Branch.BranchLinkVSInstruction;
import Instructions.CmpInstruction;
import Instructions.Instruction;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.Labels.DataLabel;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadCSInstruction;
import Instructions.Load.LoadEqualInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Load.LoadLargerThanInstruction;
import Instructions.Load.LoadNotEqualInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.Operand2.Operand2String;
import Instructions.Load.LoadEqualInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2String;
import Instructions.PopInstruction;
import Instructions.PushInstruction;
import Instructions.StringInstruction;
import Instructions.WordInstruction;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ARM11Machine {

  private Map<String, List<Instruction>> functions;
  private List<Instruction> previousFunction;
  private List<Instruction> currentFunction;
  private List<Instruction> msg;
  private Map<String, List<Instruction>> printFunctions;

  //initialise the machine
  public ARM11Machine() {
    functions = new LinkedHashMap<>();
    msg = new LinkedList<>();
    functions.put("msg", msg);
    printFunctions = new LinkedHashMap<>();
  }

  //add the label for the start of the function and add it to the map
  public void addFunctionStart(String name) {
    if (currentFunction != null) {
      previousFunction = currentFunction;
    }
    currentFunction = new LinkedList<>();
    functions.put(name, currentFunction);
    currentFunction.add(new Label(name));
  }

  public void addFunctionEnd() {
    currentFunction = previousFunction;
  }

  //add Instruction to the current function
  public void add(Instruction instr) {
    currentFunction.add(instr);
  }

  //add Instruction in this style:
  //.msg_(number)
  //    .word (length)
  //    .ascii (string)
  public int addMsg(String message) {
    if (msg.isEmpty()) {
      msg.add(new DataLabel());
    }
    int msgIndex = (msg.size() - 1) / 3;
    msg.add(new Label("msg_" + msgIndex));
    String messageCopy = message;
    messageCopy = messageCopy.replace("\\0", "\0").replace("\\b", "\b").replace("\\t", "\t").replace("\\n", "\n").replace("\\t", "\t")
        .replace("\\f", "\f").replace("\\r", "\r").replace("\\'", "\'").replace("\\", "\"").replace("\\\\", "\\");
    msg.add(new WordInstruction(messageCopy.length()));
    msg.add(new StringInstruction(message));
    return msgIndex;
  }

  public void addPrintIntFunction() {
    List<Instruction> printInt = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_int")) {
      int msg_num = addMsg("\"%d\\0\"");

      printInt.add(new Label("p_print_int"));
      printInt.add(new PushInstruction(Registers.lr));
      printInt
          .add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
      printInt.add(new LoadInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_num)));
      printInt.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      printInt.add(new BranchLinkInstruction("printf"));
      printInt.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      printInt.add(new BranchLinkInstruction("fflush"));
      printInt.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_print_int", printInt);
    }
  }

  public void addPrintStringFunction() {
    List<Instruction> printString = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_string")) {
      int msg_num = addMsg("\"%.*s\\0\""); // Restriction: only add once

      printString.add(new Label("p_print_string"));
      printString.add(new PushInstruction(Registers.lr));
      printString.add(new LoadInstruction(Registers.r1,
          new Operand2Reg(Registers.r0, true))); // LDR r1, [r0]
      printString.add(
          new AddInstruction(Registers.r2, Registers.r0,
              new Operand2Int('#', 4)));
      printString.add(new LoadInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_num)));
      printString.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      printString.add(new BranchLinkInstruction("printf"));
      printString
          .add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      printString.add(new BranchLinkInstruction("fflush"));
      printString.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_print_string", printString);
    }


  }

  public void addPrintBoolFunction() {
    List<Instruction> printBool = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_bool")) {
      int msg_true = addMsg("\"true\\0\"");
      int msg_false = addMsg("\"false\\0\"");

      printBool.add(new Label("p_print_bool"));
      printBool.add(new PushInstruction(Registers.lr));
      printBool.add(new CmpInstruction(Registers.r0, new Operand2Int('#', 0)));
      printBool.add(new LoadNotEqualInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_true)));
      printBool.add(new LoadEqualInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_false)));
      printBool.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      printBool.add(new BranchLinkInstruction("printf"));
      printBool.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      printBool.add(new BranchLinkInstruction("fflush"));
      printBool.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_print_bool", printBool);
    }

  }

  public void addPrintReferenceFunction() {
    List<Instruction> printReference = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_reference")) {
      int msg_num = addMsg("\"%p\\0\""); // Restriction: only add once

      printReference.add(new Label("p_print_reference"));
      printReference.add(new PushInstruction(Registers.lr));
      printReference.add(new MovInstruction(Registers.r1,
          new Operand2Reg(Registers.r0))); // LDR r1, r0
      printReference.add(new LoadInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_num)));
      printReference.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      printReference.add(new BranchLinkInstruction("printf"));
      printReference
          .add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      printReference.add(new BranchLinkInstruction("fflush"));
      printReference.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_print_string", printReference);
    }

  }

  public void addPrintlnFunction() {
    List<Instruction> println = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_ln")) {
      int msg_newline = addMsg("\"\\0\"");

      println.add(new Label("p_print_ln"));
      println.add(new PushInstruction(Registers.lr));
      println.add(new LoadInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_newline)));
      println.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      println.add(new BranchLinkInstruction("puts"));
      println.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      println.add(new BranchLinkInstruction("fflush"));
      println.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_print_ln", println);
    }
  }

  public void addReadIntFunction() {
    List<Instruction> readInt = new LinkedList<>();

    if (!printFunctions.containsKey("p_read_int")) {
      readInt.add(new Label("p_read_int"));
      int msg_readInt = addMsg("\"%d\\0\"");

      readInt.add(new PushInstruction(Registers.lr));
      readInt
          .add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
      readInt.add(new LoadInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_readInt)));
      readInt.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      readInt.add(new BranchLinkInstruction("scanf"));
      readInt.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_read_int", readInt);
    }
  }

  public void addReadCharFunction() {
    List<Instruction> readChar = new LinkedList<>();

    if (!printFunctions.containsKey("p_read_char")) {
      int msg_readChar = addMsg("\" %c\\0\"");
      readChar.add(new Label("p_read_char"));
      readChar.add(new PushInstruction(Registers.lr));
      readChar
          .add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
      readChar.add(new LoadInstruction(Registers.r0,
          new Operand2String('=', "msg_" + msg_readChar)));
      readChar.add(
          new AddInstruction(Registers.r0, Registers.r0,
              new Operand2Int('#', 4)));
      readChar.add(new BranchLinkInstruction("scanf"));
      readChar.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_read_char", readChar);
    }
  }


  //Add this to the end of the messages:
  //.text
  //
  //.global main
  public void endMsg() {
    msg.add(new TextLabel());
    msg.add(new GlobalMainLabel());
  }

  public void addCheckArrayIndexTooLargeErrorFunction(){
    if (!printFunctions.containsKey("p_check_array_bounds")) {
      add(new BranchLinkInstruction("p_check_array_bounds"));
      int arrayMsg = addMsg("\"ArrayIndexOutOfBoundsError: index too large\\n\\0\"");
      List<Instruction> arrayBoundError = new LinkedList<>();
      arrayBoundError.add(new Label("p_check_array_bounds"));
      arrayBoundError.add(new PushInstruction(Registers.lr));
      arrayBoundError.add(new CmpInstruction(Registers.r0, new Operand2Int('#',0)));

      arrayBoundError.add(new LoadLargerThanInstruction(Registers.r0,new Operand2String('=', "msg_" + arrayMsg)));
      arrayBoundError.add(new BranchLinkLargerThanInstruction("p_throw_runtime_error"));

      arrayBoundError.add(new LoadInstruction(Registers.r1,new Operand2Reg(Registers.r1))); //TODO:What is r1
      arrayBoundError.add(new CmpInstruction(Registers.r0,Registers.r1)); //TODO:What is r1
      arrayBoundError
          .add(new LoadCSInstruction(Registers.r0, new Operand2String('=', "msg_" + arrayMsg)));
      arrayBoundError.add(new BrachLinkCSInstrcution("p_throw_runtime_error"));
      arrayBoundError.add(new PopInstruction(Registers.pc));




      printFunctions.put("p_check_array_bounds", arrayBoundError);
      addRuntimeErrorInstruction();
    }
  }

  public void addCheckArrayNegErrorFunction(){
    if (!printFunctions.containsKey("p_check_array_bounds")) {
      add(new BranchLinkInstruction("p_check_array_bounds"));
      int arrayMsg = addMsg("\"ArrayIndexOutOfBoundsError: negative index\\n\\0\"");
      List<Instruction> arrayBoundError = new LinkedList<>();
      arrayBoundError.add(new Label("p_check_array_bounds"));
      arrayBoundError.add(new PushInstruction(Registers.lr));
      arrayBoundError.add(new CmpInstruction(Registers.r0, new Operand2Int('#',0)));

      arrayBoundError.add(new LoadLargerThanInstruction(Registers.r0,new Operand2String('=', "msg_" + arrayMsg)));
      arrayBoundError.add(new BranchLinkLargerThanInstruction("p_throw_runtime_error"));

      arrayBoundError.add(new LoadInstruction(Registers.r1,new Operand2Reg(Registers.r1))); //TODO:What is r1
      arrayBoundError.add(new CmpInstruction(Registers.r0,Registers.r1)); //TODO:What is r1
      arrayBoundError
          .add(new LoadCSInstruction(Registers.r0, new Operand2String('=', "msg_" + arrayMsg)));
      arrayBoundError.add(new BrachLinkCSInstrcution("p_throw_runtime_error"));
      arrayBoundError.add(new PopInstruction(Registers.pc));




      printFunctions.put("p_check_array_bounds", arrayBoundError);
      addRuntimeErrorInstruction();
    }
  }

  public void CheckDividedByZeroFunction(){
    if (!printFunctions.containsKey("p_check_divide_by_zero")) {
      add(new BranchLinkInstruction("p_check_divide_by_zero"));
      add(new BranchLinkInstruction(" __aeabi_idiv"));
      int checkZero = addMsg("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\"");
      List<Instruction> checkZeroError = new LinkedList<>();
      checkZeroError.add(new Label("p_throw_overflow_error"));
      checkZeroError.add(new PushInstruction(Registers.lr));
      checkZeroError.add(new CmpInstruction(Registers.r1,new Operand2Int('#',0)));
      checkZeroError
          .add(new LoadEqualInstruction(Registers.r0, new Operand2String('=', "msg_" + checkZero)));
      checkZeroError.add(new BranchLinkEqualInstruction("p_throw_runtime_error"));
      checkZeroError.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_throw_overflow_error", checkZeroError);
      addRuntimeErrorInstruction();
    }
  }

  public void CheckDividedByModFunction(){
    if (!printFunctions.containsKey("p_check_divide_by_zero")) {
      add(new BranchLinkInstruction("p_check_divide_by_zero"));
      add(new BranchLinkInstruction(" __aeabi_imod"));
      int checkZero = addMsg("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\"");
      List<Instruction> checkZeroError = new LinkedList<>();
      checkZeroError.add(new Label("p_throw_overflow_error"));
      checkZeroError.add(new PushInstruction(Registers.lr));
      checkZeroError.add(new CmpInstruction(Registers.r1,new Operand2Int('#',0)));
      checkZeroError
          .add(new LoadEqualInstruction(Registers.r0, new Operand2String('=', "msg_" + checkZero)));
      checkZeroError.add(new BranchLinkEqualInstruction("p_throw_runtime_error"));
      checkZeroError.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_throw_overflow_error", checkZeroError);
      addRuntimeErrorInstruction();
    }
  }


  public void addOverflowErrorFunction() {
    if (!printFunctions.containsKey("p_throw_overflow_error")) {
      add(new BranchLinkVSInstruction("p_throw_overflow_error"));
      int overflowMsg = addMsg("\"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\\n\"");
      List<Instruction> overflowError = new LinkedList<>();
      overflowError.add(new Label("p_throw_overflow_error"));
      overflowError
          .add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_" + overflowMsg)));
      overflowError.add(new BranchLinkInstruction("p_throw_runtime_error"));
      printFunctions.put("p_throw_overflow_error", overflowError);
      addRuntimeErrorInstruction();
    }
  }

  public void addRuntimeErrorInstruction() {
    if (!printFunctions.containsKey("p_throw_runtime_error")) {
      List<Instruction> runTimeError = new LinkedList<>();
      add(new BranchLinkEqualInstruction("p_throw_runtime_error"));
      runTimeError.add(new Label("p_throw_runtime_error"));
      runTimeError.add(new BranchLinkInstruction("p_print_string"));
      runTimeError.add(new MovInstruction(Registers.r0, new Operand2Int('#', -1)));
      runTimeError.add(new BranchLinkInstruction("exit"));
      printFunctions.put("p_throw_runtime_error", runTimeError);
      addPrintStringFunction();
    }
  }

  public void addcheckNullPointerInstruction() {
    if (!printFunctions.containsKey("p_check_null_pointer")) {
      add(new BranchLinkInstruction("p_check_null_pointer"));
      int nullReferenceMsg = addMsg("\"NullReferenceError: dereference a null reference\\n\\0\"");
      List<Instruction> nullPointer = new LinkedList<>();
      nullPointer.add(new Label("p_check_null_pointer"));
      nullPointer.add(new PushInstruction(Registers.lr));
      nullPointer.add(new CmpInstruction(Registers.r0, new Operand2Int('#', 0)));
      nullPointer.add(new LoadEqualInstruction(Registers.r0,
          new Operand2String('=', "msg_" + nullReferenceMsg)));
      nullPointer.add(new BranchLinkEqualInstruction("p_throw_runtime_error:"));
      nullPointer.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_check_null_pointer", nullPointer);
      addRuntimeErrorInstruction();
    }
  }

  //translate the instruction into string for output
  public String toCode() {
    StringBuilder builder = new StringBuilder();
    for (List<Instruction> func : functions.values()) {
      for (Instruction instr : func) {
        for (int num = 0; num < instr.getIndentation(); num++) {
          builder.append("\t\t");
        }
        builder.append(instr.toCode()).append("\n");
      }
    }
    for (List<Instruction> func : printFunctions.values()) {
      for (Instruction instr : func) {
        for (int num = 0; num < instr.getIndentation(); num++) {
          builder.append("\t\t");
        }
        builder.append(instr.toCode()).append("\n");
      }
    }
    return builder.toString();
  }
}
