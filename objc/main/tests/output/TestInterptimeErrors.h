#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"
#import "Misc.h"
#import "Assert.h"

@interface UserHiddenName : NSObject {
  NSString * name;
}

- (id) initWithName:(NSString *)name;
- (NSString *) getName;
@end

@interface UserHiddenNameField : NSObject {
  NSString * name;
}

- (id) initWithName:(NSString *)name;
@end

@interface TestInterptimeErrors : BaseTest {
}

- (void) testMissingEmbeddedTemplate;
- (void) testMissingSuperTemplate;
- (void) testNoPropertyNotError;
- (void) testHiddenPropertyNotError;
- (void) testHiddenFieldNotError;
- (void) testSoleArg;
- (void) testSoleArgUsingApplySyntax;
- (void) testUndefinedAttr;
- (void) testParallelAttributeIterationWithMissingArgs;
- (void) testStringTypeMismatch;
- (void) testStringTypeMismatch2;
@end
