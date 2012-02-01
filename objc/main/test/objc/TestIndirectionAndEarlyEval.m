#import "TestIndirectionAndEarlyEval.h"

@implementation TestIndirectionAndEarlyEval

- (void) testEarlyEval {
  NSString *template = @"<(name)>";
  ST *st = [[[ST alloc] init:template] autorelease];
  [st add:@"name" arg1:@"Ter"];
  NSString *expected = @"Ter";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testIndirectTemplateInclude {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"foo" arg1:@"bar"];
  NSString *template = @"<(name)()>";
  [group defineTemplate:@"test" arg1:@"name" arg2:template];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"name" arg1:@"foo"];
  NSString *expected = @"bar";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testIndirectTemplateIncludeWithArgs {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"foo" arg1:@"x,y" arg2:@"<x><y>"];
  NSString *template = @"<(name)({1},{2})>";
  [group defineTemplate:@"test" arg1:@"name" arg2:template];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"name" arg1:@"foo"];
  NSString *expected = @"12";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testIndirectTemplateIncludeViaTemplate {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"foo" arg1:@"bar"];
  [group defineTemplate:@"tname" arg1:@"foo"];
  NSString *template = @"<(tname())()>";
  [group defineTemplate:@"test" arg1:@"name" arg2:template];
  ST *st = [group getInstanceOf:@"test"];
  NSString *expected = @"bar";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testIndirectProp {
  NSString *template = @"<u.(propname)>: <u.name>";
  ST *st = [[[ST alloc] init:template] autorelease];
  [st add:@"u" arg1:[[[User alloc] init:1 arg1:@"parrt"] autorelease]];
  [st add:@"propname" arg1:@"id"];
  NSString *expected = @"1: parrt";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testIndirectMap {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"a" arg1:@"x" arg2:@"[<x>]"];
  [group defineTemplate:@"test" arg1:@"names,templateName" arg2:@"hi <names:(templateName)()>!"];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"names" arg1:@"Ter"];
  [st add:@"names" arg1:@"Tom"];
  [st add:@"names" arg1:@"Sumana"];
  [st add:@"templateName" arg1:@"a"];
  NSString *expected = @"hi [Ter][Tom][Sumana]!";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testNonStringDictLookup {
  NSString *template = @"<m.(intkey)>";
  ST *st = [[[ST alloc] init:template] autorelease];
  NSMutableDictionary *m = [[[NSMutableDictionary alloc] init] autorelease];
  [m setObject:36 arg1:@"foo"];
  [st add:@"m" arg1:m];
  [st add:@"intkey" arg1:36];
  NSString *expected = @"foo";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

@end
