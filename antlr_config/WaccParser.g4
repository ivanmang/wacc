parser grammar WaccParser;

options {
  tokenVocab=WaccLexer;
}

prog           : BEGIN (func)* stat END EOF;
func           : type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param_list     : param (COMMA param)* ;
param          : type ident ;

stat           : stat_helper
               | RETURN expr
               | <assoc=right> stat_helper COL stat
               ;

stat_helper    : SKIP_                              #skipStat
               | type ident EQUAL assign_rhs        #declareAndAssignStat
               | assign_lhs EQUAL assign_rhs        #assignStat
               | READ assign_lhs                    #readStat
               | FREE expr                          #freeStat
               | EXIT expr                          #exitStat
               | PRINT expr                         #printStat
               | PRINTLN expr                       #printlnStat
               | IF expr THEN stat ELSE stat FI     #ifStat
               | WHILE expr DO stat DONE            #whileStat
               | BEGIN stat END                     #beginStat
               ;


assign_lhs     : ident
               | array_elem
               | pair_elem
               ;

assign_rhs     : expr
               | array_liter
               | new_pair
               | pair_elem
               | function_call
               ;

new_pair       : NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES;

function_call  : CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES;


arg_list       : expr (COMMA expr)*;

pair_elem      : FST expr
               | SND expr
               ;

type           : base_type
               | array_type
               | pair_type
               ;

base_type      : INT
               | BOOL
               | CHAR
               | STRING
               ;

array_type     : (base_type | pair_type ) OPEN_BRACKET CLOSE_BRACKET
               | array_type OPEN_BRACKET CLOSE_BRACKET
               ;

pair_type      : PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES;

pair_elem_type : base_type
               | array_type
               | PAIR
               ;


expr           : pair_liter | int_liter | bool_liter | CHAR_LIT | CHARACTER_LIT
               | plus_minus_oper ( GT plus_minus_oper| GET plus_minus_oper| LT plus_minus_oper| LET plus_minus_oper| EQL plus_minus_oper| NEQL plus_minus_oper| AND plus_minus_oper| OR plus_minus_oper)*
               | ident
               | array_elem
               | unary_oper expr
               | OPEN_PARENTHESES expr CLOSE_PARENTHESES
               ;

expr_helper    : pair_liter | int_liter | bool_liter | CHAR_LIT | CHARACTER_LIT
               | ident
               | array_elem
               | unary_oper expr
               | OPEN_PARENTHESES expr CLOSE_PARENTHESES
               ;


unary_oper     : NOT | MINUS | LEN | ORD | CHR ;

mul_div_oper   : expr_helper MUL expr_helper|expr_helper DIV expr_helper;

plus_minus_oper: mul_div_oper ( MOD mul_div_oper)* |mul_div_oper (PLUS mul_div_oper)* |mul_div_oper ( MINUS mul_div_oper)*;

ident          : IDENT ;

array_elem     : ident (OPEN_BRACKET expr CLOSE_BRACKET)+ ;

int_liter      : (int_sign)? INTEGER ;

int_sign       : PLUS | MINUS ;

bool_liter     : TRUE | FALSE ;

array_liter    : OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET ;

pair_liter     : NULL ;

//