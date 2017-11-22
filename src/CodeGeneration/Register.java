package CodeGeneration;

public class Register {

  public String name;

  public Register(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Register)) {
      return false;
    } else {
      Register reg = (Register) o;
      if(name.equals(reg.toString())) {
        return true;
      } else {
        return false;
      }
    }
  }

}
