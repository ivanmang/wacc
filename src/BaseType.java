import antlr.WaccParser;

public class BaseType implements Type{
  private final int INVALID_ID = -1;
  private int contentId;

  BaseType() {}

  BaseType(int contentId) {
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

  private int getContentId() {
    return contentId;
  }

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
  public ID getID() {
    return ID.Base;
  }
}
