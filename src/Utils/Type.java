package Utils;

public interface Type{
  ID getID();

  boolean equals(Type other);

  boolean isValidType();

  String toString();
}
