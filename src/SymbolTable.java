import Utils.Type;
import java.util.Hashtable;
import java.util.Map;

public class SymbolTable {

  private Map<String, Type> dictionary;
  private SymbolTable innerSymbolTable;
  private SymbolTable outerSymbolTable;


  public SymbolTable(SymbolTable innerSymbolTable, SymbolTable outerSymbolTable) {
    this.innerSymbolTable = innerSymbolTable;
    this.outerSymbolTable = outerSymbolTable;
    dictionary = new Hashtable<>();
  }

  public void setInnerSymbolTable(SymbolTable innerSymbolTable) {
    this.innerSymbolTable = innerSymbolTable;
  }

  public void setOuterSymbolTable(SymbolTable outerSymbolTable) {
    this.outerSymbolTable = outerSymbolTable;
  }

  public SymbolTable enterScope(SymbolTable cur) {
    return new SymbolTable(cur, cur);
  }

  public SymbolTable exitScope(SymbolTable cur) {
    return cur.outerSymbolTable;
  }

  //the previous name of the specified type in this hash table, or null if it did not have one
  public Type insert(String name, Type type){
    return dictionary.put(name, type);
  }

  //the Utils.Type to which the specified name is mapped, or null if this map contains no mapping for the Utils.Type
  public Type lookup(String name){
    return dictionary.get(name);
  }

  //lookup name in current and enclosed symbol table, if found return type, else return null
  public Type lookupAll(String name){
    SymbolTable symbolTable = this;
    while(symbolTable != null){
      Type type = symbolTable.lookup(name);
      if(type != null){
        return type;
      }else{
        symbolTable = symbolTable.getInnerSymbolTable();
      }
    }
    return null;
  }

  public boolean contain(String name){
    //It can't find any name in it previously
      return !(this.lookupAll(name) == null);
  }

  public SymbolTable getOuterSymbolTable() {
    return outerSymbolTable;
  }

  public SymbolTable getInnerSymbolTable() {
    return innerSymbolTable;
  }

  public Map<String, Type> getDictionary() {
    return dictionary;
  }
}
