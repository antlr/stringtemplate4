#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"

@interface TestLists : BaseTest {
}

- (void) test01JustCat;
- (void) test02ListLiteralWithEmptyElements;
- (void) test03ListLiteralWithEmptyFirstElement;
- (void) test04Length;
- (void) test05Cat2Attributes;
- (void) test06Cat2AttributesWithApply;
- (void) test07Cat3Attributes;
- (void) test08CatWithTemplateApplicationAsElement;
- (void) test09CatWithIFAsElement;
- (void) test10CatNullValues;
- (void) test11CatWithNullTemplateApplicationAsElement;
- (void) test12CatWithNestedTemplateApplicationAsElement;
- (void) test13ListAsTemplateArgument;
@end
