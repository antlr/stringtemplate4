#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "ErrorBuffer.h"
//#import "ANTLR/AMutableArray.h"
//#import "LinkedHashMap.h"
//#import "ANTLR/MutableDictionary.h"

@interface TestSubtemplates_Anon1 : AMutableArray {
}

- (id) init;
@end

@interface TestSubtemplates : BaseTest {
}

- (void) test01SimpleIteration;
- (void) test02MapIterationIsByKeys;
- (void) test03SimpleIterationWithArg;
- (void) test04NestedIterationWithArg;
- (void) test05SubtemplateAsDefaultArg;
- (void) test06ParallelAttributeIteration;
- (void) test07ParallelAttributeIterationWithNullValue;
- (void) test08ParallelAttributeIterationHasI;
- (void) test09ParallelAttributeIterationWithDifferentSizes;
- (void) test10ParallelAttributeIterationWithSingletons;
- (void) test11ParallelAttributeIterationWithDifferentSizesTemplateRefInsideToo;
- (void) test12EvalSTIteratingSubtemplateInSTFromAnotherGroup;
- (void) test13EvalSTIteratingSubtemplateInSTFromAnotherGroupSingleValue;
- (void) test14EvalSTFromAnotherGroup;
@end
