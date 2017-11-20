package CodeGeneration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class ARM11Machine {

  public Deque<Register> returnRegisters;
  public Deque<Register> generalRegisters;
  public Deque<Register> usedRegisters;

  Register r0 = new Register("r0");
  Register r1 = new Register("r1");
  Register r2 = new Register("r2");
  Register r3 = new Register("r3");
  Register r4 = new Register("r4");
  Register r5 = new Register("r5");
  Register r6 = new Register("r6");
  Register r7 = new Register("r7");
  Register r8 = new Register("r8");
  Register r9 = new Register("r9");
  Register r10 = new Register("r10");
  Register r11 = new Register("r11");
  Register r12 = new Register("r12");
  Register r13 = new Register("sp");
  Register r14 = new Register("lr");
  Register r15 = new Register("pc");

  public ARM11Machine() {
    generalRegisters = new ArrayDeque<>(
        Arrays.asList(r4, r5, r6, r7, r8, r9, r10, r11, r12));
    returnRegisters = new ArrayDeque<>(
        Arrays.asList(r0, r1, r2, r3)
    );
    usedRegisters = new ArrayDeque<>();
  }
}
