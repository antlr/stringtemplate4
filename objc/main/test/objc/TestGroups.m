#import "TestGroups.h"

NSString newline = @"\n";

@implementation Field_anon1

- (void) init
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

- (void) init
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

- (void) init
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

- (void) dealloc
{
    [name release];
    [super dealloc];
}

@end

@implementation Counter

- (void) init
{
    if ( (self=[super init]) != nil ) {
        n = 0;
    }
    return self;
}

- (NSString *) description
{
    return [String valueOf:n++];
}

- (NSString *) toString
{
    return [self description];
}

@end

NSString *const tmpdir = @"~/Documents/";
NSString *const newline = @"\n"/* Misc.newline */;

@implementation TestGroups

- (void) testSimpleGroup
{
    NSString *dir = [self randomDir];
    NSString *a = [NSString stringWithFormat:@"a(x) ::= <<%@foo%@>>%@", newline, newline, newline];
    [self writeFile:dir fileName:@"a.st" content:a];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testSimpleGroupFromString
{
    NSString *g = @"a(x) ::= <<foo>>\nb() ::= <<bar>>\n";
    STGroup * group = [[[STGroupString alloc] init:g] autorelease];
    ST * st = [group getInstanceOf:@"a"];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testGroupWithTwoTemplates
{
    NSString *dir = [self randomDir];
    NSString *a = [NSString stringWithFormat:@"a(x) ::= <<%@foo%@>>%@", newline, newline, newline];
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = NSString stringWithFormat:@"b() ::= \"bar\"%@", newline];
    [self writeFile:dir filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"b"];
    NSString *expected = @"foobar";
    NSString *result = [st1 render] + [st2 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testSubdir
{
    NSString *dir = [self randomDir];
    [NSString stringWithFormat:@"a(x) ::= <<%@foo%@>>%@", newline, newline, newline];
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = [NSString stringWithFormat:@"b() ::= \"bar\"%@", newline];
    [self writeFile:[NSString stringWithFormat:@"%@/subdir", dir] filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"subdir/b"];
    NSString *expected = @"foobar";
    NSString *result = [st1 render] + [st2 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    st2 = [group getInstanceOf:@"subdir/b"];
    expected = @"bar";
    result = [st2 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testAbsoluteTemplateRef
{
    NSString *dir = [self randomDir];
    NSString *a = @"a(x) ::= << <subdir/b()> >>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:[NSString stringWithFormat:@"%@/subdir", dir] filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testGroupFileInDir
{
    NSString *dir = [self randomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *groupFile = [@"b() ::= \"bar\"\nc() ::= \"duh\"\n"];
    [self writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"group/b"];
    ST *st3 = [group getInstanceOf:@"group/c"];
    NSString *expected = @"foobarduh";
    NSString *result = [st1 render] + [st2 render] + [st3 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testSubSubdir
{
    NSString *dir = [self randomDir];
    [NSString stringWithFormat:@"a(x) ::= <<%@foo%@>>%@", newline, newline, newline];
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *b = [@"b() ::= \"bar\"" stringByAppendingString:newline];
    [self writeFile:[dir stringByAppendingString:@"/sub1/sub2"] fileName:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"sub1/sub2/b"];
    NSString *expected = @"foobar";
    NSString *result = [st1 render] + [st2 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testGroupFileInSubDir
{
    NSString *dir = [self randomDir];
    NSString *a = @"a(x) ::= <<\nfoo\n>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *groupFile = [@"b() ::= \"bar\"\n" stringByAppendingString:@"c() ::= \"duh\"\n"];
    [self writeFile:dir fileName:@"subdir/group.stg" content:groupFile];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"subdir/group/b"];
    ST *st3 = [group getInstanceOf:@"subdir/group/c"];
    NSString *expected = @"foobarduh";
    NSString *result = [st1 render] + [st2 render] + [st3 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testRefToAnotherTemplateInSameGroup
{
    NSString *dir = [self randomDir];
    NSString *a = @"a() ::= << <b()> >>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    [self writeFile:dir filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testRefToAnotherTemplateInSameSubdir
{
    NSString *dir = [self randomDir];
    NSString *a = @"a() ::= << <subdir/b()> >>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:[dir stringByAppendingString:@"/subdir"] filename:@"a.st" content:a];
    [self writeFile:[dir stringByAppendingString:@"/subdir"] filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"subdir/a"];
    [st.impl dump];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testDupDef
{
    NSString *dir = [self randomDir];
    NSString *groupFile = [@"b() ::= \"bar\"\nb() ::= \"duh\"\n"];
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    id<STErrorListener>errors = [ErrorBuffer newErrorBuffer];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    [group setListener:errors];
    [group load];
    NSString *expected = [NSString stringWithFormat:@"group.stg 2:0: redefinition of template b%@", newline];
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testAlias
{
    NSString *dir = [self randomDir];
    NSString *groupFile = [@"a() ::= \"bar\"\n" stringByAppendingString:@"b ::= a\n"];
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ST *st = [group getInstanceOf:@"b"];
    NSString *expected = @"bar";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testAliasWithArgs
{
    NSString *dir = [self randomDir];
    NSString *groupFile = [@"a(x,y) ::= \"<x><y>\"\n" stringByAppendingString:@"b ::= a\n"];
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ST *st = [group getInstanceOf:@"b"];
    [st add:@"x" arg1:1];
    [st add:@"y" arg1:2];
    NSString *expected = @"12";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testSimpleDefaultArg
{
    NSString *dir = [self randomDir];
    NSString *a = @"a() ::= << <b()> >>\n";
    NSString *b = @"b(x=\"foo\") ::= \"<x>\"\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    [self writeFile:dir filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @" foo ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testDefaultArgument
{
    NSString *templates = [NSString stringWithFormat:@"method(name) ::= <<%@<stat(name)>%@>>%@stat(name,value=\"99\") ::= \"x=<value>; // <name>\"%@", newline, newline, newline, newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"method"];
    [b add:@"name" arg1:@"foo"];
    NSString *expecting = @"x=99; // foo";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testBooleanDefaultArguments
{
    NSString *templates = [NSString stringWithFormat:@"method(name) ::= <<%@<stat(name)>%@>>%@stat(name,x=true,y=false) ::= \"<name>; <x> <y>\"%@", newline, newline, newline, newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"method"];
    [b add:@"name" arg1:@"foo"];
    NSString *expecting = @"foo; true false";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgument2
{
    NSString *templates = [NSString stringWithFormat:@"stat(name,value=\"99\") ::= \"x=<value>; // <name>\"%@", newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"stat"];
    [b add:@"name" arg1:@"foo"];
    NSString *expecting = @"x=99; // foo";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testSubtemplateAsDefaultArgSeesOtherArgs
{
    NSString *templates = [[[@"t(x,y={<x:{s|<s><z>}>},z=\"foo\") ::= <<\n" stringByAppendingString:@"x: <x>\n"] stringByAppendingString:@"y: <y>\n"] stringByAppendingString:@">>"] + newline;
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"t"];
    [b add:@"x" arg1:@"a"];
    NSString *expecting = [[@"x: a" stringByAppendingString:newline] stringByAppendingString:@"y: afoo"];
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentAsSimpleTemplate
{
    NSString *templates = [@"stat(name,value={99}) ::= \"x=<value>; // <name>\"" stringByAppendingString:newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"stat"];
    [b add:@"name" arg1:@"foo"];
    NSString *expecting = @"x=99; // foo";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentManuallySet
{
    NSString *templates = [[[[@"method(fields) ::= <<" stringByAppendingString:newline] stringByAppendingString:@"<fields:{f | <stat(f)>}>"] + newline stringByAppendingString:@">>"] + newline stringByAppendingString:@"stat(f,value={<f.name>}) ::= \"x=<value>; // <f.name>\""] + newline;
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *m = [group getInstanceOf:@"method"];
    [m add:@"fields" arg1:[[[Field_anon1 alloc] init] autorelease]];
    NSString *expecting = @"x=parrt; // parrt";
    NSString *result = [m render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentSeesVarFromDynamicScoping
{
    NSString *templates = [NSString stringWithFormat:@"method(fields) ::= <<%@<fields:{f | <stat()>}>%@>>%@stat(value={<f.name>}) ::= \"x=<value>; // <f.name>\"%@", newline, newline, newline, newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *m = [group getInstanceOf:@"method"];
    [m add:@"fields" arg1:[[[Field_anon2 alloc] init] autorelease]];
    NSString *expecting = @"x=parrt; // parrt";
    NSString *result = [m render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentImplicitlySet2
{
    NSString *templates = [NSString stringWithFormat:@"method(fields) ::= <<%@<fields:{f | <f:stat()>}>%@>>%@stat(f,value={<f.name>}) ::= \"x=<value>; // <f.name>\"%@", newline, newline, newline, newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *m = [group getInstanceOf:@"method"];
    [m add:@"fields" arg1:[[[Field_anon3 alloc] init] autorelease]];
    NSString *expecting = @"x=parrt; // parrt";
    NSString *result = [m render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentAsTemplate
{
    NSString *templates = [NSString stringWithFormat:@"method(name,size) ::= <<%@<stat(name)>%@>>%@stat(name,value={<name>}) ::= \"x=<value>; // <name>\"%@", newline, newline, newline, newline];
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"method"];
    [b add:@"name" arg1:@"foo"];
    [b add:@"size" arg1:@"2"];
    NSString *expecting = @"x=foo; // foo";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentAsTemplate2
{
    NSString *templates = [[[[@"method(name,size) ::= <<" stringByAppendingString:newline] stringByAppendingString:@"<stat(name)>"] + newline stringByAppendingString:@">>"] + newline stringByAppendingString:@"stat(name,value={ [<name>] }) ::= \"x=<value>; // <name>\""] + newline;
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"method"];
    [b add:@"name" arg1:@"foo"];
    [b add:@"size" arg1:@"2"];
    NSString *expecting = @"x=[foo] ; // foo";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDoNotUseDefaultArgument
{
    NSString *templates = [[[[@"method(name) ::= <<" stringByAppendingString:newline] stringByAppendingString:@"<stat(name,\"34\")>"] + newline stringByAppendingString:@">>"] + newline stringByAppendingString:@"stat(name,value=\"99\") ::= \"x=<value>; // <name>\""] + newline;
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *b = [group getInstanceOf:@"method"];
    [b add:@"name" arg1:@"foo"];
    NSString *expecting = @"x=34; // foo";
    NSString *result = [b render];
    [self assertEquals:expecting arg1:result];
}

- (void) testDefaultArgumentInParensToEvalEarly
{
    NSString *templates = [[@"A(x) ::= \"<B()>\"" stringByAppendingString:newline] stringByAppendingString:@"B(y={<(x)>}) ::= \"<y> <x> <x> <y>\""] + newline;
    [self writeFile:tmpdir filename:@"group.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group.stg", tmpdir]];
    ST *a = [group getInstanceOf:@"A"];
    [a add:@"x" arg1:[[[Counter alloc] init] autorelease]];
    NSString *expecting = @"0 1 2 0";
    NSString *result = [a render];
    [self assertEquals:expecting arg1:result];
}

- (void) testTrueFalseArgs
{
    NSString *dir = [self randomDir];
    NSString *groupFile = [@"f(x,y) ::= \"<x><y>\"\n" stringByAppendingString:@"g() ::= \"<f(true,{a})>\""];
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ST *st = [group getInstanceOf:@"g"];
    NSString *expected = @"truea";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testNamedArgsInOrder
{
    NSString *dir = [self randomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(x={a},y={b})>\"";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ST *st = [group getInstanceOf:@"g"];
    NSString *expected = @"ab";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testNamedArgsOutOfOrder
{
    NSString *dir = [self randomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(y={b},x={a})>\"";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ST *st = [group getInstanceOf:@"g"];
    NSString *expected = @"ab";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testUnknownNamedArg
{
    NSString *dir = [self randomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(x={a},z={b})>\"";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    ST *st = [group getInstanceOf:@"g"];
    [st render];
    NSString *expected = @"context [g] 1:1 attribute z isn't defined\n";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testMissingNamedArg
{
    NSString *dir = [self randomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng() ::= \"<f(x={a},{b})>\"";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    [group load];
    NSString *expected = [@"group.stg 2:28: mismatched input '{' expecting ID" stringByAppendingString:newline];
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testNamedArgsNotAllowInIndirectInclude
{
    NSString *dir = [self randomDir];
    NSString *groupFile = @"f(x,y) ::= \"<x><y>\"\ng(name) ::= \"<(name)(x={a},y={b})>\"";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroupFile *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    [group setListener:errors];
    [group load];
    NSString *expected = @"group.stg 2:22: '=' came as a complete surprise to me\n";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testCantSeeGroupDirIfGroupFileOfSameName
{
    NSString *dir = [self randomDir];
    NSString *a = @"a() ::= <<dir1 a>>\n";
    [self writeFile:dir filename:@"group/a.st" content:a];
    NSString *groupFile = @"b() ::= \"group file b\"\n";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroup *group1 = [[[STGroupDir alloc] init:dir] autorelease];
    ST *st = [group1 getInstanceOf:@"group/a"];
    STAssertTrue( ((expected == nil) && (st == nil)), @"Expected nil BUT GOT \"%@\"", [st description] );
}

- (void) testFullyQualifiedGetInstanceOf
{
    NSString *dir = [self randomDir];
    [NSString stringWithFormat:@"a(x) ::= <<%@foo%@>>%@", newline, newline, newline];
    [self writeFile:dir fileName:@"a.st" content:a];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testFullyQualifiedTemplateRef
{
    NSString *dir = [self randomDir];
    NSString *a = @"a() ::= << <subdir/b()> >>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:[dir stringByAppendingString:@"/subdir"] filename:@"a.st" content:a];
    [self writeFile:[dir stringByAppendingString:@"/subdir"] filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st = [group getInstanceOf:@"subdir/a"];
    NSString *expected = @" bar ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testFullyQualifiedTemplateRef2
{
    NSString *dir = [self randomDir];
    NSString *a = @"a(x) ::= << <group/b()> >>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    NSString *groupFile = @"b() ::= \"bar\"\nc() ::= \"<a()>\"\n";
    [self writeFile:dir filename:@"group.stg" content:groupFile];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    ST *st1 = [group getInstanceOf:@"a"];
    ST *st2 = [group getInstanceOf:@"group/c"];
    NSString *expected = @" bar  bar ";
    NSString *result = [st1 render] + [st2 render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testUnloadingSimpleGroup
{
    NSString *dir = [self randomDir];
    NSString *a = @"a(x) ::= <<foo>>\n";
    NSString *b = @"b() ::= <<bar>>\n";
    [self writeFile:dir fileName:@"a.st" content:a];
    [self writeFile:dir filename:@"b.st" content:b];
    STGroup *group = [STGroupDir newSTGroupDir:dir];
    [group load];
    ST *st = [group getInstanceOf:@"a"];
    int originalHashCode = [System identityHashCode:st];
    [group unload];
    st = [group getInstanceOf:@"a"];
    int newHashCode = [System identityHashCode:st];
    [self assertEquals:originalHashCode == newHashCode arg1:NO];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    st = [group getInstanceOf:@"b"];
    expected = @"bar";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

- (void) testUnloadingGroupFile
{
    NSString *dir = [self randomDir];
    NSString *a = [@"a(x) ::= <<foo>>\n" stringByAppendingString:@"b() ::= <<bar>>\n"];
    [self writeFile:dir filename:@"a.stg" content:a];
    STGroup *group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/a.stg"]] autorelease];
    [group load];
    ST *st = [group getInstanceOf:@"a"];
    int originalHashCode = [System identityHashCode:st];
    [group unload];
    st = [group getInstanceOf:@"a"];
    int newHashCode = [System identityHashCode:st];
    [self assertEquals:originalHashCode == newHashCode arg1:NO];
    NSString *expected = @"foo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
    st = [group getInstanceOf:@"b"];
    expected = @"bar";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" BUT GOT \"%@\"", expected, result );
}

@end
