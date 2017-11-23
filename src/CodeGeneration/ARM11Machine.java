package CodeGeneration;

import Instructions.Instruction;
import Instructions.Labels.DataLabel;
import Instructions.Labels.GlobalMainLabel;
import Instructions.Labels.Label;
import Instructions.Labels.TextLabel;
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

  public void addPrintIntFunction() {
    List<Instruction> printInt = new LinkedList<>();
    //TODO: add msgs for printInt
    //TODO: add instructions for printInt
    printFunctions.put("p_print_int", printInt);
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
