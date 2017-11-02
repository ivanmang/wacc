public interface Type{
  enum ID{
    Base,
    Pair,
    Array;
  }

  ID getID();

  boolean equals(Type other);
}
