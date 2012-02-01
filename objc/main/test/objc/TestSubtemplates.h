#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "Intercepter.h"
#import "ErrorBuffer.h"
#import "AMutableArray.h"
#import "LinkedHashMap.h"
#import "NSMutableDictionary.h"

@interface TestSubtemplates_Anon1 : AMutableArray {
}

- (void) init;
@end

@interface TestSubtemplates : SenTestCase {
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
