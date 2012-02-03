#import "Intercepter.h"
#import "Test.h"
#import "ErrorBuffer.h"
#import "NSMutableArray.h"
#import "LinkedHashMap.h"
#import "NSMutableDictionary.h"
#import "Assert.h"

@interface TestSubtemplates_Anon1 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestSubtemplates : BaseTest {
}

- (void) testSimpleIteration;
- (void) testMapIterationIsByKeys;
- (void) testSimpleIterationWithArg;
- (void) testNestedIterationWithArg;
- (void) testSubtemplateAsDefaultArg;
- (void) testParallelAttributeIteration;
- (void) testParallelAttributeIterationWithNullValue;
- (void) testParallelAttributeIterationHasI;
- (void) testParallelAttributeIterationWithDifferentSizes;
- (void) testParallelAttributeIterationWithSingletons;
- (void) testParallelAttributeIterationWithDifferentSizesTemplateRefInsideToo;
- (void) testEvalSTIteratingSubtemplateInSTFromAnotherGroup;
- (void) testEvalSTIteratingSubtemplateInSTFromAnotherGroupSingleValue;
- (void) testEvalSTFromAnotherGroup;
@end
