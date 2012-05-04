#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "STErrorListener.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "STException.h"
#import "ErrorBuffer.h"

@interface TestSyntaxErrors : BaseTest {
}

- (void) test01EmptyExpr;
- (void) test02EmptyExpr2;
- (void) test03UnterminatedExpr;
- (void) test04WeirdChar;
- (void) test05WeirdChar2;
- (void) test06ValidButOutOfPlaceChar;
- (void) test07ValidButOutOfPlaceCharOnDifferentLine;
- (void) test08ErrorInNestedTemplate;
- (void) test09EOFInExpr;
- (void) test10EOFInExpr2;
- (void) test11EOFInString;
- (void) test12NonterminatedComment;
- (void) test13MissingRPAREN;
- (void) test14RotPar;
@end
