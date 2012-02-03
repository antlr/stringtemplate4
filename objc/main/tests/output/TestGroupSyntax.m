#import "TestGroupSyntax.h"

@implementation TestGroupSyntax

- (void) testSimpleGroup {
  NSString * templates = [@"t() ::= <<foo>>" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[[@"t() ::= <<" stringByAppendingString:Misc.newline] stringByAppendingString:@"foo"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testMultiTemplates {
  NSString * templates = [[[@"ta(x) ::= \"[<x>]\"" stringByAppendingString:Misc.newline] stringByAppendingString:@"duh() ::= <<hi there>>"] + Misc.newline stringByAppendingString:@"wow() ::= <<last>>"] + Misc.newline;
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[[[[[[[[@"ta(x) ::= <<" stringByAppendingString:Misc.newline] stringByAppendingString:@"[<x>]"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline stringByAppendingString:@"duh() ::= <<"] + Misc.newline stringByAppendingString:@"hi there"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline stringByAppendingString:@"wow() ::= <<"] + Misc.newline stringByAppendingString:@"last"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testSingleTemplateWithArgs {
  NSString * templates = [@"t(a,b) ::= \"[<a>]\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[[@"t(a,b) ::= <<" stringByAppendingString:Misc.newline] stringByAppendingString:@"[<a>]"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testDefaultValues {
  NSString * templates = [@"t(a={def1},b=\"def2\") ::= \"[<a>]\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[[@"t(a={def1},b=\"def2\") ::= <<" stringByAppendingString:Misc.newline] stringByAppendingString:@"[<a>]"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testDefaultValueTemplateWithArg {
  NSString * templates = [@"t(a={x | 2*<x>}) ::= \"[<a>]\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[[@"t(a={x | 2*<x>}) ::= <<" stringByAppendingString:Misc.newline] stringByAppendingString:@"[<a>]"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testNestedTemplateInGroupFile {
  NSString * templates = [@"t(a) ::= \"<a:{x | <x:{y | <y>}>}>\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[@"t(a) ::= <<\n" stringByAppendingString:@"<a:{x | <x:{y | <y>}>}>\n"] stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testNestedDefaultValueTemplate {
  NSString * templates = [@"t(a={x | <x:{y|<y>}>}) ::= \"ick\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group load];
  NSString * expected = [[@"t(a={x | <x:{y|<y>}>}) ::= <<\n" stringByAppendingString:@"ick\n"] stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testNestedDefaultValueTemplateWithEscapes {
  NSString * templates = [@"t(a={x | \\< <x:{y|<y>\\}}>}) ::= \"[<a>]\"" stringByAppendingString:Misc.newline];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  NSString * expected = [[[@"t(a={x | \\< <x:{y|<y>\\}}>}) ::= <<" stringByAppendingString:Misc.newline] stringByAppendingString:@"[<a>]"] + Misc.newline stringByAppendingString:@">>"] + Misc.newline;
  NSString * result = [group show];
  [self assertEquals:expected param1:result];
}

- (void) testMessedUpTemplateDoesntCauseRuntimeError {
  NSString * templates = [[[[[[@"main(p) ::= <<\n" stringByAppendingString:@"<f(x=\"abc\")>\n"] stringByAppendingString:@">>\n"] stringByAppendingString:@"\n"] stringByAppendingString:@"f() ::= <<\n"] stringByAppendingString:@"<x>\n"] stringByAppendingString:@">>\n"];
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroupFile * group = nil;
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"main"];
  [st render];
  NSString * expected = [[@"[context [main] 1:1 passed 1 arg(s) to template f with 0 declared arg(s)," stringByAppendingString:@" context [main] 1:1 attribute x isn't defined,"] stringByAppendingString:@" context [main f] 1:1 attribute x isn't defined]"];
  NSString * result = [errors.errors description];
  [self assertEquals:expected param1:result];
}

@end
