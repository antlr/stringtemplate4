#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "STGroup.h"
#import "STGroupFile.h"
#import "DebugST.h"
#import "InterpEvent.h"
#import "Misc.h"
#import "AMutableArray.h"

@interface TestDebugEvents : SenTestCase {
}

- (void) testString;
- (void) testAttribute;
- (void) testTemplateCall;
@end
