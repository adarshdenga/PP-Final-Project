grammar lang;

prog    : func+ ;

stat    : decl ';'                                                                     #declstat // Delcaration
        | assgn ';'                                                                    #assgnstat // Assignment        
        | ifstat ';'                                                                   #ifsat // if statement
        | floop ';'                                                                    #floopstat // for loop
        | wloop ';'                                                                    #wloopstat // while loop
        | inc ';'                                                                      #incstat // Increment class of statments
        | funccall ';'                                                                 #funcstat // Function call, possibly done in parallel
        | 'fork' funccall ';'                                                          #forkfuncstat
        | 'join' ';'                                                                   #joinstat // Join statment if concurrency is used         
        ;

decl       : 'let' ('shared')? IDENTIFIER '=' expr '::' type #vardecl
           | 'let' ('shared')? IDENTIFIER '=' '[' (expr (',' expr)*)? ']' '::' type #arrdecl
           | 'let' ('shared')? IDENTIFIER '::' type #uninitdecl
           ;

assgn   : IDENTIFIER '=' expr                                                          #regassign // Some assignment e.g. x = 2, x = y
        | IDENTIFIER binassgn expr                                                     #binassign
        ;
expr    : '(' expr ')'                                                                 #bracketexpr
        | '!' '(' expr ')'                                                             #notexpr
        | term                                                                         #exprterm // Some Term
        | expr binaryop expr                                                           #doubleexprbin // Binary operation statemernt e.g. x + y, x + 1
        | expr logicalop expr                                                          #doublelogicbin // Binary logical statemernt e.g. True and False
        | expr ordering expr                                                           #orderingexpr // binary ordering statement, e.g x > y
        // probably need logicalop here?
        ;

inc     : IDENTIFIER unaryop;                                                          // Unary operation statement e.g. x**

returnVal  : 'return' expr ';';

term    : '(' term ')'                                                                 #bracketterm
        | value                                                                        #termval // Value e.g. 1, True, etc.                           
        | IDENTIFIER                                                                   #termident // Variable e.g. x, someVariable123, etc.
        | funccall                                                                     #termfunccall // Function call
        ;

// For loop
floop   : 'for' '(' decl ';' cond ';' (assgn|inc) ')' '{' (stat)+ '}';

// While loop
wloop   : 'while' '(' cond ')' '{' stat+ '}';

// If Elif Else statement
ifstat  : 'if' '(' cond ')' '{' ifbody '}' ('else' '{' elsebody '}')?;

ifbody: stat+;
elsebody: stat+;

cond    : expr                                                                          #singlecond // Single condition e.g. x > y
        ;

func    : 'def' IDENTIFIER ('::' 'void')'(' ((IDENTIFIER '::' type) (','IDENTIFIER '::' type)*)? ')' '{' stat+ '}' ';'                                  #noreturn      
        | 'def' IDENTIFIER ('::' type) '(' ((IDENTIFIER '::' type) (','IDENTIFIER '::' type)*)? ')' '{' stat+ returnVal'}' ';'                          #withreturn
;      

funccall: IDENTIFIER '(' ( funcarg (',' funcarg)*)? ')';

funcarg : IDENTIFIER | value;
// ----------------------------------------------------------------------------------------

value   : INTEGER                                                                       #valint
        | BOOLEAN                                                                       #valbool
        ;

type    : 'int'                                                     
        | 'bool'
        | 'Arr' '[' ('int' | 'bool') ']';

IDENTIFIER: [a-z] [A-Za-z0-9]*;

INTEGER : [0-9]+;

BOOLEAN : 'True'
        | 'False';

binaryop : '+' | '-' | '*' ;                                                       // Binary operations with two operands
unaryop: '++' | '--' | '**';                                                            // Unary operations with one operand
binassgn:  '+=' | '-=' | '*='; 
logicalop: 'or' | 'and' ;                                                                // Logical operations for booleans    
ordering: '<' | '=<' | '==' | '!=' | '>=' | '>';                                        // Ordering for comparisons

WS : [ \t\n\r] -> skip;                                                                 // Ignore whitespace
COMMENT
    : '/*' .*? '*/' -> skip
;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
;