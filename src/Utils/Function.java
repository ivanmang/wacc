package Utils;

import java.util.List;

public class Function {

  private Type returnType;
  private String ident;
  private List<String> identList;
  private List<Type> typeList;

  public Function(Type returnType, List<String> identList,
      List<Type> typeList) {
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
    return typeList.get(index);
  }
}
