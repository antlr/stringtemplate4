#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "NSMutableDictionary.h"
#import "NSMutableDictionary.h"
#import "Assert.h"

@interface TestIndirectionAndEarlyEval : BaseTest {
}

- (void) testEarlyEval;
- (void) testIndirectTemplateInclude;
- (void) testIndirectTemplateIncludeWithArgs;
- (void) testIndirectTemplateIncludeViaTemplate;
- (void) testIndirectProp;
- (void) testIndirectMap;
- (void) testNonStringDictLookup;
@end
