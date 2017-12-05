package Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolNode{

  private Map<String, SymbolInfo> dictionary;
  private SymbolNode parent;
  private List<SymbolNode> children;
  private int nextNodeIndex;

  public SymbolNode(Map<String, SymbolInfo> dictionary, SymbolNode parent,
      List<SymbolNode> children) {
    this.dictionary = dictionary;
    this.parent = parent;
    this.children = children;
    this.nextNodeIndex = 0;
  }

  public SymbolNode getParent() {
    return parent;
  }

  public Map<String, SymbolInfo> getDictionary() {
    return dictionary;
  }

  public int getSize() {
    int size = 0;
    for(SymbolInfo symbolInfo : dictionary.values()) {
      size += symbolInfo.getType().getSize();
    }
    return size;
  }

  public SymbolInfo getSymbolInfo(String name){
    return dictionary.get(name);
  }

  public int getAddress(String name){
    return this.lookupAllSymbol(name).getAddress();
  }

  public SymbolNode enterScope(SymbolNode cur) {
    SymbolNode newNode = new SymbolNode(new HashMap<>(), cur, new ArrayList<>());
    cur.children.add(newNode);
    return newNode;
  }

  public SymbolNode enterScopeCodeGen(SymbolNode cur) {
    if(cur.children.isEmpty()) {
      System.out.println("Error: Node has no children");
    }
    if(nextNodeIndex >= cur.children.size()) {
      System.out.println("Error: Next children in the scope doesn't exist");
    }
    SymbolNode nextNode = cur.children.get(nextNodeIndex);
    nextNodeIndex++;
    return nextNode;
  }

  public SymbolNode exitScope() {
    return parent;
  }

  public void insert(String name, Type type) {
    dictionary.put(name, new SymbolInfo(type));
  }

  public void setAddress(String name, int address) {
    if(dictionary.get(name) == null) {
      System.out.println("Ident " + name + " not found");
    } else {
      dictionary.get(name).setAddress(address);
    }
  }

  public SymbolInfo lookupSymbol(String name) {
    if(dictionary.get(name) == null) {
      return null;
    }
    return dictionary.get(name);
  }

  public SymbolInfo lookupAllSymbol(String name) {
    SymbolNode symbolNode = this;
    while (symbolNode != null) {
      SymbolInfo symbolInfo = symbolNode.lookupSymbol(name);
      if (symbolInfo != null) {
        return symbolInfo;
      } else {
        symbolNode = symbolNode.getParent();
      }
    }
    return null;
  }

  public Type lookup(String name) {
    if(dictionary.get(name) == null) {
      return null;
    }
    return dictionary.get(name).getType();
  }

  public Type lookupAll(String name) {
    SymbolNode symbolNode = this;
    while (symbolNode != null) {
      Type type = symbolNode.lookup(name);
      if (type != null) {
        return type;
      } else {
        symbolNode = symbolNode.getParent();
      }
    }
    return null;
  }

  public boolean contain(String name) {
    //It can't find any name in it previously
    return !(this.lookupAll(name) == null);
  }

  public void printTable(int indentation) {
    if(this.getDictionary().isEmpty()) {
      for (int i = 0; i < indentation; i++) {
        System.out.print("\t");
      }
      System.out.println("EMPTY");
    } else {
      for (String key : this.getDictionary().keySet()) {
        for (int i = 0; i < indentation; i++) {
          System.out.print("\t");
        }
        System.out.println(
            key + " " + this.getDictionary().get(key).getType());
      }
    }
    for(SymbolNode childNode : this.children) {
      indentation++;
      System.out.println("-----------------");
      childNode.printTable(indentation);
      System.out.println("-----------------");
      indentation--;

    }
  }
}
