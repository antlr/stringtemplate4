#import "Test.h"
#import "StringWriter.h"
#import "NSMutableArray.h"
#import "Assert.h"

@interface TestLineWrap_Anon1 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestLineWrap : BaseTest {
}

- (void) testLineWrap;
- (void) testLineWrapAnchored;
- (void) testSubtemplatesAnchorToo;
- (void) testFortranLineWrap;
- (void) testLineWrapWithDiffAnchor;
- (void) testLineWrapEdgeCase;
- (void) testLineWrapLastCharIsNewline;
- (void) testLineWrapCharAfterWrapIsNewline;
- (void) testLineWrapForList;
- (void) testLineWrapForAnonTemplate;
- (void) testLineWrapForAnonTemplateAnchored;
- (void) testLineWrapForAnonTemplateComplicatedWrap;
- (void) testIndentBeyondLineWidth;
- (void) testIndentedExpr;
- (void) testNestedIndentedExpr;
- (void) testNestedWithIndentAndTrackStartOfExpr;
- (void) testLineDoesNotWrapDueToLiteral;
- (void) testSingleValueWrap;
- (void) testLineWrapInNestedExpr;
@end
