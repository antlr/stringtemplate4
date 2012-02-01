#import "TestSubtemplates.h"

@implementation TestSubtemplates_Anon1

- (void) init {
  if ( (self=[super init]) != nil ) {
    [self add:@"1"];
    [self add:nil];
    [self add:@"3"];
  }
  return self;
}

@end

@implementation TestSubtemplates

- (void) testSimpleIteration {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" arg1:@"names" arg2:@"<names:{n|<n>}>!"];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"names" arg1:@"Ter"];
  [st add:@"names" arg1:@"Tom"];
  [st add:@"names" arg1:@"Sumana"];
  NSString *expected = @"TerTomSumana!";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testMapIterationIsByKeys {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" arg1:@"emails" arg2:@"<emails:{n|<n>}>!"];
  ST *st = [group getInstanceOf:@"test"];
  NSMutableDictionary *emails = [[[LinkedHashMap alloc] init] autorelease];
  [emails setObject:@"parrt" arg1:@"Ter"];
  [emails setObject:@"tombu" arg1:@"Tom"];
  [emails setObject:@"dmose" arg1:@"Dan"];
  [st add:@"emails" arg1:emails];
  NSString *expected = @"parrttombudmose!";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testSimpleIterationWithArg {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" arg1:@"names" arg2:@"<names:{n | <n>}>!"];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"names" arg1:@"Ter"];
  [st add:@"names" arg1:@"Tom"];
  [st add:@"names" arg1:@"Sumana"];
  NSString *expected = @"TerTomSumana!";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testNestedIterationWithArg {
  STGroup *group = [[[STGroup alloc] init] autorelease];
  [group defineTemplate:@"test" arg1:@"users" arg2:@"<users:{u | <u.id:{id | <id>=}><u.name>}>!"];
  ST *st = [group getInstanceOf:@"test"];
  [st add:@"users" arg1:[[[User alloc] init:1 arg1:@"parrt"] autorelease]];
  [st add:@"users" arg1:[[[User alloc] init:2 arg1:@"tombu"] autorelease]];
  [st add:@"users" arg1:[[[User alloc] init:3 arg1:@"sri"] autorelease]];
  NSString *expected = @"1=parrt2=tombu3=sri!";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testSubtemplateAsDefaultArg {
  NSString *templates = [[[@"t(x,y={<x:{s|<s><s>}>}) ::= <<\n" stringByAppendingString:@"x: <x>\n"] stringByAppendingString:@"y: <y>\n"] stringByAppendingString:@">>"] + newline;
  [self writeFile:tmpdir arg1:@"group.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/group.stg"]] autorelease];
  ST *b = [group getInstanceOf:@"t"];
  [b add:@"x" arg1:@"a"];
  NSString *expecting = [[@"x: a" stringByAppendingString:newline] stringByAppendingString:@"y: aa"];
  NSString *result = [b render];
  [self assertEquals:expecting arg1:result];
}

- (void) testParallelAttributeIteration {
  ST *e = [[[ST alloc] init:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"] autorelease];
  [e add:@"names" arg1:@"Ter"];
  [e add:@"names" arg1:@"Tom"];
  [e add:@"phones" arg1:@"1"];
  [e add:@"phones" arg1:@"2"];
  [e add:@"salaries" arg1:@"big"];
  [e add:@"salaries" arg1:@"huge"];
  NSString *expecting = [[@"Ter@1: big" stringByAppendingString:newline] stringByAppendingString:@"Tom@2: huge"] + newline;
  [self assertEquals:expecting arg1:[e render]];
}

- (void) testParallelAttributeIterationWithNullValue {
  ST *e = [[[ST alloc] init:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"] autorelease];
  [e add:@"names" arg1:@"Ter"];
  [e add:@"names" arg1:@"Tom"];
  [e add:@"names" arg1:@"Sriram"];
  [e add:@"phones" arg1:[[[TestSubtemplates_Anon1 alloc] init] autorelease]];
  [e add:@"salaries" arg1:@"big"];
  [e add:@"salaries" arg1:@"huge"];
  [e add:@"salaries" arg1:@"enormous"];
  NSString *expecting = [[[@"Ter@1: big" stringByAppendingString:newline] stringByAppendingString:@"Tom@: huge"] + newline stringByAppendingString:@"Sriram@3: enormous"] + newline;
  [self assertEquals:expecting arg1:[e render]];
}

- (void) testParallelAttributeIterationHasI {
  ST *e = [[[ST alloc] init:@"<names,phones,salaries:{n,p,s | <i0>. <n>@<p>: <s>\n}>"] autorelease];
  [e add:@"names" arg1:@"Ter"];
  [e add:@"names" arg1:@"Tom"];
  [e add:@"phones" arg1:@"1"];
  [e add:@"phones" arg1:@"2"];
  [e add:@"salaries" arg1:@"big"];
  [e add:@"salaries" arg1:@"huge"];
  NSString *expecting = [[@"0. Ter@1: big" stringByAppendingString:newline] stringByAppendingString:@"1. Tom@2: huge"] + newline;
  [self assertEquals:expecting arg1:[e render]];
}

- (void) testParallelAttributeIterationWithDifferentSizes {
  ST *e = [[[ST alloc] init:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"] autorelease];
  [e add:@"names" arg1:@"Ter"];
  [e add:@"names" arg1:@"Tom"];
  [e add:@"names" arg1:@"Sriram"];
  [e add:@"phones" arg1:@"1"];
  [e add:@"phones" arg1:@"2"];
  [e add:@"salaries" arg1:@"big"];
  NSString *expecting = @"Ter@1: big, Tom@2: , Sriram@: ";
  [self assertEquals:expecting arg1:[e render]];
}

- (void) testParallelAttributeIterationWithSingletons {
  ST *e = [[[ST alloc] init:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"] autorelease];
  [e add:@"names" arg1:@"Ter"];
  [e add:@"phones" arg1:@"1"];
  [e add:@"salaries" arg1:@"big"];
  NSString *expecting = @"Ter@1: big";
  [self assertEquals:expecting arg1:[e render]];
}

- (void) testParallelAttributeIterationWithDifferentSizesTemplateRefInsideToo {
  NSString *templates = [[[@"page(names,phones,salaries) ::= " stringByAppendingString:newline] stringByAppendingString:@"	<< <names,phones,salaries:{n,p,s | <value(n)>@<value(p)>: <value(s)>}; separator=\", \"> >>"] + newline stringByAppendingString:@"value(x) ::= \"<if(!x)>n/a<else><x><endif>\""] + newline;
  [self writeFile:tmpdir arg1:@"g.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/g.stg"]] autorelease];
  ST *p = [group getInstanceOf:@"page"];
  [p add:@"names" arg1:@"Ter"];
  [p add:@"names" arg1:@"Tom"];
  [p add:@"names" arg1:@"Sriram"];
  [p add:@"phones" arg1:@"1"];
  [p add:@"phones" arg1:@"2"];
  [p add:@"salaries" arg1:@"big"];
  NSString *expecting = @" Ter@1: big, Tom@2: n/a, Sriram@n/a: n/a ";
  [self assertEquals:expecting arg1:[p render]];
}

- (void) testEvalSTIteratingSubtemplateInSTFromAnotherGroup {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroup *innerGroup = [[[STGroup alloc] init] autorelease];
  [innerGroup setListener:errors];
  [innerGroup defineTemplate:@"test" arg1:@"m" arg2:@"<m:samegroup()>"];
  [innerGroup defineTemplate:@"samegroup" arg1:@"x" arg2:@"hi "];
  ST *st = [innerGroup getInstanceOf:@"test"];
  [st add:@"m" arg1:[NSArray arrayWithObjects:1, 2, 3, nil]];
  STGroup *outerGroup = [[[STGroup alloc] init] autorelease];
  [outerGroup defineTemplate:@"errorMessage" arg1:@"x" arg2:@"<x>"];
  ST *outerST = [outerGroup getInstanceOf:@"errorMessage"];
  [outerST add:@"x" arg1:st];
  NSString *expected = @"hi hi hi ";
  NSString *result = [outerST render];
  [self assertEquals:[errors.errors size] arg1:0];
  [self assertEquals:expected arg1:result];
}

- (void) testEvalSTIteratingSubtemplateInSTFromAnotherGroupSingleValue {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroup *innerGroup = [[[STGroup alloc] init] autorelease];
  [innerGroup setListener:errors];
  [innerGroup defineTemplate:@"test" arg1:@"m" arg2:@"<m:samegroup()>"];
  [innerGroup defineTemplate:@"samegroup" arg1:@"x" arg2:@"hi "];
  ST *st = [innerGroup getInstanceOf:@"test"];
  [st add:@"m" arg1:10];
  STGroup *outerGroup = [[[STGroup alloc] init] autorelease];
  [outerGroup defineTemplate:@"errorMessage" arg1:@"x" arg2:@"<x>"];
  ST *outerST = [outerGroup getInstanceOf:@"errorMessage"];
  [outerST add:@"x" arg1:st];
  NSString *expected = @"hi ";
  NSString *result = [outerST render];
  [self assertEquals:[errors.errors size] arg1:0];
  [self assertEquals:expected arg1:result];
}

- (void) testEvalSTFromAnotherGroup {
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroup *innerGroup = [[[STGroup alloc] init] autorelease];
  [innerGroup setListener:errors];
  [innerGroup defineTemplate:@"bob" arg1:@"inner"];
  ST *st = [innerGroup getInstanceOf:@"bob"];
  STGroup *outerGroup = [[[STGroup alloc] init] autorelease];
  [outerGroup setListener:errors];
  [outerGroup defineTemplate:@"errorMessage" arg1:@"x" arg2:@"<x>"];
  [outerGroup defineTemplate:@"bob" arg1:@"outer"];
  ST *outerST = [outerGroup getInstanceOf:@"errorMessage"];
  [outerST add:@"x" arg1:st];
  NSString *expected = @"inner";
  NSString *result = [outerST render];
  [self assertEquals:[errors.errors size] arg1:0];
  [self assertEquals:expected arg1:result];
}

@end
