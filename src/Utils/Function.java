package Utils;

import java.util.*;

public class Function {

  private Type returnType;
  private String ident;
  private ArrayList<String> identList;
  private List<SymbolInfo> symbolInfoList = new LinkedList<>();
  private SymbolNode symbolNode;

  public Function(Type returnType, ArrayList<String> identList,
      List<Type> typeList) {
    this.returnType = returnType;
    this.identList = identList;
    for(Type type : typeList) {
      symbolInfoList.add(new SymbolInfo(type));
    }
  }
  
  public ArrayList<String> getIdentList() {
    return identList;
  }
  
  public void setSymbolNode(SymbolNode symbolNode) {
    this.symbolNode = symbolNode;
  }

  public SymbolNode getSymbolNode() {
    return symbolNode;
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
  
  public int getIndex(String key) { return identList.indexOf(key);}

  public Type getType(int i) {
    return symbolInfoList.get(i).getType();
  }
  
  public Type getType(String key) {
    return symbolInfoList.get(getIndex(key)).getType();
  }

  public SymbolInfo getSymbolInfo(int index) {
    return symbolInfoList.get(index);
  }

  public int getAddress(String key) {
    return symbolInfoList.get(getIndex(key)).getAddress();
  }

  public void setAddress(String key,int address) {
    symbolInfoList.get(getIndex(key)).setAddress(address);
  }
}
