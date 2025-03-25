grammar ICSS;

//--- LEXER: ---

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
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: (stylerule | variableAssignment)* EOF;

stylerule: selector block;
selector: CLASS_IDENT#classSelector | ID_IDENT#idSelector | LOWER_IDENT#tagSelector;
block: OPEN_BRACE (declaration | variableAssignment | ifClause)* CLOSE_BRACE;

declaration: propertyName COLON operation SEMICOLON;
propertyName: LOWER_IDENT;
literal: COLOR#colorLiteral | PIXELSIZE#pixelLiteral | PERCENTAGE#percentageLiteral | SCALAR#scalarLiteral;

variableAssignment: variableReference ASSIGNMENT_OPERATOR operation SEMICOLON;
variableReference: CAPITAL_IDENT;
boolLiteral: TRUE | FALSE;

operation
    : operation MUL operation
    | operation (PLUS | MIN) operation
    | (literal | variableReference)
    ;

ifClause: IF BOX_BRACKET_OPEN (variableReference | boolLiteral) BOX_BRACKET_CLOSE
                block
                elseClause?;
elseClause: ELSE (ifClause | block);
