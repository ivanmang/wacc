parser grammar WaccParser;

options {
  tokenVocab=WaccLexer;
}

prog           : BEGIN (func)* stat END ;
func           : type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END ;
param_list     : param (COMMA param)* ;
param          : type ident ;
stat           : SKIP_ | type ident EQUAL assign_rhs | assign_lhs EQUAL assign_rhs |
                 READ assign_lhs | FREE expr | RETURN expr | EXIT expr | PRINT expr | PRINTLN expr |
                 IF expr THEN stat ELSE stat FI | WHILE expr DO stat DONE | BEGIN stat END | stat COL stat ;
assign_lhs     : ident | array_elem | pair_elem ;
assign_rhs     : expr | array_liter | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES | pair_elem | CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES ;
arg_list       : expr (COMMA expr)* ;
pair_elem      : FST expr | SND expr ;
type           : base_type | array_type | pair_type ;
base_type      : INT | BOOL | CHAR | STRING ;
array_type     : type OPEN_BRACKET CLOSE_BRACKET ;
pair_type      : PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES ;
pair_elem_type : base_type | array_type | PAIR ;
expr           : int_liter | bool_liter | char_liter | str_liter | pair_liter | ident | array_elem | unary_oper expr | expr binary_oper expr | OPEN_PARENTHESES expr CLOSE_PARENTHESES ;
unary_oper     : NOT | NEG | LEN | ORD | CHR ;
binary_oper    : MUL | DIV | MOD | PLUS | MINUS | GT | GET | LT | LET | EQ | NEQ | AND | OR ;
ident          : IDENT ;
array_elem     : ident (OPEN_BRACKET expr CLOSE_BRACKET)+ ;
int_liter      : (int_sign)? (digit)+ ;
digit          : DIGIT ;
int_sign       : PLUS | MINUS ;
bool_liter     : TRUE | FALSE ;
char_liter     : QUOTATION character QUOTATION ;
str_liter      : DOUBLE_QUOTATION character DOUBLE_QUOTATION ;
character      : CHARACTER_LIT;
escape_char    : ESCAPE_CHAR ;
array_liter    : OPEN_BRACKET (expr (COMMA expr)*)? CLOSE_BRACKET ;
pair_liter     : NULL ;
comment        : HASH (ANY_CHARACTER_EXCEPT_EOL)* EOL ;