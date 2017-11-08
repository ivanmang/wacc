package Utils;

public class AllType implements  Type{

  public AllType() {}

  @Override
  public ID getID() {
    return ID.All;
  }

  @Override
  public boolean equals(Type other) {
    return true;
  }

  @Override
  public boolean isValidType() {
    return true;
  }
}
