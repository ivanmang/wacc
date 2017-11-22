package CodeGeneration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Registers {

  public Deque<Register> returnRegisters;
  public Deque<Register> generalRegisters;
  public Deque<Register> usedRegisters;

  //List of registers
  //r0 - r3: return registers
  //r4 - r12: general registers
  //r13: stack pointer
  //r14:
  //r15: program counter
  private Register r0 = new Register("r0");
  private Register r1 = new Register("r1");
  private Register r2 = new Register("r2");
  private Register r3 = new Register("r3");
  private Register r4 = new Register("r4");
  private Register r5 = new Register("r5");
  private Register r6 = new Register("r6");
  private Register r7 = new Register("r7");
  private Register r8 = new Register("r8");
  private Register r9 = new Register("r9");
  private Register r10 = new Register("r10");
  private Register r11 = new Register("r11");
  private Register r12 = new Register("r12");
  private Register r13 = new Register("sp");
  private Register r14 = new Register("lr");
  private Register r15 = new Register("pc");

  private final List<Register> referenceReturnRegisters = new ArrayList<>(
      Arrays.asList(r0, r1, r2, r3));
  private final List<Register> referenceGeneralRegisters = new ArrayList<>(
      Arrays.asList(r4, r5, r6, r7, r8, r9, r10, r11, r12));

  //default registers available
  public Registers() {
    generalRegisters = new ArrayDeque<>(
        Arrays.asList(r4, r5, r6, r7, r8, r9, r10, r11, r12));
    returnRegisters = new ArrayDeque<>(
        Arrays.asList(r0, r1, r2, r3)
    );
    usedRegisters = new ArrayDeque<>();
  }

  //Return free registers that are used for return values and paramters
  public Register getReturnRegister() {
    if(returnRegisters.isEmpty()) {
      //TODO: no return registers available
    }
    Register dest = returnRegisters.pop();
    usedRegisters.push(dest);
    return dest;
  }

  //Return free general registers
  public Register getRegister() {
    if(generalRegisters.isEmpty()) {
      //TODO: no general registers available
    }
    Register dest = generalRegisters.pop();
    usedRegisters.push(dest);
    return dest;
  }

  //Check if register is in used
  public boolean isRegInUse(Register reg) {
    return usedRegisters.contains(reg);
  }

  //Label register as not in use and add it back to the registers for use
  public void free(Register reg) {
    usedRegisters.remove(reg);
    if(referenceGeneralRegisters.contains(reg)) {
      generalRegisters.push(reg);
    } else if(referenceReturnRegisters.contains(reg)) {
      returnRegisters.push(reg);
    }
  }

}
