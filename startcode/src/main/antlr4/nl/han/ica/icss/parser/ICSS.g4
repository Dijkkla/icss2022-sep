grammar ICSS;

//--- LEXER: ---

COMMENT: ('//' .*? ('\r' | '\n' | EOF) | '/*' .*? '*/') -> skip;

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
ASSIGNMENT_OPERATOR: ':=';
PLUS: '+';
MIN: '-';
MUL: '*';
DIV: '/';
REST: '%';
POW: '^';
EXCLAM: '!';
BRACKET_OPEN: '(';
BRACKET_CLOSE: ')';

GREATER_THAN: '>';
SMALLER_THAN: '<';
GREATER_OR_EQUAL_THAN: '>=';
SMALLER_OR_EQUAL_THAN: '<=';
GREATER_OR_SMALLER_THAN: '<>';
EQUALS: '=';

AND: '&&';
OR: '||';



//--- PARSER: ---
stylesheet: ((stylerule | variableAssignment)* | (variableAssignment | operation SEMICOLON)*) EOF;

stylerule: selector+ block;
selector: CLASS_IDENT#classSelector | (ID_IDENT | COLOR)#idSelector | LOWER_IDENT#tagSelector;
block: OPEN_BRACE (declaration | variableAssignment | ifClause)* CLOSE_BRACE;

declaration: propertyName COLON operation SEMICOLON;
propertyName: LOWER_IDENT;
literal: COLOR#colorLiteral | PIXELSIZE#pixelLiteral | PERCENTAGE#percentageLiteral | SCALAR#scalarLiteral | (TRUE | FALSE)#boolLiteral;

variableAssignment: variableReference ASSIGNMENT_OPERATOR operation SEMICOLON;
variableReference: CAPITAL_IDENT;

operation
    : BRACKET_OPEN operation BRACKET_CLOSE#bracketOperation
    | operation EXCLAM#factorialOperation
    | (EXCLAM | PLUS | MIN) operation#prefixOperation
    | operation POW operation#powerOperation
    | operation (MUL | DIV | REST) operation#multiplicativeOperation
    | operation (PLUS | MIN) operation#additiveOperation
    | operation (GREATER_OR_EQUAL_THAN | GREATER_THAN | SMALLER_OR_EQUAL_THAN | SMALLER_THAN) operation#relationalOperation
    | operation ((EQUALS | EXCLAM) EQUALS | GREATER_OR_SMALLER_THAN) operation#equalityOperation
    | operation AND operation#andOperation
    | operation OR operation#orOperation
    | (variableReference | literal)#terminalOperation
    ;

ifClause: IF BOX_BRACKET_OPEN operation BOX_BRACKET_CLOSE
                block
                elseClause?;
elseClause: ELSE (ifClause | block);
