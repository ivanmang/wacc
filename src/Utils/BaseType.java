package Utils;

import antlr.WaccParser;

public class BaseType implements Type{
  private final int INVALID_ID = -1;
  private int contentId;

  public BaseType() {}

  public BaseType(int contentId) {
    switch(contentId) {
      case WaccParser.INT:
      case WaccParser.BOOL:
      case WaccParser.CHAR:
      case WaccParser.STRING:
        this.contentId = contentId;
        break;
      default:
        this.contentId = INVALID_ID;
    }

  }

  public int getContentId() {
    return contentId;
  }

  //If the current type is a character array and the other type is a string, their type is
  //considered equal
  public boolean equals(Type other) {
    if(other.getID() == ID.Base) {
      BaseType base = (BaseType) other;
      return contentId == base.getContentId();
    } else if(other.getID() == ID.Array && contentId == WaccParser.STRING) {
      ArrayType array = (ArrayType) other;
      if(array.getElementType().getID() == ID.Base) {
        BaseType base = (BaseType) array.getElementType();
        return base.contentId == WaccParser.CHAR;
      }
    }
    return false;
  }

  @Override
  public boolean isValidType() {
    return contentId != INVALID_ID;
  }

  @Override
  public ID getID() {
    return ID.Base;
  }

  @Override
  public String toString() {
    switch(contentId) {
      case WaccParser.INT:
        return "INT";
      case WaccParser.BOOL:
        return "BOOL";
      case WaccParser.CHAR:
        return "CHAR";
      case WaccParser.STRING:
        return "CHAR[]";
      default:
        return "Unknown Utils.Type";
    }
  }
}
