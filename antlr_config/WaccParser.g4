parser grammar WaccParser;

options {
  tokenVocab=WaccLexer;
}

prog           : BEGIN (func)* stat END EOF;
func           : type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param_list     : param (COMMA param)* ;
param          : type ident ;

//stat           : stat_helper
//               | RETURN expr
//               | <assoc=right> stat COL stat
//               ;
//
//func_stat      : stat_helper
//               | RETURN expr
//               | <assoc=right> stat_helper COL func_stat
//               ;

stat           : SKIP_                              #skipStat
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
               | RETURN expr                        #returnStat
               | <assoc=right> stat COL stat        #statToStat
               | side_effect                        #sideEffectStat
               ;

//stat_helper    : SKIP_                              #skipStat
//               | type ident EQUAL assign_rhs        #declareAndAssignStat
//               | assign_lhs EQUAL assign_rhs        #assignStat
//               | READ assign_lhs                    #readStatg
//               | FREE expr                          #freeStat
//               | EXIT expr                          #exitStat
//               | PRINT expr                         #printStat
//               | PRINTLN expr                       #printlnStat
//               | IF expr THEN stat ELSE stat FI     #ifStat
//               | WHILE expr DO stat DONE            #whileStat
//               | BEGIN stat END                     #beginStat
//               ;


assign_lhs     : ident
               | array_elem
               | pair_elem
               ;

assign_rhs     : expr
               | array_liter
               | new_pair
               | pair_elem
               | function_call
               | side_effect
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

int_liter      : (int_sign)? INTEGER ;

expr           : pair_liter | int_liter | bool_liter | CHAR_LIT | CHARACTER_LIT
               | expr binary_oper_mul expr
               | expr binary_oper_plus expr
               | expr binary_oper_eql expr
               | expr binary_oper_and_or expr
               | ident
               | array_elem
               | OPEN_PARENTHESES expr CLOSE_PARENTHESES
               | unary_oper expr
               | side_effect
               ;

side_effect    : ident INC | INC ident | ident DEC | DEC ident ;

unary_oper     : NOT | MINUS | LEN | ORD | CHR ;

binary_oper_mul   : MUL | DIV | MOD ;
binary_oper_plus  : PLUS | MINUS ;
binary_oper_eql  : GT | GET | LT | LET | EQL | NEQL;
binary_oper_and_or  : AND | OR ;

ident          : IDENT ;

array_elem     : ident (OPEN_BRACKET expr CLOSE_BRACKET)+ ;

int_sign       : PLUS | MINUS ;

bool_liter     : TRUE | FALSE ;

array_liter    : OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET ;

pair_liter     : NULL ;

//