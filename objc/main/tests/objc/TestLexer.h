#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <GHUnit/GHTestCase.h>
#import "STLexer.h"
#import "STToken.h"

@interface TestLexer : GHTestCase {
}

- (void)setUp;
- (void)tearDown;

- (void) checkTokens:(NSString *)template expected:(NSString *)expected;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;

- (void) testOneExpr;
- (void) testOneExprSurrounded;
- (void) testEscDelim;
- (void) testEscEsc;
- (void) testEscDelimHasCorrectStartChar;
- (void) testEscChar;
- (void) testString;
- (void) testEscInString;
- (void) testSubtemplate;
- (void) testSubtemplateNoArg;
- (void) testSubtemplateMultiArgs;
- (void) testNestedSubtemplate;
- (void) testNestedList;
- (void) testIF;
- (void) testIFNot;
- (void) testIFELSE;
- (void) testELSEIF;
- (void) testEmbeddedRegion;
- (void) testRegion;
@end
