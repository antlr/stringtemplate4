#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "ErrorBuffer.h"
#import "Assert.h"

@interface TestOptions : BaseTest {
}

- (void) testSeparator;
- (void) testSeparatorWithSpaces;
- (void) testAttrSeparator;
- (void) testIncludeSeparator;
- (void) testSubtemplateSeparator;
- (void) testSeparatorWithNullFirstValueAndNullOption;
- (void) testSeparatorWithNull2ndValueAndNullOption;
- (void) testNullValueAndNullOption;
- (void) testListApplyWithNullValueAndNullOption;
- (void) testDoubleListApplyWithNullValueAndNullOption;
- (void) testMissingValueAndNullOption;
- (void) testOptionDoesntApplyToNestedTemplate;
- (void) testIllegalOption;
@end
