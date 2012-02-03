#import "TestDollarDelimiters.h"

@implementation TestDollarDelimiters

- (void) testAttr {
  NSString * template = @"hi $name$!";
  ST * st = [[[ST alloc] init:template param1:'$' param2:'$'] autorelease];
  [st add:@"name" param1:@"Ter"];
  NSString * expected = @"hi Ter!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testParallelMap {
  STGroup * group = [[[STGroup alloc] init:'$' param1:'$'] autorelease];
  [group defineTemplate:@"test" param1:@"names,phones" param2:@"hi $names,phones:{n,p | $n$:$p$;}$"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"names" param1:@"Ter"];
  [st add:@"names" param1:@"Tom"];
  [st add:@"names" param1:@"Sumana"];
  [st add:@"phones" param1:@"x5001"];
  [st add:@"phones" param1:@"x5002"];
  [st add:@"phones" param1:@"x5003"];
  NSString * expected = @"hi Ter:x5001;Tom:x5002;Sumana:x5003;";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testRefToAnotherTemplateInSameGroup {
  NSString * dir = [self randomDir];
  NSString * a = @"a() ::= << $b()$ >>\n";
  NSString * b = @"b() ::= <<bar>>\n";
  [self writeFile:dir param1:@"a.st" param2:a];
  [self writeFile:dir param1:@"b.st" param2:b];
  STGroup * group = [[[STGroupDir alloc] init:dir param1:'$' param2:'$'] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = @" bar ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testDefaultArgument {
  NSString * templates = [[[[@"method(name) ::= <<" stringByAppendingString:newline] stringByAppendingString:@"$stat(name)$"] + newline stringByAppendingString:@">>"] + newline stringByAppendingString:@"stat(name,value=\"99\") ::= \"x=$value$; // $name$\""] + newline;
  [self writeFile:tmpdir param1:@"group.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/group.stg"] param1:'$' param2:'$'] autorelease];
  ST * b = [group getInstanceOf:@"method"];
  [b add:@"name" param1:@"foo"];
  NSString * expecting = @"x=99; // foo";
  NSString * result = [b render];
  [self assertEquals:expecting param1:result];
}

@end
