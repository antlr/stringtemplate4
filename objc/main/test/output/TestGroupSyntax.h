#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"
#import "Misc.h"
#import "Assert.h"

@interface TestGroupSyntax : BaseTest {
}

- (void) testSimpleGroup;
- (void) testMultiTemplates;
- (void) testSingleTemplateWithArgs;
- (void) testDefaultValues;
- (void) testDefaultValueTemplateWithArg;
- (void) testNestedTemplateInGroupFile;
- (void) testNestedDefaultValueTemplate;
- (void) testNestedDefaultValueTemplateWithEscapes;
- (void) testMessedUpTemplateDoesntCauseRuntimeError;
@end
