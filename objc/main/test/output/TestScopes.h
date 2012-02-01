#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"
#import "ErrorManager.h"
#import "Assert.h"

@interface TestScopes : BaseTest {
}

- (void) testSeesEnclosingAttr;
- (void) testMissingArg;
- (void) testUnknownAttr;
- (void) testArgWithSameNameAsEnclosing;
- (void) testIndexAttrVisibleLocallyOnly;
@end
