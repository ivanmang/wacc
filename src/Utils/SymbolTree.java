package Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SymbolTree {
  private SymbolNode root;

  public SymbolTree() {
    root = new SymbolNode(new HashMap<>(), null, new ArrayList<>());
  }


}
