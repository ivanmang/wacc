package Utils;

public class SymbolInfo {
  private Type type;
  private int address;

  public SymbolInfo(Type type) {
    this.type = type;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public int getAddress() {
    return address;
  }

  public Type getType() {
    return type;
  }
}
