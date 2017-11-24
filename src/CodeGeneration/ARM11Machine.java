package CodeGeneration;

import Instructions.*;
import Instructions.Branch.BranchLinkInstruction;
import Instructions.Labels.DataLabel;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.TextLabel;
import Instructions.Load.LoadEqualInstruction;
import Instructions.Load.LoadInstruction;
import Instructions.Load.LoadNotEqualInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.Operand2Int;
import Instructions.Operand2.Operand2Reg;
import Instructions.Operand2.Operand2String;
import Instructions.StringInstruction;
import Instructions.WordInstruction;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ARM11Machine {

  private Map<String, List<Instruction>> functions;
  private List<Instruction> currentFunction;
  private List<Instruction> msg;
  private Map<String, List<Instruction>> printFunctions;

  //initialise the machine
  public ARM11Machine() {
    functions = new LinkedHashMap<>();
    msg = new LinkedList<>();
    functions.put("msg", msg);
  }

  //add the label for the start of the function and add it to the map
  public void addFunctionStart(String name) {
    currentFunction = new LinkedList<>();
    //TODO: add code for function start
    functions.put(name, currentFunction);
    currentFunction.add(new Label(name));
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
    if(msg.isEmpty()) {
      msg.add(new DataLabel());
    }
    int msgIndex = msg.size() - 1;
    msg.add(new Label("msg_" + msgIndex));
    msg.add(new WordInstruction(message.length()));
    msg.add(new StringInstruction(message));
    return msgIndex;
  }

  public void addPrintIntFunction(String str) {
    List<Instruction> printInt = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_int")) {
    int msg_num = addMsg("\"%d\\0\"";

    printInt.add(new PushInstruction(Registers.lr));
    printInt.add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
    printInt.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_num)));
    printInt.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
    printInt.add(new BranchLinkInstruction("printf"));
    printInt.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
    printInt.add(new BranchLinkInstruction("fflush"));
    printInt.add(new PopInstruction(Registers.pc));
    printFunctions.put("p_print_int", printInt);
    }
  }

  public void addPrintStringFunction(String str) {
    List<Instruction> printString = new LinkedList<>();

    addMsg(str);

    if (!printFunctions.containsKey("p_print_string")) {
      int msg_num = addMsg("\"%.*s\\0\""); // Restriction: only add once

      printString.add(new PushInstruction(Registers.lr));
      printString.add(new LoadInstruction(Registers.r1, new Operand2Reg(Registers.r0))); // LDR r1, [r0]
      printString.add(new AddInstruction(Registers.r2, new Operand2Reg(Registers.r1), new Operand2Int('#', 4)));
      printString.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_num)));
      printString.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
      printString.add(new BranchLinkInstruction("printf"));
      printString.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
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

      printBool.add(new PushInstruction(Registers.lr));
      printBool.add(new CmpInstruction(Registers.r0, new Operand2Int('#', 0)));
      printBool.add(new LoadNotEqualInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_true)));
      printBool.add(new LoadEqualInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_false)));
      printBool.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
      printBool.add(new BranchLinkInstruction("printf"));
      printBool.add(new MovInstruction(Registers.r0, new Operand2Int('#', 0)));
      printBool.add(new BranchLinkInstruction("fflush"));
      printBool.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_print_bool", printBool);
    }

  }

  public void addPrintlnFunction() {
    List<Instruction> println = new LinkedList<>();

    if (!printFunctions.containsKey("p_print_ln")) {
      int msg_newline = addMsg("\"\\0\"");

      println.add(new PushInstruction(Registers.lr));
      println.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_newline)));
      println.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4)));
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
      int msg_readInt = addMsg("\"%d\\0\"");

      readInt.add(new PushInstruction(Registers.lr));
      readInt.add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
      readInt.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_readInt));
      readInt.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4) ));
      readInt.add(new BranchLinkInstruction("scanf"));
      readInt.add(new PopInstruction(Registers.pc));
      printFunctions.put("p_read_int", readInt);
    }
  }

  public void addReadCharFunction() {
    List<Instruction> readChar = new LinkedList<>();

    if (!printFunctions.containsKey("p_read_char")) {
      int msg_readChar = addMsg("\" %c\\0\"");

      readChar.add(new PushInstruction(Registers.lr));
      readChar.add(new MovInstruction(Registers.r1, new Operand2Reg(Registers.r0)));
      readChar.add(new LoadInstruction(Registers.r0, new Operand2String('=', "msg_" + msg_readChar));
      readChar.add(new AddInstruction(Registers.r0, new Operand2Reg(Registers.r0), new Operand2Int('#', 4) ));
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

  //translate the instruction into string for output
  public String toCode() {
    StringBuilder builder = new StringBuilder();
    for(List<Instruction> func : functions.values()) {
      for(Instruction instr : func) {
        for(int num = 0; num < instr.getIndentation(); num++) {
          builder.append("\t\t");
        }
        builder.append(instr.toCode()).append("\n");
      }
    }
    return builder.toString();
  }
}
