import antlr.WaccParser.*;
import antlr.WaccParserBaseVisitor;

public class SemanticChecker extends WaccParserBaseVisitor<Type>{


  SymbolTable symbolTable = new SymbolTable();

  public boolean typeChecker(Type type, Type type1){
    return type.equals(type1);
  }

  @Override
  public Type visitDeclareAndAssignStat(DeclareAndAssignStatContext ctx) {
    Type current = visit(ctx.type());
    Type assign = visit(ctx.assign_rhs());
    if(!typeChecker(current,assign)){


    }
    return super.visitDeclareAndAssignStat(ctx);
  }

}
