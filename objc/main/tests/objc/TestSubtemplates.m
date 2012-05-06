
#import "TestSubtemplates.h"
#import "STGroup.h"
#import "STGroupDir.h"
#import "STGroupFile.h"

@implementation TestSubtemplates_Anon1

- (id) init {
  if ( (self=[super init]) != nil ) {
    [self addObject:@"1"];
    [self addObject:nil];
    [self addObject:@"3"];
  }
  return self;
}

@end

@implementation TestSubtemplates

- (void) test01SimpleIteration
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n|<n>}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @"TerTomSumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test02MapIterationIsByKeys
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"emails" template:@"<emails:{n|<n>}>!"];
    ST *st = [group getInstanceOf:@"test"];
    LinkedHashMap *emails = [LinkedHashMap newLinkedHashMap:8];
    [emails put:@"parrt" value:@"Ter"];
    [emails put:@"tombu" value:@"Tom"];
    [emails put:@"dmose" value:@"Dan"];
    [st add:@"emails" value:emails];
    [st.impl dump];
    NSString *expected = @"parrttombudmose!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test03SimpleIterationWithArg
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n | <n>}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @"TerTomSumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test04NestedIterationWithArg
{
    STGroup *group = [STGroup newSTGroup];
    //    [group defineTemplate:@"test" argsS:@"users" template:@"<users:{u | <u.id:{id | <id>=}><u.name>}>!"];
    [group defineTemplate:@"test" argsS:@"users" template:@"<users:{u | <u.num:{num | <num>=}><u.name>}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"users" value:[User newUser:1 name:@"parrt"]];
    [st add:@"users" value:[User newUser:2 name:@"tombu"]];
    [st add:@"users" value:[User newUser:3 name:@"sri"]];
    NSString *expected = @"1=parrt2=tombu3=sri!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test05SubtemplateAsDefaultArg
{
    NSString *templates = @"t(x,y={<x:{s|<s><s>}>}) ::= <<\nx: <x>\ny: <y>\n>>\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/group.stg"]];
    ST *st = [group getInstanceOf:@"t"];
    [st add:@"x" value:@"a"];
    NSString *expected = @"x: a\ny: aa";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test06ParallelAttributeIteration
{
    ST *st = [ST newSTWithTemplate:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    [st add:@"salaries" value:@"big"];
    [st add:@"salaries" value:@"huge"];
    NSString *expected = @"Ter@1: big\nTom@2: huge\n";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test07ParallelAttributeIterationWithNullValue
{
    ST *st = [ST newSTWithTemplate:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>\n}>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    [st add:@"phones" value:[[TestSubtemplates_Anon1 alloc] init]];
    [st add:@"salaries" value:@"big"];
    [st add:@"salaries" value:@"huge"];
    [st add:@"salaries" value:@"enormous"];
    NSString *expected = @"Ter@1: big\nTom@: huge\nSriram@3: enormous\n";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test08ParallelAttributeIterationHasI
{
    ST *st = [ST newSTWithTemplate:@"<names,phones,salaries:{n,p,s | <i0>. <n>@<p>: <s>\n}>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    [st add:@"salaries" value:@"big"];
    [st add:@"salaries" value:@"huge"];
    NSString *expected = @"0. Ter@1: big\n1. Tom@2: huge\n";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test09ParallelAttributeIterationWithDifferentSizes
{
    ST *st = [ST newSTWithTemplate:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    [st add:@"salaries" value:@"big"];
    NSString *expected = @"Ter@1: big, Tom@2: , Sriram@: ";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test10ParallelAttributeIterationWithSingletons
{
    ST *st = [ST newSTWithTemplate:@"<names,phones,salaries:{n,p,s | <n>@<p>: <s>}; separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"phones" value:@"1"];
    [st add:@"salaries" value:@"big"];
    NSString *expected = @"Ter@1: big";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test11ParallelAttributeIterationWithDifferentSizesTemplateRefInsideToo
{
    NSString *templates = @"page(names,phones,salaries) ::= \n	<< <names,phones,salaries:{n,p,s | <value(n)>@<value(p)>: <value(s)>}; separator=\", \"> >>\nvalue(x) ::= \"<if(!x)>n/a<else><x><endif>\"\n";
    [self writeFile:tmpdir fileName:@"g.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/g.stg"]];
    ST *st = [group getInstanceOf:@"page"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    [st add:@"salaries" value:@"big"];
    NSString *expected = @" Ter@1: big, Tom@2: n/a, Sriram@n/a: n/a ";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test12EvalSTIteratingSubtemplateInSTFromAnotherGroup
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *innerGroup = [STGroup newSTGroup];
    [innerGroup setListener:errors];
    [innerGroup defineTemplate:@"test" argsS:@"m" template:@"<m:samegroup()>"];
    [innerGroup defineTemplate:@"samegroup" argsS:@"x" template:@"hi "];
    ST *st = [innerGroup getInstanceOf:@"test"];
    [st add:@"m" value:[AMutableArray arrayWithObjects:[ACNumber numberWithInteger:1], [ACNumber numberWithInteger:2], [ACNumber numberWithInteger:3], nil]];
    STGroup *outerGroup = [STGroup newSTGroup];
    [outerGroup defineTemplate:@"errorMessage" argsS:@"x" template:@"<x>"];
    ST *outerST = [outerGroup getInstanceOf:@"errorMessage"];
    [outerST add:@"x" value:st];
    NSString *expected = @"hi hi hi ";
    NSString *result = [outerST render];
    [self assertEquals:expected result:result];
    expected = @"[errors.errors count] == 0";
    result = [NSString stringWithFormat:@"[errors.errors count] == %d", [errors.errors count]];
    [self assertEquals:expected result:result];
}

- (void) test13EvalSTIteratingSubtemplateInSTFromAnotherGroupSingleValue
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *innerGroup = [STGroup newSTGroup];
    [innerGroup setListener:errors];
    [innerGroup defineTemplate:@"test" argsS:@"m" template:@"<m:samegroup()>"];
    [innerGroup defineTemplate:@"samegroup" argsS:@"x" template:@"hi "];
    ST *st = [innerGroup getInstanceOf:@"test"];
    [st addInt:@"m" value:10];
    STGroup *outerGroup = [STGroup newSTGroup];
    [outerGroup defineTemplate:@"errorMessage" argsS:@"x" template:@"<x>"];
    ST *outerST = [outerGroup getInstanceOf:@"errorMessage"];
    [outerST add:@"x" value:st];
    NSString *expected = @"hi ";
    NSString *result = [outerST render];
    [self assertEquals:expected result:result];
    expected = @"[errors.errors count] == 0";
    result = [NSString stringWithFormat:@"[errors.errors count] == %d", [errors.errors count]];
    [self assertEquals:expected result:result];
}

- (void) test14EvalSTFromAnotherGroup
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *innerGroup = [STGroup newSTGroup];
    [innerGroup setListener:errors];
    [innerGroup defineTemplate:@"bob" template:@"inner"];
    ST *st = [innerGroup getInstanceOf:@"bob"];
    STGroup *outerGroup = [STGroup newSTGroup];
    [outerGroup setListener:errors];
    [outerGroup defineTemplate:@"errorMessage" argsS:@"x" template:@"<x>"];
    [outerGroup defineTemplate:@"bob" template:@"outer"];
    ST *outerST = [outerGroup getInstanceOf:@"errorMessage"];
    [outerST add:@"x" value:st];
    NSString *expected = @"inner";
    NSString *result = [outerST render];
    [self assertEquals:expected result:result];
    expected = @"[errors.errors count] == 0";
    result = [NSString stringWithFormat:@"[errors.errors count] == %d", [errors.errors count]];
    [self assertEquals:expected result:result];
}

@end
