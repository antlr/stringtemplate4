#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "STErrorListener.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"

@interface TestGroupSyntaxErrors : SenTestCase {
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
