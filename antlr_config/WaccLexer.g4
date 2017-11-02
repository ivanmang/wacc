lexer grammar WaccLexer;

//base type
INT: 'int';
BOOL: 'bool';
CHAR: 'char';
STRING: 'string';

//stat
SKIP_: 'skip';
READ: 'read';
FREE: 'free';
RETURN: 'return';
EXIT: 'exit';
PRINT: 'print';
PRINTLN: 'println';
IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi' ;
WHILE: 'while';
DO: 'do';
DONE: 'done';
BEGIN: 'begin';
END: 'end';
IS: 'is' ;

//pair type
PAIR: 'pair' ;

//assign rules
NEWPAIR: 'newpair';
CALL: 'call';

//pair elem
FST: 'fst';
SND: 'snd';

//un operators
NOT: '!';
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;


//bin operators
MUL: '*' ;
DIV: '/' ;
MOD: '%' ;
PLUS: '+' ;
MINUS: '-' ;
GT: '>' ;
GET: '>=';
LT: '<' ;
LET: '<=' ;
EQL: '==';
NEQL: '!=';
AND: '&&' ;
OR : '||' ;

//bool
TRUE: 'true';
FALSE: 'false';

//char literal
CHAR_LIT: '\'' ((.) | ESCAPE) '\''  ;
CHARACTER_LIT: '"' ( ~'\\' | ESCAPE )*? '"' ;
ESCAPE: '\\0' | '\\b' | '\\t' | '\\n' | '\\f' | '\\r' | '\\\'' | '\\\"' | '\\\\';

//ident
IDENT: ('_' | LOWERCASE | UPPERCASE ) ('_' | LOWERCASE | UPPERCASE | DIGIT)*;

//character
LOWERCASE: 'a'..'z';
UPPERCASE: 'A'..'Z';

//punctuations
OPEN_PARENTHESES : '(' ;
CLOSE_PARENTHESES : ')' ;
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
COMMA: ',' ;
EQUAL: '=';
HASH: '#';
COL: ';';


//numbers
fragment DIGIT : '0'..'9' ;

INTEGER: DIGIT+ ;

//null
NULL: 'null' ;

WHITESPACE: ( '\t' | ' ' | '\r' | '\n')+ -> channel(HIDDEN) ;
COMMENT: ('#' (.)*? '\n' )+ -> skip ;


