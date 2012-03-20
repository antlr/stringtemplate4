#import <Cocoa/Cocoa.h>
#import "TestCompiler.h"

@implementation TestCompiler

- (void) test01Attr
{
    NSLog( @"Start test01.Attr" );
    NSString *aTemplate = @"hi <name>";
    NSLog( @"aTemplate = %@", aTemplate );
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSLog( @"returned from newCompiler" );
    NSString *asmExpected = @"write_str 0, load_attr 1, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSLog( @"returned from first assert" );
    NSString *stringsExpected = @"[hi , name]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    NSLog( @"end test01.Attr" );
    return;
}

- (void) test02Include
{
    NSString *aTemplate = @"hi <foo()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, new 1 0, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test02aIncludeWithPassThrough
{
    NSString *template = @"hi <foo(...)>";
    CompiledST *code = [[Compiler newCompiler] compile:template];
    NSString *asmExpected =
        @"write_str 0, args, passthru 1, new_box_args 1, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test02bIncludeWithPartialPassThrough
{
    NSString *template = @"hi <foo(x=y,...)>";
    CompiledST *code = [[Compiler newCompiler] compile:template];
    NSString *asmExpected =
        @"write_str 0, args, load_attr 1, store_arg 2, passthru 3, new_box_args 3, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , y, x, foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test03SuperInclude
{
    NSString *aTemplate = @"<super.foo()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"super_new 0 0, write";
    [code dump];
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test04SuperIncludeWithArgs
{
    NSString *aTemplate = @"<super.foo(a,{b})>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, new 1 0, super_new 2 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[a, _sub1, foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test05SuperIncludeWithNamedArgs
{
    NSString *aTemplate = @"<super.foo(x=a,y={b})>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"args, load_attr 0, store_arg 1, new 2 0, store_arg 3, super_new_box_args 4, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[a, x, _sub1, y, foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test06IncludeWithArgs
{
    NSString *aTemplate = @"hi <foo(a,b)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, load_attr 2, new 3 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , a, b, foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test07AnonIncludeArgs
{
    NSString *aTemplate = @"<({ a, b | <a><b>})>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"new 0 0, tostr, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[_sub1]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test08AnonIncludeArgMismatch
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    NSString *aTemplate = @"<a:{foo}>";
    STGroup *g = [STGroup newSTGroup];
    g.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    CompiledST *code = [[Compiler newCompiler:g] compile:aTemplate];
    NSString *expected = @"1:3: anonymous template has 0 arg(s) but mapped across 1 value(s)";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test09AnonIncludeArgMismatch2
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    NSString *aTemplate = @"<a,b:{x|foo}>";
    STGroup *g = [STGroup newSTGroup];
    g.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    CompiledST *code = [[Compiler newCompiler:g] compile:aTemplate];
    NSString *expected = @"1:5: anonymous template has 1 arg(s) but mapped across 2 value(s)";
    NSString *result = [errors description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test10AnonIncludeArgMismatch3
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    NSString *aTemplate = @"<a:{x|foo},{bar}>";
    STGroup *g = [STGroup newSTGroup];
    g.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    CompiledST *code = [[Compiler newCompiler:g] compile:aTemplate];
    NSString *expected = @"1:11: anonymous template has 0 arg(s) but mapped across 1 value(s)";
    STAssertTrue( [expected isEqualTo:[errors description]], @"Expected \"%@\" but had \"%@\"", expected, [errors description] );
    return;
}

- (void) test11IndirectIncludeWithArgs
{
    NSString *aTemplate = @"hi <(foo)(a,b)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, tostr, load_attr 2, load_attr 3, new_ind 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , foo, a, b]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult);
    return;
}

- (void) test12Prop
{
    NSString *aTemplate = @"hi <a.b>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, load_prop 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , a, b]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test13Prop2
{
    NSString *aTemplate = @"<u.id>: <u.name>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_prop 1, write, write_str 2, load_attr 0, load_prop 3, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[u, id, : , name]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test14Map
{
    NSString *aTemplate = @"<name:bold()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, map, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, bold]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test15MapAsOption
{
    NSString *aTemplate = @"<a; wrap=name:bold()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, options, load_attr 1, null, new 2 1, map, store_option 4, write_opt";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[a, name, bold]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test16MapArg
{
    NSString *aTemplate = @"<name:bold(x)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, map, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, x, bold]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test17IndirectMapArg
{
    NSString *aTemplate = @"<name:(t)(x)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, tostr, null, load_attr 2, new_ind 2, map, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, t, x]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test18RepeatedMap
{
    NSString *aTemplate = @"<name:bold():italics()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, map, null, new 2 1, map, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, bold, italics]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test19RepeatedMapArg
{
    NSString *aTemplate = @"<name:bold(x):italics(x,y)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, map, null, load_attr 1, load_attr 3, new 4 3, map, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, x, bold, y, italics]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test20RotMap
{
    NSString *aTemplate = @"<name:bold(),italics()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, null, new 2 1, rot_map 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, bold, italics]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test21RotMapArg
{
    NSString *aTemplate = @"<name:bold(x),italics()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, null, new 3 1, rot_map 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, x, bold, italics]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test22ZipMap
{
    NSString *aTemplate = @"<names,phones:bold()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[names, phones, bold]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test23ZipMapArg
{
    NSString *aTemplate = @"<names,phones:bold(x)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, null, null, load_attr 2, new 3 3, zip_map 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[names, phones, x, bold]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test24AnonMap
{
    NSString *aTemplate = @"<name:{n | <n>}>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, map, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[name, _sub1]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test25AnonZipMap
{
    NSString *aTemplate = @"<a,b:{x,y | <x><y>}>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[a, b, _sub1]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test26If
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 12, write_str 2";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[go: , name, hi, foo]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test27IfElse
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<else>bye<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 15, write_str 2, br 18, write_str 3";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[go: , name, hi, foo, bye]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test28ElseIf
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<elseif(user)>a user<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 15, write_str 2, br 24, load_attr 3, brf 24, write_str 4";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[go: , name, hi, foo, user, a user]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test29ElseIfElse
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<elseif(user)>a user<else>bye<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 15, write_str 2, br 30, load_attr 3, brf 27, write_str 4, br 30, write_str 5";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[go: , name, hi, foo, user, a user, bye]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test30Option
{
    NSString *aTemplate = @"hi <name; separator=\"x\">";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, options, load_str 2, store_option 3, write_opt";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , name, x]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test31OptionAsTemplate
{
    NSString *aTemplate = @"hi <name; separator={, }>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, options, new 2 0, store_option 3, write_opt";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[hi , name, _sub1]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test32Options
{
    NSString *aTemplate = @"hi <name; anchor, wrap=foo(), separator=\", \">";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *stringsExpected = @"[hi , name, true, foo, , ]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    NSString *asmExpected = @"write_str 0, load_attr 1, options, load_str 2, store_option 0, new 3 0, store_option 4, load_str 4, store_option 3, write_opt";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    return;
}

- (void) test33EmptyList
{
    NSString *aTemplate = @"<[]>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"list, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test34List
{
    NSString *aTemplate = @"<[a,b]>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"list, load_attr 0, add, load_attr 1, add, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[a, b]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test35EmbeddedRegion
{
    NSString *aTemplate = @"<@r>foo<@end>";
    CompiledST *code = [[Compiler newCompiler] compile:@"a" template:aTemplate];
    NSString *asmExpected = @"new 0 0, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[region__/a__r]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

- (void) test36Region
{
    NSString *aTemplate = @"x:<@r()>";
    CompiledST *code = [[Compiler newCompiler] compile:@"a" template:aTemplate];
    NSString *asmExpected = @"write_str 0, new 1 0, write";
    NSString *asmResult = [code dis_instrs];
    STAssertTrue( [asmExpected isEqualTo:asmResult], @"Expected \"%@\" but had \"%@\"", asmExpected, asmResult );
    NSString *stringsExpected = @"[x:, region__/a__r]";
    NSString *stringsResult = [[Strings newStringsWithArray:code.strings] description];
    STAssertTrue( [stringsExpected isEqualTo:stringsResult], @"Expected \"%@\" but had \"%@\"", stringsExpected, stringsResult );
    return;
}

@end
