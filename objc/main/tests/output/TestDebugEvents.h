#import "Test.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "DebugST.h"
#import "InterpEvent.h"
#import "Misc.h"
#import "NSMutableArray.h"
#import "Assert.h"

@interface TestDebugEvents : BaseTest {
}

- (void) testString;
- (void) testAttribute;
- (void) testTemplateCall;
@end
