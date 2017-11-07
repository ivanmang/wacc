public class PairType implements Type{
  private Type fst;
  private Type snd;
  private boolean isNull = false;

  PairType() {
    isNull =  true;
  }

  PairType(Type fst, Type snd) {
    this.fst = fst;
    this.snd = snd;
  }

  public ID getID() {
    return ID.Pair;
  }

  //returns true if both are pairs and the first and second type matches
  @Override
  public boolean equals(Type other) {
    if(isNull) {
      return true;
    }
    if(other.getID() == ID.Pair) {
      PairType pair = (PairType) other;
      return fst.equals(pair.fst) && snd.equals(pair.snd);
    }
    return false;
  }

  @Override
  public boolean isValidType() {
    return this.getID() == ID.Pair;
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }

  @Override
  public String toString() {
    if(fst != null && snd != null) {
      return "(" + fst.toString() + ", " + snd.toString() + ")";
    }
    return "";
  }
}
