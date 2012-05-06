#import "BaseTest.h"

@interface TestWhitespace : BaseTest {
}

- (void) test01TrimmedSubtemplates;
- (void) test02TrimmedSubtemplatesNoArgs;
- (void) test03TrimmedSubtemplatesArgs;
- (void) test04TrimJustOneWSInSubtemplates;
- (void) test05TrimNewlineInSubtemplates;
- (void) test06LeaveNewlineOnEndInSubtemplates;
- (void) test07TabBeforeEndInSubtemplates;
- (void) test08EmptyExprAsFirstLineGetsNoOutput;
- (void) test09EmptyLineWithIndent;
- (void) test10EmptyLine;
- (void) test11SizeZeroOnLineByItselfGetsNoOutput;
- (void) test12SizeZeroOnLineWithIndentGetsNoOutput;
- (void) test13SizeZeroOnLineWithMultipleExpr;
- (void) test14IFExpr;
- (void) test15IndentedIFExpr;
- (void) test16IFElseExpr;
- (void) test17IFOnMultipleLines;
- (void) test18NestedIFOnMultipleLines;
- (void) test19LineBreak;
- (void) test20LineBreak2;
- (void) test21LineBreakNoWhiteSpace;
- (void) test22NewlineNormalizationInTemplateString;
- (void) test23NewlineNormalizationInTemplateStringPC;
- (void) test24NewlineNormalizationInAttribute;
- (void) test25CommentOnlyLineGivesNoOutput;
- (void) test26CommentOnlyLineGivesNoOutput2;

@end
