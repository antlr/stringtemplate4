#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "StringWriter.h"
#import "AMutableArray.h"

@interface TestLineWrap_Anon1 : AMutableArray {
}

- (void) init;
@end

@interface TestLineWrap : SenTestCase {
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
