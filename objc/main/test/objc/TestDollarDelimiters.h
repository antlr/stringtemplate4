#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"

@interface TestDollarDelimiters : SenTestCase {
}

- (void) testAttr;
- (void) testParallelMap;
- (void) testRefToAnotherTemplateInSameGroup;
- (void) testDefaultArgument;
@end
