#import "Test.h"
#import "Assert.h"

@interface TestNullAndEmptyValues_Anon1 : NSObject <NSMutableDictionary> {
}

- (void) init;
@end

@interface TestNullAndEmptyValues : BaseTest {
}

- (void) testSeparatorWithNullFirstValue;
- (void) testTemplateAppliedToNullIsEmpty;
- (void) testTemplateAppliedToMissingValueIsEmpty;
- (void) testSeparatorWithNull2ndValue;
- (void) testSeparatorWithNullLastValue;
- (void) testSeparatorWithTwoNullValuesInRow;
- (void) testTwoNullValues;
- (void) testNullListItemNotCountedForIteratorIndex;
- (void) testSizeZeroButNonNullListGetsNoOutput;
- (void) testNullListGetsNoOutput;
- (void) testEmptyListGetsNoOutput;
- (void) testMissingDictionaryValue;
- (void) testMissingDictionaryValue2;
- (void) testMissingDictionaryValue3;
- (void) TestSeparatorEmittedForEmptyIteratorValue;
- (void) TestSeparatorEmittedForEmptyIteratorValue2;
@end
