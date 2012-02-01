#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "RuleReturnScope.h"
#import "Tree.h"
#import "Before.h"

@interface TestTreeConstruction : gUnitBase {
}

- (void) setup;
- (void) test_template1;
- (void) test_template2;
@end
