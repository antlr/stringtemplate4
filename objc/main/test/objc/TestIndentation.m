#import "TestIndentation.h"

@implementation TestIndentation

- (void) testIndentInFrontOfTwoExpr {
  NSString *templates = [[@"list(a,b) ::= <<" stringByAppendingString:@"  <a><b>"] + newline stringByAppendingString:@">>"] + newline;
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST *t = [group getInstanceOf:@"list"];
  [t.impl dump];
  [t add:@"a" arg1:@"Terence"];
  [t add:@"b" arg1:@"Jim"];
  NSString *expecting = @"  TerenceJim";
  [self assertEquals:expecting arg1:[t render]];
}

- (void) testSimpleIndentOfAttributeList {
  NSString *templates = [[@"list(names) ::= <<" stringByAppendingString:@"  <names; separator=\"\\n\">"] + newline stringByAppendingString:@">>"] + newline;
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST *t = [group getInstanceOf:@"list"];
  [t add:@"names" arg1:@"Terence"];
  [t add:@"names" arg1:@"Jim"];
  [t add:@"names" arg1:@"Sriram"];
  NSString *expecting = [[[@"  Terence" stringByAppendingString:newline] stringByAppendingString:@"  Jim"] + newline stringByAppendingString:@"  Sriram"];
  [self assertEquals:expecting arg1:[t render]];
}

- (void) testIndentOfMultilineAttributes {
  NSString *templates = [[@"list(names) ::= <<" stringByAppendingString:@"  <names; separator=\"\n\">"] + newline stringByAppendingString:@">>"] + newline;
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST *t = [group getInstanceOf:@"list"];
  [t add:@"names" arg1:@"Terence\nis\na\nmaniac"];
  [t add:@"names" arg1:@"Jim"];
  [t add:@"names" arg1:@"Sriram\nis\ncool"];
  NSString *expecting = [[[[[[[[@"  Terence" stringByAppendingString:newline] stringByAppendingString:@"  is"] + newline stringByAppendingString:@"  a"] + newline stringByAppendingString:@"  maniac"] + newline stringByAppendingString:@"  Jim"] + newline stringByAppendingString:@"  Sriram"] + newline stringByAppendingString:@"  is"] + newline stringByAppendingString:@"  cool"];
  [self assertEquals:expecting arg1:[t render]];
}

- (void) testIndentOfMultipleBlankLines {
  NSString *templates = [[@"list(names) ::= <<" stringByAppendingString:@"  <names>"] + newline stringByAppendingString:@">>"] + newline;
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST *t = [group getInstanceOf:@"list"];
  [t add:@"names" arg1:@"Terence\n\nis a maniac"];
  NSString *expecting = [[[@"  Terence" stringByAppendingString:newline] stringByAppendingString:@""] + newline stringByAppendingString:@"  is a maniac"];
  [self assertEquals:expecting arg1:[t render]];
}

- (void) testIndentBetweenLeftJustifiedLiterals {
  NSString *templates = [[[[@"list(names) ::= <<" stringByAppendingString:@"Before:"] + newline stringByAppendingString:@"  <names; separator=\"\\n\">"] + newline stringByAppendingString:@"after"] + newline stringByAppendingString:@">>"] + newline;
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST *t = [group getInstanceOf:@"list"];
  [t add:@"names" arg1:@"Terence"];
  [t add:@"names" arg1:@"Jim"];
  [t add:@"names" arg1:@"Sriram"];
  NSString *expecting = [[[[[@"Before:" stringByAppendingString:newline] stringByAppendingString:@"  Terence"] + newline stringByAppendingString:@"  Jim"] + newline stringByAppendingString:@"  Sriram"] + newline stringByAppendingString:@"after"];
  [self assertEquals:expecting arg1:[t render]];
}

- (void) testNestedIndent {
  NSString *templates = [[[[[[[[[[@"method(name,stats) ::= <<" stringByAppendingString:@"void <name>() {"] + newline stringByAppendingString:@"\t<stats; separator=\"\\n\">"] + newline stringByAppendingString:@"}"] + newline stringByAppendingString:@">>"] + newline stringByAppendingString:@"ifstat(expr,stats) ::= <<"] + newline stringByAppendingString:@"if (<expr>) {"] + newline stringByAppendingString:@"  <stats; separator=\"\\n\">"] + newline stringByAppendingString:@"}"] stringByAppendingString:@">>"] + newline stringByAppendingString:@"assign(lhs,expr) ::= <<<lhs>=<expr>;>>"] + newline;
  [self writeFile:tmpdir arg1:@"t.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST *t = [group getInstanceOf:@"method"];
  [t add:@"name" arg1:@"foo"];
  ST *s1 = [group getInstanceOf:@"assign"];
  [s1 add:@"lhs" arg1:@"x"];
  [s1 add:@"expr" arg1:@"0"];
  ST *s2 = [group getInstanceOf:@"ifstat"];
  [s2 add:@"expr" arg1:@"x>0"];
  ST *s2a = [group getInstanceOf:@"assign"];
  [s2a add:@"lhs" arg1:@"y"];
  [s2a add:@"expr" arg1:@"x+y"];
  ST *s2b = [group getInstanceOf:@"assign"];
  [s2b add:@"lhs" arg1:@"z"];
  [s2b add:@"expr" arg1:@"4"];
  [s2 add:@"stats" arg1:s2a];
  [s2 add:@"stats" arg1:s2b];
  [t add:@"stats" arg1:s1];
  [t add:@"stats" arg1:s2];
  NSString *expecting = [[[[[[[@"void foo() {" stringByAppendingString:newline] stringByAppendingString:@"\tx=0;"] + newline stringByAppendingString:@"\tif (x>0) {"] + newline stringByAppendingString:@"\t  y=x+y;"] + newline stringByAppendingString:@"\t  z=4;"] + newline stringByAppendingString:@"\t}"] + newline stringByAppendingString:@"}"];
  [self assertEquals:expecting arg1:[t render]];
}

- (void) testIndentedIFWithValueExpr {
  ST *t = [[[ST alloc] init:[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    <if(x)>foo<endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  [t add:@"x" arg1:@"x"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    foo"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIndentedIFWithElse {
  ST *t = [[[ST alloc] init:[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    <if(x)>foo<else>bar<endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  [t add:@"x" arg1:@"x"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    foo"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIndentedIFWithElse2 {
  ST *t = [[[ST alloc] init:[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    <if(x)>foo<else>bar<endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  [t add:@"x" arg1:NO];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    bar"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIndentedIFWithNewlineBeforeText {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"t" arg1:@"x" arg2:[[[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    <if(x)>\n"] stringByAppendingString:@"foo\n"] stringByAppendingString:@"    <endif>"] + newline stringByAppendingString:@"end"] + newline];
  ST *t = [group getInstanceOf:@"t"];
  [t add:@"x" arg1:@"x"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"foo"] + newline stringByAppendingString:@"end"];
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIndentedIFWithEndifNextLine {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"t" arg1:@"x" arg2:[[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    <if(x)>foo\n"] stringByAppendingString:@"    <endif>"] + newline stringByAppendingString:@"end"] + newline];
  ST *t = [group getInstanceOf:@"t"];
  [t add:@"x" arg1:@"x"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"    foo"] + newline stringByAppendingString:@"end"];
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIFWithIndentOnMultipleLines {
  ST *t = [[[ST alloc] init:[[[[[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"   <if(x)>"] + newline stringByAppendingString:@"   foo"] + newline stringByAppendingString:@"   <else>"] + newline stringByAppendingString:@"   bar"] + newline stringByAppendingString:@"   <endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"   bar"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIFWithIndentAndExprOnMultipleLines {
  ST *t = [[[ST alloc] init:[[[[[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"   <if(x)>"] + newline stringByAppendingString:@"   <x>"] + newline stringByAppendingString:@"   <else>"] + newline stringByAppendingString:@"   <y>"] + newline stringByAppendingString:@"   <endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  [t add:@"y" arg1:@"y"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"   y"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIFWithIndentAndExprWithIndentOnMultipleLines {
  ST *t = [[[ST alloc] init:[[[[[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"   <if(x)>"] + newline stringByAppendingString:@"     <x>"] + newline stringByAppendingString:@"   <else>"] + newline stringByAppendingString:@"     <y>"] + newline stringByAppendingString:@"   <endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  [t add:@"y" arg1:@"y"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"     y"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testNestedIFWithIndentOnMultipleLines {
  ST *t = [[[ST alloc] init:[[[[[[[[[[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"   <if(x)>"] + newline stringByAppendingString:@"      <if(y)>"] + newline stringByAppendingString:@"      foo"] + newline stringByAppendingString:@"      <endif>"] + newline stringByAppendingString:@"   <else>"] + newline stringByAppendingString:@"      <if(z)>"] + newline stringByAppendingString:@"      foo"] + newline stringByAppendingString:@"      <endif>"] + newline stringByAppendingString:@"   <endif>"] + newline stringByAppendingString:@"end"] + newline] autorelease];
  [t add:@"x" arg1:@"x"];
  [t add:@"y" arg1:@"y"];
  NSString *expecting = [[[@"begin" stringByAppendingString:newline] stringByAppendingString:@"      foo"] + newline stringByAppendingString:@"end"] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

- (void) testIFInSubtemplate {
  ST *t = [[[ST alloc] init:[[[[[[[@"<names:{n |" stringByAppendingString:newline] stringByAppendingString:@"   <if(x)>"] + newline stringByAppendingString:@"   <x>"] + newline stringByAppendingString:@"   <else>"] + newline stringByAppendingString:@"   <y>"] + newline stringByAppendingString:@"   <endif>"] + newline stringByAppendingString:@"}>"] + newline] autorelease];
  [t add:@"names" arg1:@"Ter"];
  [t add:@"y" arg1:@"y"];
  NSString *expecting = [@"   y" stringByAppendingString:newline] + newline;
  NSString *result = [t render];
  [self assertEquals:expecting arg1:result];
}

@end
