package CodeGeneration;

import Utils.Type;

public class Register {

  public String name;
  private Type type;

  public Register(String name) {
    this.name = name;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public int getSize() {
    return type.getSize();
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
