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

  public ARM11Machine() {
    functions = new LinkedHashMap<>();
    msg = new LinkedList<>();
    functions.put("msg", msg);
  }

  public void addFunctionStart(String name) {
    currentFunction = new LinkedList<>();
    //TODO: add code for function start
    functions.put(name, currentFunction);
    currentFunction.add(new Label(name));
  }

  public void add(Instruction instr) {
    currentFunction.add(instr);
  }

  public void addMsg(String message) {
    if(msg.isEmpty()) {
      msg.add(new DataLabel());
    }
    int msgIndex = msg.size() - 1;
    msg.add(new Label("msg_" + msgIndex));
    msg.add(new WordInstruction(message.length()));
    msg.add(new StringInstruction(message));
  }

  public void endMsg() {
    msg.add(new TextLabel());
    msg.add(new GlobalMainLabel());
  }

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
