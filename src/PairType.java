public class PairType implements Type{
  private Type fst;
  private Type snd;

  PairType(Type fst, Type snd) {
    this.fst = fst;
    this.snd = snd;
  }

  public ID getID() {
    return ID.Pair;
  }

  @Override
  public boolean equals(Type other) {
    if(other.getID() == ID.Pair) {
      PairType pair = (PairType) other;
      return fst.equals(pair.fst) && snd.equals(pair.snd);
    }
    return false;
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }
}
