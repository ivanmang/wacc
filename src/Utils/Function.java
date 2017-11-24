package Utils;

import java.util.List;

public class Function {

  private Type returnType;
  private String ident;
  private List<String> identList;
  private List<SymbolInfo> typeList;

  public Function(Type returnType, List<String> identList,
      List<SymbolInfo> typeList) {
    this.returnType = returnType;
    this.identList = identList;
    this.typeList = typeList;
  }

  public Type getReturnType() {
    return returnType;
  }

  public String getIdent(int index) {
    return identList.get(index);
  }

  public int getParamSize() {
    return identList.size();
  }

  public Type getType(int index) {
    return typeList.get(index).getType();
  }

  public SymbolInfo getSymbolInfo(int index) {
    return typeList.get(index);
  }

  public int getAddress(int index) {
    return typeList.get(index).getAddress();
  }

  public void setAddress(int index) {
    typeList.get(index).setAddress(index);
  }
}
