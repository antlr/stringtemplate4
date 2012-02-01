#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "STErrorListener.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "STException.h"
#import "ErrorBuffer.h"

@interface TestSyntaxErrors : SenTestCase {
}

- (void) testEmptyExpr;
- (void) testEmptyExpr2;
- (void) testUnterminatedExpr;
- (void) testWeirdChar;
- (void) testWeirdChar2;
- (void) testValidButOutOfPlaceChar;
- (void) testValidButOutOfPlaceCharOnDifferentLine;
- (void) testErrorInNestedTemplate;
- (void) testEOFInExpr;
- (void) testEOFInExpr2;
- (void) testEOFInString;
- (void) testNonterminatedComment;
- (void) testMissingRPAREN;
- (void) testRotPar;
@end
