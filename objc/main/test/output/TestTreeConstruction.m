#import "TestTreeConstruction.h"

@implementation TestTreeConstruction

- (void) setup {
  lexerClassName = @"org.stringtemplate.v4.compiler.STLexer";
  parserClassName = @"org.stringtemplate.v4.compiler.STParser";
}

- (void) test_template1 {
  RuleReturnScope * rstruct = (RuleReturnScope *)[self execParser:@"template" param1:@"<[]>" param2:16];
  NSObject * actual = [((Tree *)[rstruct tree]) toStringTree];
  NSObject * expecting = @"(EXPR [)";
  [self assertEquals:@"testing rule template" param1:expecting param2:actual];
}

- (void) test_template2 {
  RuleReturnScope * rstruct = (RuleReturnScope *)[self execParser:@"template" param1:@"<[a,b]>" param2:17];
  NSObject * actual = [((Tree *)[rstruct tree]) toStringTree];
  NSObject * expecting = @"(EXPR ([ a b))";
  [self assertEquals:@"testing rule template" param1:expecting param2:actual];
}

@end
