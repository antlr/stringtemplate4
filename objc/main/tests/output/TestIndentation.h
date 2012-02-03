#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "Assert.h"

@interface TestIndentation : BaseTest {
}

- (void) testIndentInFrontOfTwoExpr;
- (void) testSimpleIndentOfAttributeList;
- (void) testIndentOfMultilineAttributes;
- (void) testIndentOfMultipleBlankLines;
- (void) testIndentBetweenLeftJustifiedLiterals;
- (void) testNestedIndent;
- (void) testIndentedIFWithValueExpr;
- (void) testIndentedIFWithElse;
- (void) testIndentedIFWithElse2;
- (void) testIndentedIFWithNewlineBeforeText;
- (void) testIndentedIFWithEndifNextLine;
- (void) testIFWithIndentOnMultipleLines;
- (void) testIFWithIndentAndExprOnMultipleLines;
- (void) testIFWithIndentAndExprWithIndentOnMultipleLines;
- (void) testNestedIFWithIndentOnMultipleLines;
- (void) testIFInSubtemplate;
@end
