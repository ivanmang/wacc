parser grammer Parser;

options {
  tokenVocab=Parser;
}

prog         : BEGIN (func)* stat END ;
func         : type ident OPEN_PARENTHESIS (param_list)? CLOSE_PARENTHESIS IS stat END ;
param_list   : param (COMMA param)* ;
param        : type ident ;
stat         : SKIP | type ident EQUAL assign_rhs | assign_lhs EQUAL assign_rhs |
               READ assign_lhs | FREE expr | RETURN expr | EXIT expr | PRINT expr | PRINTLN expr |
               IF expr THEN stat ELSE stat FI | WHILE expr DO stat DONE | BEGIN stat END | stat COL stat ;
baseType     : INT | BOOL | CHAR | STRING ;
unaryOper    : NOT | NEG | LEN | ORD | CHR ;
binaryOper   : MUL | DIV | MOD | PLUS | MINUS | GT | GET | LT | LET | EQ | NEQ | AND | OR ;
ident        : IDENT ;
