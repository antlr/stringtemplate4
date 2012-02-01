#import "BaseTest.h"
#import "TestLists.h"

@implementation TestLists

- (void) test01JustCat {
  ST *e = [ST newSTWithTemplate:@"<[names,phones]>"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"TerTom12";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test02ListLiteralWithEmptyElements {
  ST *e = [ST newSTWithTemplate:@"<[\"Ter\",,\"Jesse\"]:{n | <i>:<n>}; separator=\", \", null={foo}>"];
  NSString *expected = @"1:Ter, foo, 2:Jesse";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test03ListLiteralWithEmptyFirstElement {
  ST *e = [ST newSTWithTemplate:@"<[,\"Ter\",\"Jesse\"]:{n | <i>:<n>}; separator=\", \", null={foo}>"];
  NSString *expected = @"foo, 1:Ter, 2:Jesse";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test04Length {
  ST *e = [ST newSTWithTemplate:@"<length([names,phones])>"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"4";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test05Cat2Attributes {
  ST *e = [ST newSTWithTemplate:@"<[names,phones]; separator=\", \">"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"Ter, Tom, 1, 2";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test06Cat2AttributesWithApply {
  ST *e = [ST newSTWithTemplate:@"<[names,phones]:{a|<a>.}>"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"Ter.Tom.1.2.";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test07Cat3Attributes {
  ST *e = [ST newSTWithTemplate:@"<[names,phones,salaries]; separator=\", \">"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  [e add:@"salaries" value:@"big"];
  [e add:@"salaries" value:@"huge"];
  NSString *expected = @"Ter, Tom, 1, 2, big, huge";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test08CatWithTemplateApplicationAsElement {
  ST *e = [ST newSTWithTemplate:@"<[names:{n|<n>!},phones]; separator=\", \">"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"Ter!, Tom!, 1, 2";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test09CatWithIFAsElement {
  ST *e = [ST newSTWithTemplate:@"<[{<if(names)>doh<endif>},phones]; separator=\", \">"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"doh, 1, 2";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test10CatNullValues {
  ST *e = [ST newSTWithTemplate:@"<[no,go]; null=\"foo\", separator=\", \">"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"foo, foo";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test11CatWithNullTemplateApplicationAsElement {
  ST *e = [ST newSTWithTemplate:@"<[names:{n|<n>!},\"foo\"]:{a|x}; separator=\", \">"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"x";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test12CatWithNestedTemplateApplicationAsElement {
  ST *e = [ST newSTWithTemplate:@"<[names, [\"foo\",\"bar\"]:{x | <x>!},phones]; separator=\", \">"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"Ter, Tom, foo!, bar!, 1, 2";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test13ListAsTemplateArgument {
  NSString *templates = @"test(names,phones) ::= \"<foo([names,phones])>\"\nfoo(items) ::= \"<items:{a | *<a>*}>\"\n";
  [BaseTest writeFile:tmpdir fileName:@"t.stg" content:templates];
  STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
  ST *e = [group getInstanceOf:@"test"];
  [e add:@"names" value:@"Ter"];
  [e add:@"names" value:@"Tom"];
  [e add:@"phones" value:@"1"];
  [e add:@"phones" value:@"2"];
  NSString *expected = @"*Ter**Tom**1**2*";
  NSString *result = [e render];
  STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

@end
