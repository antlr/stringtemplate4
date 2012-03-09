#import "TestGroups.h"
#import "STGroupDir.h"
#import "STGroupString.h"
#import "STGroupFile.h"

@implementation Field_anon1

- (id) init
{
    if ( (self=[super init]) != nil ) {
        name = @"parrt";
        n = 0;
    }
    return self;
}

- (NSString *) description
{
    return @"Field";
}

- (NSString *) toString
{
    return [self description];
}

- (void) dealloc
{
    [name release];
    [super dealloc];
}

@end

@implementation Field_anon2

- (id) init
{
    if ( (self=[super init]) != nil ) {
        name = @"parrt";
        n = 0;
    }
    return self;
}

- (NSString *) description
{
    return @"Field";
}

- (NSString *) toString
{
    return [self description];
}

- (void) dealloc
{
    [name release];
    [super dealloc];
}

@end

@implementation Field_anon3

- (id) init
{
    if ( (self=[super init]) != nil ) {
        name = @"parrt";
        n = 0;
    }
    return self;
}

- (NSString *) description
{
    return @"Field";
}

- (NSString *) toString
{
    return [self description];
}

- (void) dealloc
{
    [name release];
    [super dealloc];
}

@end

@implementation Counter

- (id) init
{
    if ( (self=[super init]) != nil ) {
        n = 0;
    }
    return self;
}

- (NSString *) description
{
    return [NSString stringWithFormat:@"%d", n++];
}

- (NSString *) toString
{
    return [self description];
}

@end

@implementation TestGroups

- (void) test01SimpleGroup
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test02SimpleGroupFromString
{
    NSString *g = @"a(x) ::= <<foo>>\nb() ::= <<bar>>\n";
    STGroup *group = [STGroupString newSTGroupString:g];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test03GroupWithTwoTemplates
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = @"b() ::= \"bar\"\n";
    [self writeFile:dir fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"b"];
    NSString *expected = @"foobar";
    NSString *result = [NSString stringWithFormat:@"%@%@", [st1 render], [st2 render]];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test04Subdir
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = @"b() ::= \"bar\"\n";
    [self writeFile:[NSString stringWithFormat:@"%@/subdir", dir] fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"subdir/b"];
    NSString *expected = @"foobar";
    NSString *result = [NSString stringWithFormat:@"%@%@", [st1 render], [st2 render]];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    st2 = [group getInstanceOf:@"subdir/b"];
    expected = @"bar";
    result = [st2 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test05SubdirWithSubtemplate
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= \"<x:{y|<y>}>\"";
    [self writeFile:[NSString stringWithFormat:@"%@/subdir", dir] fileName:@"a.st" content:a];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"/subdir/a"];
    //[st add:@"x" value:[AttributeList arrayWithObjects:@"a", @"b", nil]];
    [[st add:@"x" value:@"a"] add:@"x" value:@"b"];
    NSString *expected = @"ab";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) test06GroupFileInDir
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *groupFile = @"b() ::= \"bar\"\nc() ::= \"duh\"\n";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"group/b"];
    ST *st3 = [group getInstanceOf:@"group/c"];
    NSString *expected = @"foobarduh";
    NSString *result = [NSString stringWithFormat:@"%@%@%@", [st1 render], [st2 render], [st3 render]];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test07SubSubdir
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = @"b() ::= \"bar\"\n";
    [self writeFile:[dir stringByAppendingString:@"/sub1/sub2"] fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"sub1/sub2/b"];
    NSString *expected = @"foobar";
    NSString *result = [NSString stringWithFormat:@"%@%@", [st1 render], [st2 render]];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test08GroupFileInSubDir
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *groupFile = [@"b() ::= \"bar\"\n" stringByAppendingString:@"c() ::= \"duh\"\n"];
    [self writeFile:dir fileName:@"subdir/group.stg" content:groupFile];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"subdir/group/b"];
    ST *st3 = [group getInstanceOf:@"subdir/group/c"];
    NSString *expected = @"foobarduh";
    NSString *result = [NSString stringWithFormat:@"%@%@%@", [st1 render], [st2 render], [st3 render]];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test09RefToAnotherTemplateInSameGroup
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a() ::= << <b()> >>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    [self writeFile:dir fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test10RefToAnotherTemplateInSameSubdir
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a() ::= << <subdir/b()> >>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:[dir stringByAppendingString:@"/subdir"] fileName:@"a.st" content:a];
    [self writeFile:[dir stringByAppendingString:@"/subdir"] fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"subdir/a"];
    [st.impl dump];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test11DupDef
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"b() ::= \"bar\"\nb() ::= \"duh\"\n";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    id<STErrorListener>errors = [ErrorBuffer newErrorBuffer];
    STGroupFile *group = [[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]];
    [group setListener:errors];
    [group load];
    NSString *expected = @"group.stg 2:0: redefinition of template b\n";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test12Alias
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"a() ::= \"bar\"\nb ::= a\n";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [STGroupFile newSTGroupFile:[dir stringByAppendingString:@"/group.stg"]];
    ST *st = [group getInstanceOf:@"b"];
    NSString *expected = @"bar";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test13AliasWithArgs
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"a(x,y) ::= \"<x><y>\"\nb ::= a\n";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [STGroupFile newSTGroupFile:[dir stringByAppendingString:@"/group.stg"]];
    ST *st = [group getInstanceOf:@"b"];
    [st addInt:@"x" value:1];
    [st addInt:@"y" value:2];
    NSString *expected = @"12";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test14SimpleDefaultArg
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a() ::= << <b()> >>\n";
    NSString *b = @"b(x=\"foo\") ::= \"<x>\"\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    [self writeFile:dir fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @" foo ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test15DefaultArgument
{
    NSString *templates = @"method(name) ::= <<\n<stat(name)>\n>>\nstat(name,value=\"99\") ::= \"x=<value>; // <name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"name" value:@"foo"];
    NSString *expected = @"x=99; // foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test16BooleanDefaultArguments
{
    NSString *templates = @"method(name) ::= <<\n<stat(name)>\n>>\nstat(name,x=true,y=false) ::= \"<name>; <x> <y>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"name" value:@"foo"];
    NSString *expected = @"foo; true false";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test17DefaultArgument2
{
    NSString *templates = @"stat(name,value=\"99\") ::= \"x=<value>; // <name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"stat"];
    [st add:@"name" value:@"foo"];
    NSString *expected = @"x=99; // foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test18SubtemplateAsDefaultArgSeesOtherArgs
{
    NSString *templates =@"t(x,y={<x:{s|<s><z>}>},z=\"foo\") ::= <<\nx: <x>\ny: <y>\n>>\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"t"];
    [st add:@"x" value:@"a"];
    NSString *expected = @"x: a\ny: afoo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test19DefaultArgumentAsSimpleTemplate
{
    NSString *templates = @"stat(name,value={99}) ::= \"x=<value>; // <name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"stat"];
    [b add:@"name" value:@"foo"];
    NSString *expected = @"x=99; // foo";
    NSString *result = [b render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test20DefaultArgumentManuallySet
{
    NSString *templates = @"method(fields) ::= <<\n<fields:{f | <stat(f)>}>\n>>\nstat(f,value={<f.name>}) ::= \"x=<value>; // <f.name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"fields" value:[[[Field_anon1 alloc] init] autorelease]];
    NSString *expected = @"x=parrt; // parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test21DefaultArgumentSeesVarFromDynamicScoping
{
    NSString *templates = @"method(fields) ::= <<\n<fields:{f | <stat()>}>\n>>\nstat(value={<f.name>}) ::= \"x=<value>; // <f.name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"fields" value:[[Field_anon2 alloc] init]];
    NSString *expected = @"x=parrt; // parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test22DefaultArgumentImplicitlySet2
{
    NSString *templates = @"method(fields) ::= <<\n<fields:{f | <f:stat()>}>\n>>\nstat(f,value={<f.name>}) ::= \"x=<value>; // <f.name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"fields" value:[[Field_anon3 alloc] init]];
    NSString *expected = @"x=parrt; // parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test23DefaultArgumentAsTemplate
{
    NSString *templates = @"method(name,size) ::= <<\n<stat(name)>\n>>\nstat(name,value={<name>}) ::= \"x=<value>; // <name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"name" value:@"foo"];
    [st add:@"size" value:@"2"];
    NSString *expected = @"x=foo; // foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test24DefaultArgumentAsTemplate2
{
    NSString *templates = @"method(name,size) ::= <<\n<stat(name)>\n>>\nstat(name,value={ [<name>] }) ::= \"x=<value>; // <name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"name" value:@"foo"];
    [st add:@"size" value:@"2"];
    NSString *expected = @"x=[foo] ; // foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test25DoNotUseDefaultArgument
{
    NSString *templates = @"method(name) ::= <<\n<stat(name,\"34\")>\n>>\nstat(name,value=\"99\") ::= \"x=<value>; // <name>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"method"];
    [st add:@"name" value:@"foo"];
    NSString *expected = @"x=34; // foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test26DefaultArgumentInParensToEvalEarly
{
    NSString *templates = @"A(x) ::= \"<B()>\"\nB(y={<(x)>}) ::= \"<y> <x> <x> <y>\"\n";
    [self writeFile:tmpdir fileName:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"A"];
    [st add:@"x" value:[[Counter alloc] init]];
    NSString *expected = @"0 1 2 0";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test27TrueFalseArgs
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(true,{a})>\"";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]];
    ST *st = [group getInstanceOf:@"g"];
    NSString *expected = @"truea";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test28NamedArgsInOrder
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(x={a},y={b})>\"";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]];
    ST *st = [group getInstanceOf:@"g"];
    NSString *expected = @"ab";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test29NamedArgsOutOfOrder
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(y={b},x={a})>\"";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]];
    ST *st = [group getInstanceOf:@"g"];
    NSString *expected = @"ab";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test30UnknownNamedArg
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(x={a},z={b})>\"";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    ST *st = [group getInstanceOf:@"g"];
    [st render];
    NSString *expected = @"context [g] 1:1 attribute z isn't defined\n";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test31MissingNamedArg
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(x={a},{b})>\"";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    [group load];
    NSString *expected = @"group.stg 2:28: mismatched input '{' expecting ID\n";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test32NamedArgsNotAllowInIndirectInclude
{
    NSString *dir = [self getRandomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng(name) ::= \"<(name)(x={a},y={b})>\"";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    [group load];
    NSString *expected = @"group.stg 2:22: '=' came as a complete surprise to me\n";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test33CantSeeGroupDirIfGroupFileOfSameName
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a() ::= <<dir1 a>>\n";
    [self writeFile:dir fileName:@"group/a.st" content:a];
    NSString *groupFile = @"b() ::= \"group file b\"\n";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroup *group1 = [[STGroupDir alloc] init:dir];
    ST *st = [group1 getInstanceOf:@"group/a"];
    STAssertTrue( (st == nil), @"Expected nil BUT GOT \"%@\"", [st description] );
    return;
}

- (void) test34FullyQualifiedGetInstanceOf
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test35FullyQualifiedTemplateRef
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a() ::= << <subdir/b()> >>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:[dir stringByAppendingString:@"/subdir"] fileName:@"a.st" content:a];
    [self writeFile:[dir stringByAppendingString:@"/subdir"] fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"subdir/a"];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test36FullyQualifiedTemplateRef2
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= << <group/b()> >>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *groupFile = @"b() ::= \"bar\"\nc() ::= \"<a()>\"\n";
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"group/c"];
    NSString *expected = @" bar  bar ";
    NSString *result = [NSString stringWithFormat:@"%@%@", [st1 render], [st2 render]];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test37UnloadingSimpleGroup
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<foo>>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    [self writeFile:dir fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    [group load];
    ST *st = [group getInstanceOf:@"a"];
    NSInteger originalHashCode = (NSInteger)st;
    [group unload];
    st = [group getInstanceOf:@"a"];
    NSInteger newHashCode = (NSInteger)st;
    STAssertTrue( (originalHashCode == newHashCode), @"Expected \"YES\" BUT GOT \"NO\"" );
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    st = [group getInstanceOf:@"b"];
    expected = @"bar";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

- (void) test38UnloadingGroupFile
{
    NSString *dir = [self getRandomDir];
    NSString *a = @"a(x) ::= <<foo>>\nb() ::= <<bar>>\n";
    [self writeFile:dir fileName:@"a.stg" content:a];
    STGroup *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/a.stg"]] autorelease];
    [group load];
    ST *st = [group getInstanceOf:@"a"];
    NSInteger originalHashCode = (NSInteger) st;
    [group unload];
    st = [group getInstanceOf:@"a"];
    NSInteger newHashCode = (NSInteger) st;
    STAssertTrue( (originalHashCode == newHashCode), @"Expected \"YES\" BUT GOT \"NO\"" );
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    st = [group getInstanceOf:@"b"];
    expected = @"bar";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    return;
}

@end
