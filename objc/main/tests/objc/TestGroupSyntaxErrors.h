#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "STErrorListener.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"

@interface TestGroupSyntaxErrors : BaseTest {
}

- (void) test01MissingImportString;
- (void) test02ImportNotString;
- (void) test03MissingTemplate;
- (void) test04UnclosedTemplate;
- (void) test05Paren;
- (void) test06NewlineInString;
- (void) test07Paren2;
- (void) test08Arg;
- (void) test09Arg2;
- (void) test10Arg3;
- (void) test11ErrorWithinTemplate;
- (void) test12Map;
- (void) test13Map2;
- (void) test14Map3;
- (void) test15UnterminatedString;
@end
