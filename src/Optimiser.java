import CodeGeneration.ARM11Machine;
import Instructions.AddInstruction;
import Instructions.Instruction;
import Instructions.Load.LoadInstruction;
import Instructions.Move.MovInstruction;
import Instructions.Operand2.Operand2Int;
import Instructions.Store.StoreInstruction;
import Instructions.SubInstruction;
import java.util.List;
import java.util.Map.Entry;

public class Optimiser {

  private ARM11Machine machine;


  public Optimiser(ARM11Machine machine) {
    this.machine = machine;
  }


  public void optimise() {
    for (Entry<String, List<Instruction>> entry : machine.getFunctions()
        .entrySet()) {
      List<Instruction> instructionList = entry.getValue();
      for (int i = 0; i < instructionList.size() - 1; i++) {
        Instruction currInstr = instructionList.get(i);
        Instruction nextInstr = instructionList.get(i + 1);
        subtractingZeroToReg(instructionList, i, currInstr);
        addingZeroToReg(instructionList, i, currInstr);
        movOptimise(instructionList, i, currInstr, nextInstr);
        StrLdrOptimise(instructionList, i, currInstr, nextInstr);
      }
    }
  }

  private void subtractingZeroToReg(List<Instruction> instructionList, int i,
      Instruction currInstr) {
    if(currInstr instanceof SubInstruction){
      SubInstruction subInstr = (SubInstruction) currInstr;
      if(subInstr.getOperand2().equals(new Operand2Int('#',0))){
        if(subInstr.getSrc().equals(subInstr.getDest())){
          machine.removeInstruction(instructionList,i);
        }else{
          machine.removeInstruction(instructionList,i);
          machine.addInstruction(instructionList,i, new MovInstruction(subInstr.getDest(),subInstr.getSrc()));
        }
      }
    }
  }

  private void addingZeroToReg(List<Instruction> instructionList, int i,
      Instruction currInstr) {
    if (currInstr instanceof AddInstruction) {
      AddInstruction addInstr = (AddInstruction) currInstr;
      if (addInstr.getOperand2().equals(new Operand2Int('#', 0))) {
        if (addInstr.getSrc().equals(addInstr.getDest())) {
          machine.removeInstruction(instructionList, i);
        } else {
          machine.removeInstruction(instructionList, i);
          machine.addInstruction(instructionList, i,
              new MovInstruction(addInstr.getDest(),
                  addInstr.getSrc()));
        }

      }
    }
  }

  private void StrLdrOptimise(List<Instruction> instructionList, int i,
      Instruction currInstr,
      Instruction nextInstr) {
    if (currInstr instanceof StoreInstruction
        && nextInstr instanceof LoadInstruction) {
      StoreInstruction store = (StoreInstruction) currInstr;
      LoadInstruction load = (LoadInstruction) nextInstr;
      if (store.operand2.equals(load.getOperand2()) && store.dest
          .equals(load.getReg())) {
        machine.removeInstruction(instructionList, i + 1);
      }
    }
  }


  private void movOptimise(List<Instruction> instructionList, int i,
      Instruction currInstr,
      Instruction nextInstr) {
    if (currInstr instanceof MovInstruction
        && nextInstr instanceof MovInstruction) {
      System.out.println(currInstr.toCode());
      System.out.println(nextInstr.toCode());
      MovInstruction currMov = (MovInstruction) currInstr;
      MovInstruction nextMov = (MovInstruction) nextInstr;
      //MOV dest1 ret1
      //MOV dest2 ret2    if dest1 = ret2 then MOV dest2 ret1
      if (currMov.getDest() != null && currMov.getDest()
          .equals(nextMov.getRet())) {
        machine.removeInstruction(instructionList, i);
        machine.removeInstruction(instructionList, i);
        machine.addInstruction(instructionList, i,
            new MovInstruction(nextMov.getDest(), currMov.getRet()));
      }
    }
  }


}


