#import "TestIndirectionAndEarlyEval.h"

@implementation TestIndirectionAndEarlyEval

- (void) testEarlyEval {
  NSString * template = @"<(name)>";
  ST * st = [[[ST alloc] init:template] autorelease];
  [st add:@"name" param1:@"Ter"];
  NSString * expected = @"Ter";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testIndirectTemplateInclude {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"foo" param1:@"bar"];
  NSString * template = @"<(name)()>";
  [group defineTemplate:@"test" param1:@"name" param2:template];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:@"foo"];
  NSString * expected = @"bar";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testIndirectTemplateIncludeWithArgs {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"foo" param1:@"x,y" param2:@"<x><y>"];
  NSString * template = @"<(name)({1},{2})>";
  [group defineTemplate:@"test" param1:@"name" param2:template];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"name" param1:@"foo"];
  NSString * expected = @"12";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testIndirectTemplateIncludeViaTemplate {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"foo" param1:@"bar"];
  [group defineTemplate:@"tname" param1:@"foo"];
  NSString * template = @"<(tname())()>";
  [group defineTemplate:@"test" param1:@"name" param2:template];
  ST * st = [group getInstanceOf:@"test"];
  NSString * expected = @"bar";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testIndirectProp {
  NSString * template = @"<u.(propname)>: <u.name>";
  ST * st = [[[ST alloc] init:template] autorelease];
  [st add:@"u" param1:[[[User alloc] init:1 param1:@"parrt"] autorelease]];
  [st add:@"propname" param1:@"id"];
  NSString * expected = @"1: parrt";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testIndirectMap {
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"a" param1:@"x" param2:@"[<x>]"];
  [group defineTemplate:@"test" param1:@"names,templateName" param2:@"hi <names:(templateName)()>!"];
  ST * st = [group getInstanceOf:@"test"];
  [st add:@"names" param1:@"Ter"];
  [st add:@"names" param1:@"Tom"];
  [st add:@"names" param1:@"Sumana"];
  [st add:@"templateName" param1:@"a"];
  NSString * expected = @"hi [Ter][Tom][Sumana]!";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testNonStringDictLookup {
  NSString * template = @"<m.(intkey)>";
  ST * st = [[[ST alloc] init:template] autorelease];
  NSMutableDictionary * m = [[[NSMutableDictionary alloc] init] autorelease];
  [m setObject:36 param1:@"foo"];
  [st add:@"m" param1:m];
  [st add:@"intkey" param1:36];
  NSString * expected = @"foo";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

@end
