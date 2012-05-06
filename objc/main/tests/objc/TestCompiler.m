#import <Foundation/Foundation.h>
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
    [self assertEquals:asmExpected result:asmResult];
    NSLog( @"returned from first assert" );
    NSString *expected = @"[hi , name]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    NSLog( @"end test01.Attr" );
    return;
}

- (void) test02Include
{
    NSString *aTemplate = @"hi <foo()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, new 1 0, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test02aIncludeWithPassThrough
{
    NSString *template = @"hi <foo(...)>";
    CompiledST *code = [[Compiler newCompiler] compile:template];
    NSString *asmExpected =
        @"write_str 0, args, passthru 1, new_box_args 1, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test02bIncludeWithPartialPassThrough
{
    NSString *template = @"hi <foo(x=y,...)>";
    CompiledST *code = [[Compiler newCompiler] compile:template];
    NSString *asmExpected =
        @"write_str 0, args, load_attr 1, store_arg 2, passthru 3, new_box_args 3, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , y, x, foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test03SuperInclude
{
    NSString *aTemplate = @"<super.foo()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"super_new 0 0, write";
    [code dump];
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test04SuperIncludeWithArgs
{
    NSString *aTemplate = @"<super.foo(a,{b})>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, new 1 0, super_new 2 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[a, _sub1, foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test05SuperIncludeWithNamedArgs
{
    NSString *aTemplate = @"<super.foo(x=a,y={b})>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"args, load_attr 0, store_arg 1, new 2 0, store_arg 3, super_new_box_args 4, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[a, x, _sub1, y, foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test06IncludeWithArgs
{
    NSString *aTemplate = @"hi <foo(a,b)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, load_attr 2, new 3 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , a, b, foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test07AnonIncludeArgs
{
    NSString *aTemplate = @"<({ a, b | <a><b>})>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"new 0 0, tostr, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[_sub1]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test08AnonIncludeArgMismatch
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    NSString *aTemplate = @"<a:{foo}>";
    STGroup *g = [STGroup newSTGroup];
    g.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    CompiledST *code = [[Compiler newCompiler:g] compile:aTemplate];
    NSString *expected = @"1:3: anonymous template has 0 arg(s) but mapped across 1 value(s)\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test09AnonIncludeArgMismatch2
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    NSString *aTemplate = @"<a,b:{x|foo}>";
    STGroup *g = [STGroup newSTGroup];
    g.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    CompiledST *code = [[Compiler newCompiler:g] compile:aTemplate];
    NSString *expected = @"1:5: anonymous template has 1 arg(s) but mapped across 2 value(s)\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test10AnonIncludeArgMismatch3
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    NSString *aTemplate = @"<a:{x|foo},{bar}>";
    STGroup *g = [STGroup newSTGroup];
    g.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    CompiledST *code = [[Compiler newCompiler:g] compile:aTemplate];
    NSString *expected = @"1:11: anonymous template has 0 arg(s) but mapped across 1 value(s)\n";
    NSString *result = [errors description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test11IndirectIncludeWithArgs
{
    NSString *aTemplate = @"hi <(foo)(a,b)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, tostr, load_attr 2, load_attr 3, new_ind 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , foo, a, b]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test12Prop
{
    NSString *aTemplate = @"hi <a.b>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, load_prop 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , a, b]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test13Prop2
{
    NSString *aTemplate = @"<u.id>: <u.name>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_prop 1, write, write_str 2, load_attr 0, load_prop 3, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[u, id, : , name]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test14Map
{
    NSString *aTemplate = @"<name:bold()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, map, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, bold]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test15MapAsOption
{
    NSString *aTemplate = @"<a; wrap=name:bold()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, options, load_attr 1, null, new 2 1, map, store_option 4, write_opt";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[a, name, bold]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test16MapArg
{
    NSString *aTemplate = @"<name:bold(x)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, map, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, x, bold]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test17IndirectMapArg
{
    NSString *aTemplate = @"<name:(t)(x)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, tostr, null, load_attr 2, new_ind 2, map, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, t, x]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test18RepeatedMap
{
    NSString *aTemplate = @"<name:bold():italics()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, map, null, new 2 1, map, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, bold, italics]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test19RepeatedMapArg
{
    NSString *aTemplate = @"<name:bold(x):italics(x,y)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, map, null, load_attr 1, load_attr 3, new 4 3, map, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, x, bold, y, italics]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test20RotMap
{
    NSString *aTemplate = @"<name:bold(),italics()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, null, new 2 1, rot_map 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, bold, italics]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test21RotMapArg
{
    NSString *aTemplate = @"<name:bold(x),italics()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, null, new 3 1, rot_map 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, x, bold, italics]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test22ZipMap
{
    NSString *aTemplate = @"<names,phones:bold()>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[names, phones, bold]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test23ZipMapArg
{
    NSString *aTemplate = @"<names,phones:bold(x)>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, null, null, load_attr 2, new 3 3, zip_map 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[names, phones, x, bold]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test24AnonMap
{
    NSString *aTemplate = @"<name:{n | <n>}>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, null, new 1 1, map, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[name, _sub1]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test25AnonZipMap
{
    NSString *aTemplate = @"<a,b:{x,y | <x><y>}>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[a, b, _sub1]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test26If
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 12, write_str 2";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[go: , name, hi, foo]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test27IfElse
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<else>bye<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 15, write_str 2, br 18, write_str 3";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[go: , name, hi, foo, bye]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test28ElseIf
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<elseif(user)>a user<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 15, write_str 2, br 24, load_attr 3, brf 24, write_str 4";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[go: , name, hi, foo, user, a user]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test29ElseIfElse
{
    NSString *aTemplate = @"go: <if(name)>hi, foo<elseif(user)>a user<else>bye<endif>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, brf 15, write_str 2, br 30, load_attr 3, brf 27, write_str 4, br 30, write_str 5";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[go: , name, hi, foo, user, a user, bye]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test30Option
{
    NSString *aTemplate = @"hi <name; separator=\"x\">";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, options, load_str 2, store_option 3, write_opt";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , name, x]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test31OptionAsTemplate
{
    NSString *aTemplate = @"hi <name; separator={, }>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"write_str 0, load_attr 1, options, new 2 0, store_option 3, write_opt";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[hi , name, _sub1]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test32Options
{
    NSString *aTemplate = @"hi <name; anchor, wrap=foo(), separator=\", \">";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *expected = @"[hi , name, true, foo, , ]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    NSString *asmExpected = @"write_str 0, load_attr 1, options, load_str 2, store_option 0, new 3 0, store_option 4, load_str 4, store_option 3, write_opt";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    return;
}

- (void) test33EmptyList
{
    NSString *aTemplate = @"<[]>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"list, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test34List
{
    NSString *aTemplate = @"<[a,b]>";
    CompiledST *code = [[Compiler newCompiler] compile:aTemplate];
    NSString *asmExpected = @"list, load_attr 0, add, load_attr 1, add, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[a, b]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test35EmbeddedRegion
{
    NSString *aTemplate = @"<@r>foo<@end>";
    CompiledST *code = [[Compiler newCompiler] compile:@"a" template:aTemplate];
    NSString *asmExpected = @"new 0 0, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[region__/a__r]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

- (void) test36Region
{
    NSString *aTemplate = @"x:<@r()>";
    CompiledST *code = [[Compiler newCompiler] compile:@"a" template:aTemplate];
    NSString *asmExpected = @"write_str 0, new 1 0, write";
    NSString *asmResult = [code dis_instrs];
    [self assertEquals:asmExpected result:asmResult];
    NSString *expected = @"[x:, /region__/a__r]";
    NSString *result = [[Strings newStringsWithArray:code.strings] description];
    [self assertEquals:expected result:result];
    return;
}

@end
