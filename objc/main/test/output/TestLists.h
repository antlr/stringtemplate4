#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "Assert.h"

@interface TestLists : BaseTest {
}

- (void) testJustCat;
- (void) testListLiteralWithEmptyElements;
- (void) testListLiteralWithEmptyFirstElement;
- (void) testLength;
- (void) testCat2Attributes;
- (void) testCat2AttributesWithApply;
- (void) testCat3Attributes;
- (void) testCatWithTemplateApplicationAsElement;
- (void) testCatWithIFAsElement;
- (void) testCatNullValues;
- (void) testCatWithNullTemplateApplicationAsElement;
- (void) testCatWithNestedTemplateApplicationAsElement;
- (void) testListAsTemplateArgument;
@end
