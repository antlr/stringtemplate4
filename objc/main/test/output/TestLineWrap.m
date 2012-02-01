#import "TestLineWrap.h"

@implementation TestLineWrap_Anon1

- (void) init {
  if (self = [super init]) {
    [self add:@"a"];
    [self add:x];
    [self add:@"b"];
  }
  return self;
}

@end

@implementation TestLineWrap

- (void) testLineWrap {
  NSString * templates = [@"array(values) ::= <<int[] a = { <values; wrap=\"\\n\", separator=\",\"> };>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"array"];
  [a add:@"values" param1:[NSArray arrayWithObjects:3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 2, 1, 6, 32, 5, 6, 77, 4, 9, 20, 2, 1, 4, 63, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 6, 32, 5, 6, 77, 3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 1, 6, 32, 5, nil]];
  NSString * expecting = [[[@"int[] a = { 3,9,20,2,1,4,6,32,5,6,77,888,\n" stringByAppendingString:@"2,1,6,32,5,6,77,4,9,20,2,1,4,63,9,20,2,1,\n"] stringByAppendingString:@"4,6,32,5,6,77,6,32,5,6,77,3,9,20,2,1,4,6,\n"] stringByAppendingString:@"32,5,6,77,888,1,6,32,5 };"];
  StringWriter * sw = [[[StringWriter alloc] init] autorelease];
  STWriter * stw = [[[AutoIndentWriter alloc] init:sw param1:@"\n"] autorelease];
  [stw setLineWidth:40];
  [a write:stw];
  NSString * result = [sw description];
  [self assertEquals:expecting param1:result];
}

- (void) testLineWrapAnchored {
  NSString * templates = [@"array(values) ::= <<int[] a = { <values; anchor, wrap, separator=\",\"> };>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"array"];
  [a add:@"values" param1:[NSArray arrayWithObjects:3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 2, 1, 6, 32, 5, 6, 77, 4, 9, 20, 2, 1, 4, 63, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 6, 32, 5, 6, 77, 3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 1, 6, 32, 5, nil]];
  NSString * expecting = [[[[@"int[] a = { 3,9,20,2,1,4,6,32,5,6,77,888,\n" stringByAppendingString:@"            2,1,6,32,5,6,77,4,9,20,2,1,4,\n"] stringByAppendingString:@"            63,9,20,2,1,4,6,32,5,6,77,6,\n"] stringByAppendingString:@"            32,5,6,77,3,9,20,2,1,4,6,32,\n"] stringByAppendingString:@"            5,6,77,888,1,6,32,5 };"];
  [self assertEquals:expecting param1:[a render:40]];
}

- (void) testSubtemplatesAnchorToo {
  NSString * templates = [@"array(values) ::= <<{ <values; anchor, separator=\", \"> }>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * x = [[[ST alloc] init:@"<\\n>{ <stuff; anchor, separator=\",\\n\"> }<\\n>"] autorelease];
  x.groupThatCreatedThisInstance = group;
  [x add:@"stuff" param1:@"1"];
  [x add:@"stuff" param1:@"2"];
  [x add:@"stuff" param1:@"3"];
  ST * a = [group getInstanceOf:@"array"];
  [a add:@"values" param1:[[[TestLineWrap_Anon1 alloc] init] autorelease]];
  NSString * expecting = [[[[@"{ a, \n" stringByAppendingString:@"  { 1,\n"] stringByAppendingString:@"    2,\n"] stringByAppendingString:@"    3 }\n"] stringByAppendingString:@"  , b }"];
  [self assertEquals:expecting param1:[a render:40]];
}

- (void) testFortranLineWrap {
  NSString * templates = [@"func(args) ::= <<       FUNCTION line( <args; wrap=\"\\n      c\", separator=\",\"> )>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"func"];
  [a add:@"args" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"d", @"e", @"f", nil]];
  NSString * expecting = [@"       FUNCTION line( a,b,c,d,\n" stringByAppendingString:@"      ce,f )"];
  [self assertEquals:expecting param1:[a render:30]];
}

- (void) testLineWrapWithDiffAnchor {
  NSString * templates = [@"array(values) ::= <<int[] a = { <{1,9,2,<values; wrap, separator=\",\">}; anchor> };>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"array"];
  [a add:@"values" param1:[NSArray arrayWithObjects:3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 2, 1, 6, 32, 5, 6, 77, 4, 9, 20, 2, 1, 4, 63, 9, 20, 2, 1, 4, 6, nil]];
  NSString * expecting = [[[[@"int[] a = { 1,9,2,3,9,20,2,1,4,\n" stringByAppendingString:@"            6,32,5,6,77,888,2,\n"] stringByAppendingString:@"            1,6,32,5,6,77,4,9,\n"] stringByAppendingString:@"            20,2,1,4,63,9,20,2,\n"] stringByAppendingString:@"            1,4,6 };"];
  [self assertEquals:expecting param1:[a render:30]];
}

- (void) testLineWrapEdgeCase {
  NSString * templates = [@"duh(chars) ::= <<<chars; wrap=\"\\n\"\\>>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"d", @"e", nil]];
  NSString * expecting = [@"abc\n" stringByAppendingString:@"de"];
  [self assertEquals:expecting param1:[a render:3]];
}

- (void) testLineWrapLastCharIsNewline {
  NSString * templates = [@"duh(chars) ::= <<<chars; wrap=\"\\n\"\\>>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"\n", @"d", @"e", nil]];
  NSString * expecting = [@"ab\n" stringByAppendingString:@"de"];
  [self assertEquals:expecting param1:[a render:3]];
}

- (void) testLineWrapCharAfterWrapIsNewline {
  NSString * templates = [@"duh(chars) ::= <<<chars; wrap=\"\\n\"\\>>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"\n", @"d", @"e", nil]];
  NSString * expecting = [[@"abc\n" stringByAppendingString:@"\n"] stringByAppendingString:@"de"];
  [self assertEquals:expecting param1:[a render:3]];
}

- (void) testLineWrapForList {
  NSString * templates = [@"duh(data) ::= <<!<data; wrap>!>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"data" param1:[NSArray arrayWithObjects:1, 2, 3, 4, 5, 6, 7, 8, 9, nil]];
  NSString * expecting = [[@"!123\n" stringByAppendingString:@"4567\n"] stringByAppendingString:@"89!"];
  [self assertEquals:expecting param1:[a render:4]];
}

- (void) testLineWrapForAnonTemplate {
  NSString * templates = [@"duh(data) ::= <<!<data:{v|[<v>]}; wrap>!>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"data" param1:[NSArray arrayWithObjects:1, 2, 3, 4, 5, 6, 7, 8, 9, nil]];
  NSString * expecting = [[@"![1][2][3]\n" stringByAppendingString:@"[4][5][6]\n"] stringByAppendingString:@"[7][8][9]!"];
  [self assertEquals:expecting param1:[a render:9]];
}

- (void) testLineWrapForAnonTemplateAnchored {
  NSString * templates = [@"duh(data) ::= <<!<data:{v|[<v>]}; anchor, wrap>!>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"data" param1:[NSArray arrayWithObjects:1, 2, 3, 4, 5, 6, 7, 8, 9, nil]];
  NSString * expecting = [[@"![1][2][3]\n" stringByAppendingString:@" [4][5][6]\n"] stringByAppendingString:@" [7][8][9]!"];
  [self assertEquals:expecting param1:[a render:9]];
}

- (void) testLineWrapForAnonTemplateComplicatedWrap {
  NSString * templates = [@"top(s) ::= <<  <s>.>>" stringByAppendingString:@"str(data) ::= <<!<data:{v|[<v>]}; wrap=\"!+\\n!\">!>>"] + newline;
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * t = [group getInstanceOf:@"top"];
  ST * s = [group getInstanceOf:@"str"];
  [s add:@"data" param1:[NSArray arrayWithObjects:1, 2, 3, 4, 5, 6, 7, 8, 9, nil]];
  [t add:@"s" param1:s];
  NSString * expecting = [[[[@"  ![1][2]!+\n" stringByAppendingString:@"  ![3][4]!+\n"] stringByAppendingString:@"  ![5][6]!+\n"] stringByAppendingString:@"  ![7][8]!+\n"] stringByAppendingString:@"  ![9]!."];
  [self assertEquals:expecting param1:[t render:9]];
}

- (void) testIndentBeyondLineWidth {
  NSString * templates = [@"duh(chars) ::= <<    <chars; wrap=\"\\n\"\\>>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"d", @"e", nil]];
  NSString * expecting = [[[[@"    a\n" stringByAppendingString:@"    b\n"] stringByAppendingString:@"    c\n"] stringByAppendingString:@"    d\n"] stringByAppendingString:@"    e"];
  [self assertEquals:expecting param1:[a render:2]];
}

- (void) testIndentedExpr {
  NSString * templates = [@"duh(chars) ::= <<    <chars; wrap=\"\\n\"\\>>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"duh"];
  [a add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"d", @"e", nil]];
  NSString * expecting = [[@"    ab\n" stringByAppendingString:@"    cd\n"] stringByAppendingString:@"    e"];
  [self assertEquals:expecting param1:[a render:6]];
}

- (void) testNestedIndentedExpr {
  NSString * templates = [[@"top(d) ::= <<  <d>!>>" stringByAppendingString:newline] stringByAppendingString:@"duh(chars) ::= <<  <chars; wrap=\"\\n\"\\>>>"] + newline;
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * top = [group getInstanceOf:@"top"];
  ST * duh = [group getInstanceOf:@"duh"];
  [duh add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"d", @"e", nil]];
  [top add:@"d" param1:duh];
  NSString * expecting = [[@"    ab\n" stringByAppendingString:@"    cd\n"] stringByAppendingString:@"    e!"];
  [self assertEquals:expecting param1:[top render:6]];
}

- (void) testNestedWithIndentAndTrackStartOfExpr {
  NSString * templates = [[@"top(d) ::= <<  <d>!>>" stringByAppendingString:newline] stringByAppendingString:@"duh(chars) ::= <<x: <chars; anchor, wrap=\"\\n\"\\>>>"] + newline;
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * top = [group getInstanceOf:@"top"];
  ST * duh = [group getInstanceOf:@"duh"];
  [duh add:@"chars" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", @"d", @"e", nil]];
  [top add:@"d" param1:duh];
  NSString * expecting = [[@"  x: ab\n" stringByAppendingString:@"     cd\n"] stringByAppendingString:@"     e!"];
  [self assertEquals:expecting param1:[top render:7]];
}

- (void) testLineDoesNotWrapDueToLiteral {
  NSString * templates = [@"m(args,body) ::= <<@Test public voidfoo(<args; wrap=\"\\n\",separator=\", \">) throws Ick { <body> }>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * a = [group getInstanceOf:@"m"];
  [a add:@"args" param1:[NSArray arrayWithObjects:@"a", @"b", @"c", nil]];
  [a add:@"body" param1:@"i=3;"];
  int n = [@"@Test public voidfoo(a, b, c" length];
  NSString * expecting = @"@Test public voidfoo(a, b, c) throws Ick { i=3; }";
  [self assertEquals:expecting param1:[a render:n]];
}

- (void) testSingleValueWrap {
  NSString * templates = [@"m(args,body) ::= <<{ <body; anchor, wrap=\"\\n\"> }>>" stringByAppendingString:newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * m = [group getInstanceOf:@"m"];
  [m add:@"body" param1:@"i=3;"];
  NSString * expecting = [@"{ \n" stringByAppendingString:@"  i=3; }"];
  [self assertEquals:expecting param1:[m render:2]];
}

- (void) testLineWrapInNestedExpr {
  NSString * templates = [[@"top(arrays) ::= <<Arrays: <arrays>done>>" stringByAppendingString:newline] stringByAppendingString:@"array(values) ::= <<int[] a = { <values; anchor, wrap=\"\\n\", separator=\",\"> };<\\n\\>>>"] + newline;
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * top = [group getInstanceOf:@"top"];
  ST * a = [group getInstanceOf:@"array"];
  [a add:@"values" param1:[NSArray arrayWithObjects:3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 2, 1, 6, 32, 5, 6, 77, 4, 9, 20, 2, 1, 4, 63, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 6, 32, 5, 6, 77, 3, 9, 20, 2, 1, 4, 6, 32, 5, 6, 77, 888, 1, 6, 32, 5, nil]];
  [top add:@"arrays" param1:a];
  [top add:@"arrays" param1:a];
  NSString * expecting = [[[[[[[[[[[[@"Arrays: int[] a = { 3,9,20,2,1,4,6,32,5,\n" stringByAppendingString:@"                    6,77,888,2,1,6,32,5,\n"] stringByAppendingString:@"                    6,77,4,9,20,2,1,4,63,\n"] stringByAppendingString:@"                    9,20,2,1,4,6,32,5,6,\n"] stringByAppendingString:@"                    77,6,32,5,6,77,3,9,20,\n"] stringByAppendingString:@"                    2,1,4,6,32,5,6,77,888,\n"] stringByAppendingString:@"                    1,6,32,5 };\n"] stringByAppendingString:@"int[] a = { 3,9,20,2,1,4,6,32,5,6,77,888,\n"] stringByAppendingString:@"            2,1,6,32,5,6,77,4,9,20,2,1,4,\n"] stringByAppendingString:@"            63,9,20,2,1,4,6,32,5,6,77,6,\n"] stringByAppendingString:@"            32,5,6,77,3,9,20,2,1,4,6,32,\n"] stringByAppendingString:@"            5,6,77,888,1,6,32,5 };\n"] stringByAppendingString:@"done"];
  [self assertEquals:expecting param1:[top render:40]];
}

@end
