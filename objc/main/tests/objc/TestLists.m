#import "TestLists.h"

@implementation TestLists

- (void) test01JustCat {
    ST *st = [ST newSTWithTemplate:@"<[names,phones]>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"TerTom12";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test02ListLiteralWithEmptyElements {
    ST *st = [ST newSTWithTemplate:@"<[\"Ter\",,\"Jesse\"]:{n | <i>:<n>}; separator=\", \", null={foo}>"];
    [st.impl dump];
    NSString *expected = @"1:Ter, foo, 2:Jesse";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test03ListLiteralWithEmptyFirstElement {
    ST *st = [ST newSTWithTemplate:@"<[,\"Ter\",\"Jesse\"]:{n | <i>:<n>}; separator=\", \", null={foo}>"];
    NSString *expected = @"foo, 1:Ter, 2:Jesse";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test04Length {
    ST *st = [ST newSTWithTemplate:@"<length([names,phones])>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"4";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test05Cat2Attributes {
    ST *st = [ST newSTWithTemplate:@"<[names,phones]; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"Ter, Tom, 1, 2";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test06Cat2AttributesWithApply {
    ST *st = [ST newSTWithTemplate:@"<[names,phones]:{a|<a>.}>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"Ter.Tom.1.2.";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test07Cat3Attributes {
    ST *st = [ST newSTWithTemplate:@"<[names,phones,salaries]; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    [st add:@"salaries" value:@"big"];
    [st add:@"salaries" value:@"huge"];
    NSString *expected = @"Ter, Tom, 1, 2, big, huge";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test08CatWithTemplateApplicationAsElement {
    ST *st = [ST newSTWithTemplate:@"<[names:{n|<n>!},phones]; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"Ter!, Tom!, 1, 2";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test09CatWithIFAsElement {
    ST *st = [ST newSTWithTemplate:@"<[{<if(names)>doh<endif>},phones]; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"doh, 1, 2";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test10CatNullValues {
    ST *st = [ST newSTWithTemplate:@"<[no,go]; null=\"foo\", separator=\", \">"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"foo, foo";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test11CatWithNullTemplateApplicationAsElement {
    ST *st = [ST newSTWithTemplate:@"<[names:{n|<n>!},\"foo\"]:{a|x}; separator=\", \">"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"x";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test12CatWithNestedTemplateApplicationAsElement {
    ST *st = [ST newSTWithTemplate:@"<[names, [\"foo\",\"bar\"]:{x | <x>!},phones]; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"Ter, Tom, foo!, bar!, 1, 2";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test13ListAsTemplateArgument {
    NSString *templates = @"test(names,phones) ::= \"<foo([names,phones])>\"\nfoo(items) ::= \"<items:{a | *<a>*}>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"*Ter**Tom**1**2*";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

@end
