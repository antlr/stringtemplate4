#import "TestLists.h"

@implementation TestLists

- (void) testJustCat {
  ST * e = [[[ST alloc] init:@"<[names,phones]>"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"TerTom12";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testListLiteralWithEmptyElements {
  ST * e = [[[ST alloc] init:@"<[\"Ter\",,\"Jesse\"]:{n | <i>:<n>}; separator=\", \", null={foo}>"] autorelease];
  NSString * expecting = @"1:Ter, foo, 2:Jesse";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testListLiteralWithEmptyFirstElement {
  ST * e = [[[ST alloc] init:@"<[,\"Ter\",\"Jesse\"]:{n | <i>:<n>}; separator=\", \", null={foo}>"] autorelease];
  NSString * expecting = @"foo, 1:Ter, 2:Jesse";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testLength {
  ST * e = [[[ST alloc] init:@"<length([names,phones])>"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"4";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCat2Attributes {
  ST * e = [[[ST alloc] init:@"<[names,phones]; separator=\", \">"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"Ter, Tom, 1, 2";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCat2AttributesWithApply {
  ST * e = [[[ST alloc] init:@"<[names,phones]:{a|<a>.}>"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"Ter.Tom.1.2.";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCat3Attributes {
  ST * e = [[[ST alloc] init:@"<[names,phones,salaries]; separator=\", \">"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  [e add:@"salaries" param1:@"big"];
  [e add:@"salaries" param1:@"huge"];
  NSString * expecting = @"Ter, Tom, 1, 2, big, huge";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCatWithTemplateApplicationAsElement {
  ST * e = [[[ST alloc] init:@"<[names:{n|<n>!},phones]; separator=\", \">"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"Ter!, Tom!, 1, 2";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCatWithIFAsElement {
  ST * e = [[[ST alloc] init:@"<[{<if(names)>doh<endif>},phones]; separator=\", \">"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"doh, 1, 2";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCatNullValues {
  ST * e = [[[ST alloc] init:@"<[no,go]; null=\"foo\", separator=\", \">"] autorelease];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"foo, foo";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCatWithNullTemplateApplicationAsElement {
  ST * e = [[[ST alloc] init:@"<[names:{n|<n>!},\"foo\"]:{a|x}; separator=\", \">"] autorelease];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"x";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testCatWithNestedTemplateApplicationAsElement {
  ST * e = [[[ST alloc] init:@"<[names, [\"foo\",\"bar\"]:{x | <x>!},phones]; separator=\", \">"] autorelease];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"Ter, Tom, foo!, bar!, 1, 2";
  [self assertEquals:expecting param1:[e render]];
}

- (void) testListAsTemplateArgument {
  NSString * templates = [[@"test(names,phones) ::= \"<foo([names,phones])>\"" stringByAppendingString:newline] stringByAppendingString:@"foo(items) ::= \"<items:{a | *<a>*}>\""] + newline;
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]] autorelease];
  ST * e = [group getInstanceOf:@"test"];
  [e add:@"names" param1:@"Ter"];
  [e add:@"names" param1:@"Tom"];
  [e add:@"phones" param1:@"1"];
  [e add:@"phones" param1:@"2"];
  NSString * expecting = @"*Ter**Tom**1**2*";
  NSString * result = [e render];
  [self assertEquals:expecting param1:result];
}

@end
