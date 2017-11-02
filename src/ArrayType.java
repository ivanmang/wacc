public class ArrayType implements Type{
  private Type elementType;

  ArrayType(Type elementType) {
    this.elementType = elementType;
  }

  public ID getID() {
    return ID.Array;
  }

  @Override
  public boolean equals(Type other) {
    if(other.getID() == ID.Array) {
      ArrayType array = (ArrayType) other;
      return elementType == array.getElementType();
    }
    return false;
  }

  public Type getElementType() {
    return elementType;
  }
}
