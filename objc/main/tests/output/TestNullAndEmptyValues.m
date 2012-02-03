#import "TestNullAndEmptyValues.h"

@implementation TestNullAndEmptyValues_Anon1

- (void) init {
  if (self = [super init]) {
    [self put:@"foo" param1:nil];
  }
  return self;
}

@end

@implementation TestNullAndEmptyValues

- (void) testSeparatorWithNullFirstValue {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"hi <name; separator=\", \">!"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:@"Tom"];
  [st add:@"name" param1:@"Sumana"];
  NSString * expected = @"hi Tom, Sumana!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testTemplateAppliedToNullIsEmpty {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"<name:t()>"];
  [group defineTemplate:@"t" param1:@"x" param2:@"<x>"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:nil];
  NSString * expected = @"";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testTemplateAppliedToMissingValueIsEmpty {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"<name:t()>"];
  [group defineTemplate:@"t" param1:@"x" param2:@"<x>"];
  ST * st = [group getInstanceOf:@"test"];
  NSString * expected = @"";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testSeparatorWithNull2ndValue {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"hi <name; separator=\", \">!"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:@"Ter"];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:@"Sumana"];
  NSString * expected = @"hi Ter, Sumana!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testSeparatorWithNullLastValue {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"hi <name; separator=\", \">!"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:@"Ter"];
  [st add:@"name" param1:@"Tom"];
  [st add:@"name" param1:nil];
  NSString * expected = @"hi Ter, Tom!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testSeparatorWithTwoNullValuesInRow {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"hi <name; separator=\", \">!"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:@"Ter"];
  [st add:@"name" param1:@"Tom"];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:@"Sri"];
  NSString * expected = @"hi Ter, Tom, Sri!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testTwoNullValues {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"hi <name; null=\"x\">!"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:nil];
  NSString * expected = @"hi xx!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testNullListItemNotCountedForIteratorIndex {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"name" param2:@"<name:{n | <i>:<n>}>"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:@"Ter"];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:nil];
  [st add:@"name" param1:@"Jesse"];
  NSString * expected = @"1:Ter2:Jesse";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testSizeZeroButNonNullListGetsNoOutput {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"users" param2:[[@"begin\n" stringByAppendingString:@"<users>\n"] stringByAppendingString:@"end\n"]];
  ST * t = [group getInstanceOf:@"test"];
  [t add:@"users" param1:nil];
  NSString * expecting = [[@"begin" stringByAppendingString:newline] stringByAppendingString:@"end"];
  NSString * result = [t render];
  [self assertEquals:expecting param1:result];
}

- (void) testNullListGetsNoOutput {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"users" param2:[[@"begin\n" stringByAppendingString:@"<users:{u | name: <u>}; separator=\", \">\n"] stringByAppendingString:@"end\n"]];
  ST * t = [group getInstanceOf:@"test"];
  NSString * expecting = [[@"begin" stringByAppendingString:newline] stringByAppendingString:@"end"];
  NSString * result = [t render];
  [self assertEquals:expecting param1:result];
}

- (void) testEmptyListGetsNoOutput {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"users" param2:[[@"begin\n" stringByAppendingString:@"<users:{u | name: <u>}; separator=\", \">\n"] stringByAppendingString:@"end\n"]];
  ST * t = [group getInstanceOf:@"test"];
  [t add:@"users" param1:[[[NSMutableArray alloc] init] autorelease]];
  NSString * expecting = [[@"begin" stringByAppendingString:newline] stringByAppendingString:@"end"];
  NSString * result = [t render];
  [self assertEquals:expecting param1:result];
}

- (void) testMissingDictionaryValue {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"m" param2:@"<m.foo>"];
  ST * t = [group getInstanceOf:@"test"];
  [t add:@"m" param1:[[[NSMutableDictionary alloc] init] autorelease]];
  NSString * expecting = @"";
  NSString * result = [t render];
  [self assertEquals:expecting param1:result];
}

- (void) testMissingDictionaryValue2 {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"m" param2:@"<if(m.foo)>[<m.foo>]<endif>"];
  ST * t = [group getInstanceOf:@"test"];
  [t add:@"m" param1:[[[NSMutableDictionary alloc] init] autorelease]];
  NSString * expecting = @"";
  NSString * result = [t render];
  [self assertEquals:expecting param1:result];
}

- (void) testMissingDictionaryValue3 {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" param1:@"m" param2:@"<if(m.foo)>[<m.foo>]<endif>"];
  ST * t = [group getInstanceOf:@"test"];
  [t add:@"m" param1:[[[TestNullAndEmptyValues_Anon1 alloc] init] autorelease]];
  NSString * expecting = @"";
  NSString * result = [t render];
  [self assertEquals:expecting param1:result];
}

- (void) TestSeparatorEmittedForEmptyIteratorValue {
  ST * st = [[[ST alloc] init:@"<values:{v|<if(v)>x<endif>}; separator=\" \">"] autorelease];
  [st add:@"values" param1:[NSArray arrayWithObjects:YES, NO, YES, nil]];
  StringWriter * sw = [[[StringWriter alloc] init] autorelease];
  [st write:[[[AutoIndentWriter alloc] init:sw] autorelease]];
  NSString * result = [sw description];
  NSString * expecting = @"x  x";
  [self assertEquals:expecting param1:result];
}

- (void) TestSeparatorEmittedForEmptyIteratorValue2 {
  ST * st = [[[ST alloc] init:@"<values; separator=\" \">"] autorelease];
  [st add:@"values" param1:[NSArray arrayWithObjects:@"x", @"", @"y", nil]];
  StringWriter * sw = [[[StringWriter alloc] init] autorelease];
  [st write:[[[AutoIndentWriter alloc] init:sw] autorelease]];
  NSString * result = [sw description];
  NSString * expecting = @"x  y";
  [self assertEquals:expecting param1:result];
}

@end
