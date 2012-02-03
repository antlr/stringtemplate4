#import "Test.h"

@interface TestLexer : BaseTest {
}

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
