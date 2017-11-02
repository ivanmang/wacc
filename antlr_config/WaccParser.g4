parser grammar WaccParser;

options {
  tokenVocab=WaccLexer;
}

prog           : BEGIN (func)* stat END EOF;
func           : type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param_list     : param (COMMA param)* ;
param          : type ident ;

stat           : SKIP_
               | type ident EQUAL assign_rhs
               | assign_lhs EQUAL assign_rhs
               | READ assign_lhs
               | FREE expr
               | RETURN expr
               | EXIT expr
               | PRINT expr
               | PRINTLN expr
               | IF expr THEN stat ELSE stat FI
               | WHILE expr DO stat DONE
               | BEGIN stat END
               | stat_helper COL stat ;

stat_helper    : SKIP_
               | type ident EQUAL assign_rhs
               | assign_lhs EQUAL assign_rhs
               | READ assign_lhs
               | FREE expr
               | EXIT expr
               | PRINT expr
               | PRINTLN expr
               | IF expr THEN stat ELSE stat FI
               | WHILE expr DO stat DONE
               | BEGIN stat END
               | stat_helper COL stat;

assign_lhs     : ident
               | array_elem
               | pair_elem;

assign_rhs     : expr
               | array_liter
               | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
               | pair_elem
               | CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES ;

arg_list       : expr (COMMA expr)* ;

pair_elem      : FST expr
               | SND expr;

type           : base_type
               | array_type
               | pair_type ;

base_type      : INT
               | BOOL
               | CHAR
               | STRING ;

array_type     : (base_type | pair_type ) OPEN_BRACKET CLOSE_BRACKET
               | array_type OPEN_BRACKET CLOSE_BRACKET;

pair_type      : PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES ;

pair_elem_type : base_type
               | array_type
               | PAIR ;

expr           : int_liter | bool_liter | CHAR_LIT | CHARACTER_LIT | pair_liter
               | expr binary_oper expr
               | ident
               | array_elem
               | unary_oper expr
               | OPEN_PARENTHESES expr CLOSE_PARENTHESES ;

unary_oper     : NOT | MINUS | LEN | ORD | CHR ;

binary_oper    : MUL | DIV | MOD | PLUS | MINUS | GT | GET | LT | LET | EQL | NEQL | AND | OR ;

ident          : IDENT ;

array_elem     : ident (OPEN_BRACKET expr CLOSE_BRACKET)+ ;

int_liter      : (int_sign)? INTEGER ;

int_sign       : PLUS | MINUS ;

bool_liter     : TRUE | FALSE ;

array_liter    : OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET ;
pair_liter     : NULL ;