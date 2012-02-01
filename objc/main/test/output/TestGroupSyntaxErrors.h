#import "Test.h"
#import "STErrorListener.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"
#import "Assert.h"

@interface TestGroupSyntaxErrors : BaseTest {
}

- (void) testMissingImportString;
- (void) testImportNotString;
- (void) testMissingTemplate;
- (void) testUnclosedTemplate;
- (void) testParen;
- (void) testNewlineInString;
- (void) testParen2;
- (void) testArg;
- (void) testArg2;
- (void) testArg3;
- (void) testErrorWithinTemplate;
- (void) testMap;
- (void) testMap2;
- (void) testMap3;
- (void) testUnterminatedString;
@end
