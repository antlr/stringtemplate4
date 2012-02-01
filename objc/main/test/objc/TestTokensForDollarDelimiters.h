#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>

@interface TestTokensForDollarDelimiters : SenTestCase {
}

- (void) testSimpleAttr;
- (void) testString;
- (void) testEscInString;
- (void) testSubtemplate;
- (void) testNestedSubtemplate;
@end
