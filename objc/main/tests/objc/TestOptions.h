#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "ST.h"
#import "STGroup.h"
#import "ErrorBuffer.h"
#import "CompiledST.h"

@interface TestOptions : BaseTest {
}

- (void) test01Separator;
- (void) test02SeparatorWithSpaces;
- (void) test03AttrSeparator;
- (void) test04IncludeSeparator;
- (void) test05SubtemplateSeparator;
- (void) test06SeparatorWithNullFirstValueAndNullOption;
- (void) test07SeparatorWithNull2ndValueAndNullOption;
- (void) test08NullValueAndNullOption;
- (void) test09ListApplyWithNullValueAndNullOption;
- (void) test10DoubleListApplyWithNullValueAndNullOption;
- (void) test11MissingValueAndNullOption;
- (void) test12OptionDoesntApplyToNestedTemplate;
- (void) test13IllegalOption;
@end
