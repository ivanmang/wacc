import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class SymbolTable {

  private SymbolTable innerSymbolTable;
  private Map<String, Type> dictionary;


  public SymbolTable(SymbolTable symbolTable) {
    innerSymbolTable = symbolTable;
    dictionary = new Hashtable<>();
  }

  //the previous name of the specified type in this hash table, or null if it did not have one
  public Type insert(String name, Type type){
    return dictionary.put(name, type);
  }

  //the Type to which the specified name is mapped, or null if this map contains no mapping for the Type
  private Type lookup(String name){
    return dictionary.get(name);
  }

  public Type lookupAll(String name){
    SymbolTable symbolTable = this;
    while(symbolTable != null){
      Type type = symbolTable.lookup(name);
      if(type != null){
        return type;
      }else{
        symbolTable = symbolTable.innerSymbolTable;
      }
    }
    return null;
  }

  public SymbolTable getInnerSymbolTable() {
    return innerSymbolTable;
  }

  public Map<String, Type> getDictionary() {
    return dictionary;
  }
}
