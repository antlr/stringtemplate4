#import "gUnitBase.h"

@implementation gUnitBase

- (NSObject *) execParser:(NSString *)ruleName input:(NSString *)input scriptLine:(int)scriptLine {
  ANTLRStringStream * is = [[[ANTLRStringStream alloc] init:input] autorelease];
  Class * lexerClass = [Class forName:lexerClassName];
  NSArray * lexArgTypes = [NSArray arrayWithObjects:[CharStream class], nil];
  Constructor * lexConstructor = [lexerClass getConstructor:lexArgTypes];
  NSArray * lexArgs = [NSArray arrayWithObjects:is, nil];
  TokenSource * lexer = (TokenSource *)[lexConstructor newInstance:lexArgs];
  [is setLine:scriptLine];
  CommonTokenStream * tokens = [[[CommonTokenStream alloc] init:lexer] autorelease];
  Class * parserClass = [Class forName:parserClassName];
  NSArray * parArgTypes = [NSArray arrayWithObjects:[TokenStream class], nil];
  Constructor * parConstructor = [parserClass getConstructor:parArgTypes];
  NSArray * parArgs = [NSArray arrayWithObjects:tokens, nil];
  Parser * parser = (Parser *)[parConstructor newInstance:parArgs];
  if (adaptorClassName != nil) {
    parArgTypes = [NSArray arrayWithObjects:[TreeAdaptor class], nil];
    Method * m = [parserClass getMethod:@"setTreeAdaptor" param1:parArgTypes];
    Class * adaptorClass = [Class forName:adaptorClassName];
    [m invoke:parser param1:[adaptorClass newInstance]];
  }
  Method * ruleMethod = [parserClass getMethod:ruleName];
  return [ruleMethod invoke:parser];
}

- (void) dealloc {
  [lexerClassName release];
  [parserClassName release];
  [adaptorClassName release];
  [super dealloc];
}

@end
