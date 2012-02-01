#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"

@interface TestIndentation : SenTestCase {
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
