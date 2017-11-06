import antlr.WaccParser;

public class ArrayType implements Type {

  private Type elementType;

  ArrayType() {}

  ArrayType(Type elementType) {
    this.elementType = elementType;
  }

  public void setElementType(Type elementType) {
    this.elementType = elementType;
  }

  public ID getID() {
    return ID.Array;
  }

  //If the current type is a character array and the other type is a string, their type is
  //considered equal
  @Override
  public boolean equals(Type other) {
    if (other.getID() == ID.Array) {
      ArrayType array = (ArrayType) other;
      return elementType == array.getElementType();
    } else if (elementType.getID() == ID.Base && other.getID() == ID.Base) {
      BaseType base = (BaseType) elementType;
      BaseType otherBase = (BaseType) other;
      return base.getContentId() == WaccParser.CHAR
          && otherBase.getContentId() == WaccParser.STRING;
    }
    return false;
  }

  @Override
  public boolean isValidType() {
    return this.getID() == ID.Array;
  }

  public Type getElementType() {
    return elementType;
  }

  @Override
  public String toString() {
    return elementType.toString() + "[]";
  }
}
