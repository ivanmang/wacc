import CodeGeneration.ARM11Machine;
import Instructions.Instruction;
import Instructions.Load.LoadInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Store.StoreInstruction;
import java.util.List;
import java.util.Map.Entry;

public class Optimiser {

  private ARM11Machine machine;


  public Optimiser(ARM11Machine machine) {
    this.machine = machine;
  }


  public void optimise() {
    for (Entry<String, List<Instruction>> entry : machine.getFunctions().entrySet()) {
      List<Instruction> instructionList = entry.getValue();
      for (int i = 0; i < instructionList.size() - 1; i++) {
        Instruction currInstr = instructionList.get(i);
        Instruction nextInstr = instructionList.get(i + 1);
        movOptimise(instructionList, i, currInstr, nextInstr);
        StrLdrOptimise(instructionList, i, currInstr, nextInstr);
      }
    }
  }

  private void StrLdrOptimise(List<Instruction> instructionList, int i, Instruction currInstr,
      Instruction nextInstr) {
    if (currInstr instanceof StoreInstruction && nextInstr instanceof LoadInstruction) {
      StoreInstruction store = (StoreInstruction) currInstr;
      LoadInstruction load = (LoadInstruction) nextInstr;
      if (store.operand2.equals(load.getOperand2()) && store.dest.equals(load.getReg())) {
        machine.removeInstruction(instructionList, i + 1);
      }
    }
  }


  private void movOptimise(List<Instruction> instructionList, int i, Instruction currInstr,
      Instruction nextInstr) {
    if (currInstr instanceof MovInstruction && nextInstr instanceof MovInstruction) {
      System.out.println(currInstr.toCode());
      System.out.println(nextInstr.toCode());
      MovInstruction currMov = (MovInstruction) currInstr;
      MovInstruction nextMov = (MovInstruction) nextInstr;
      //MOV dest1 ret1
      //MOV dest2 ret2    if dest1 = ret2 then MOV dest2 ret1
      if (currMov.getDest() != null && currMov.getDest().equals(nextMov.getRet())) {
        machine.removeInstruction(instructionList, i);
        machine.removeInstruction(instructionList, i);
        machine.addInstruction(instructionList, i,
            new MovInstruction(nextMov.getDest(), currMov.getRet()));
      }
    }
  }


}


