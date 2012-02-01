#import "TestDollarDelimiters.h"

@implementation TestDollarDelimiters

- (void) testAttr {
  NSString *template = @"hi $name$!";
  ST *st = [[[ST alloc] init:template arg1:'$' arg2:'$'] autorelease];
  [st add:@"name" value:@"Ter"];
  NSString *expected = @"hi Ter!";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testParallelMap {
  STGroup *group = [[[STGroup alloc] init:'$' arg1:'$'] autorelease];
  [group defineTemplate:@"test" arg1:@"names,phones" arg2:@"hi $names,phones:{n,p | $n$:$p$;}$"];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"names" value:@"Ter"];
  [st add:@"names" value:@"Tom"];
  [st add:@"names" value:@"Sumana"];
  [st add:@"phones" value:@"x5001"];
  [st add:@"phones" value:@"x5002"];
  [st add:@"phones" value:@"x5003"];
  NSString *expected = @"hi Ter:x5001;Tom:x5002;Sumana:x5003;";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testRefToAnotherTemplateInSameGroup {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << $b()$ >>\n";
  NSString *b = @"b() ::= <<bar>>\n";
  [self writeFile:dir arg1:@"a.st" arg2:a];
  [self writeFile:dir arg1:@"b.st" arg2:b];
  STGroup *group = [[[STGroupDir alloc] init:dir arg1:'$' arg2:'$'] autorelease];
  ST *st = [group getInstanceOf:@"a"];
  NSString *expected = @" bar ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testDefaultArgument {
  NSString *templates = [[[[@"method(name) ::= <<" stringByAppendingString:newline] stringByAppendingString:@"$stat(name)$"] + newline stringByAppendingString:@">>"] + newline stringByAppendingString:@"stat(name,value=\"99\") ::= \"x=$value$; // $name$\""] + newline;
  [self writeFile:tmpdir arg1:@"group.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/group.stg"] arg1:'$' arg2:'$'] autorelease];
  ST *b = [group getInstanceOf:@"method"];
  [b add:@"name" value:@"foo"];
  NSString *expecting = @"x=99; // foo";
  NSString *result = [b render];
  [self assertEquals:expecting arg1:result];
}

@end
