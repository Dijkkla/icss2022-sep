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
stylesheet: (style_definition | variable_assignment)* EOF;

style_definition: selector OPEN_BRACE (declaration | variable_assignment)* CLOSE_BRACE;
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;
declaration: property COLON operation SEMICOLON;
property: LOWER_IDENT;
value: COLOR | PIXELSIZE | PERCENTAGE;

variable_assignment: variable ASSIGNMENT_OPERATOR (boolean | operation) SEMICOLON;
variable: CAPITAL_IDENT;
boolean: TRUE | FALSE;

operation
    : '(' operation ')'
    | operation '^' operation
    | operation ( '*' | '/' ) operation
    | operation ( '+' | '-' ) operation
    | (value | variable | SCALAR)
    ;
