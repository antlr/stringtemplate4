#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"
#import "ErrorManager.h"

@interface TestScopes : SenTestCase {
}

- (void) testSeesEnclosingAttr;
- (void) testMissingArg;
- (void) testUnknownAttr;
- (void) testArgWithSameNameAsEnclosing;
- (void) testIndexAttrVisibleLocallyOnly;
@end
