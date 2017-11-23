import Utils.Type;
import java.util.Hashtable;
import java.util.Map;

public class SymbolTable {

  private Map<String, Type> dictionary;
  private SymbolTable childSymbolTable;
  private SymbolTable parentSymbolTable;


  public SymbolTable(SymbolTable childSymbolTable, SymbolTable parentSymbolTable) {
    this.childSymbolTable = childSymbolTable;
    this.parentSymbolTable = parentSymbolTable;
    dictionary = new Hashtable<>();
  }

  public void setChildSymbolTable(SymbolTable childSymbolTable) {
    this.childSymbolTable = childSymbolTable;
  }

  public void setParentSymbolTable(SymbolTable parentSymbolTable) {
    this.parentSymbolTable = parentSymbolTable;
  }

  public SymbolTable enterScope(SymbolTable cur) {
    SymbolTable childSymbolTable = new SymbolTable(null, cur);
    this.childSymbolTable = childSymbolTable;
    return childSymbolTable;
  }

  public SymbolTable exitScope(SymbolTable cur) {
    return cur.parentSymbolTable;
  }

  //the previous name of the specified type in this hash table, or null if it did not have one
  public Type insert(String name, Type type) {
    return dictionary.put(name, type);
  }

  //the Utils.Type to which the specified name is mapped, or null if this map contains no mapping for the Utils.Type
  public Type lookup(String name) {
    return dictionary.get(name);
  }

  //lookup name in current and enclosed symbol table, if found return type, else return null
  public Type lookupAll(String name) {
    SymbolTable symbolTable = this;
    while (symbolTable != null) {
      Type type = symbolTable.lookup(name);
      if (type != null) {
        return type;
      } else {
        symbolTable = symbolTable.getParentSymbolTable();
      }
    }
    return null;
  }

  public boolean contain(String name) {
    //It can't find any name in it previously
    return !(this.lookupAll(name) == null);
  }

  public SymbolTable getParentSymbolTable() {
    return parentSymbolTable;
  }

  public SymbolTable getChildSymbolTable() {
    return childSymbolTable;
  }

  public Map<String, Type> getDictionary() {
    return dictionary;
  }

  public void printTable() {
    SymbolTable currentTable = this;
    int indentation = 0;
    while(currentTable != null) {
      for (String key : currentTable.getDictionary().keySet()) {
        for(int i = 0; i < indentation; i++) {
          System.out.print("\t");
        }
        System.out.println(key + " " + currentTable.getDictionary().get(key));
      }
      currentTable = currentTable.getChildSymbolTable();
      indentation++;
    }
  }
}
