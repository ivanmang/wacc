package Utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Function {

  private Type returnType;
  private String ident;
  private List<String> identList;
  private List<SymbolInfo> symbolInfoList = new LinkedList<>();

  public Function(Type returnType, List<String> identList,
      List<Type> typeList) {
    this.returnType = returnType;
    this.identList = identList;
    for(Type type : typeList) {
      symbolInfoList.add(new SymbolInfo(type));
    }
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
    return symbolInfoList.get(index).getType();
  }

  public SymbolInfo getSymbolInfo(int index) {
    return symbolInfoList.get(index);
  }

  public int getAddress(int index) {
    return symbolInfoList.get(index).getAddress();
  }

  public void setAddress(int index) {
    symbolInfoList.get(index).setAddress(index);
  }
}
