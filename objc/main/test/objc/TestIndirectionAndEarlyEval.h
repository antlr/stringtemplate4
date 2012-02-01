#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ST.h"
#import "STGroup.h"

@interface TestIndirectionAndEarlyEval : SenTestCase {
}

- (void) testEarlyEval;
- (void) testIndirectTemplateInclude;
- (void) testIndirectTemplateIncludeWithArgs;
- (void) testIndirectTemplateIncludeViaTemplate;
- (void) testIndirectProp;
- (void) testIndirectMap;
- (void) testNonStringDictLookup;
@end
