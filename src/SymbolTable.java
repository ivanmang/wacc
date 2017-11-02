import java.util.Dictionary;

public class SymbolTable {

  private SymbolTable enclosedSymbolTable;
  private Dictionary<String,WaccType> dictionary;


  public SymbolTable(SymbolTable symbolTable) {
    enclosedSymbolTable = symbolTable;
    dictionary = dictionary;
  }

  public WaccType add(String name, WaccType type){
    return dictionary.put(name,type);
  }

  public WaccType lookup(String name){
    return dictionary.get(name);
  }

  public WaccType lookupAll(String name){
    SymbolTable symbolTable = this;
    while(symbolTable != null){
      WaccType type = symbolTable.lookup(name);
      if(type != null){
        return type;
      }else{
         symbolTable = symbolTable.enclosedSymbolTable;
        return null;
      }
    }
    return null;
  }






}
