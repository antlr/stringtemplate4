#import "TestCompiler.h"

@implementation TestCompiler

- (void) setUp {
  org.stringtemplate.v4.compiler.Compiler.subtemplateCount = 0;
}

- (void) testAttr {
  NSString * template = @"hi <name>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [[@"write_str 0, " stringByAppendingString:@"load_attr 1, "] stringByAppendingString:@"write"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , name]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testInclude {
  NSString * template = @"hi <foo()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, new 1 0, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , foo]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testSuperInclude {
  NSString * template = @"<super.foo()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"super_new 0 0, write";
  [code dump];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[foo]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testSuperIncludeWithArgs {
  NSString * template = @"<super.foo(a,{b})>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, new 1 0, super_new 2 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[a, _sub1, foo]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testSuperIncludeWithNamedArgs {
  NSString * template = @"<super.foo(x=a,y={b})>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"args, load_attr 0, store_arg 1, new 2 0, store_arg 3, super_new_box_args 4, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[a, x, _sub1, y, foo]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testIncludeWithArgs {
  NSString * template = @"hi <foo(a,b)>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, load_attr 1, load_attr 2, new 3 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , a, b, foo]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testAnonIncludeArgs {
  NSString * template = @"<({ a, b | <a><b>})>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"new 0 0, tostr, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[_sub1]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testAnonIncludeArgMismatch {
  STErrorListener * errors = [[[ErrorBuffer alloc] init] autorelease];
  NSString * template = @"<a:{foo}>";
  STGroup * g = [[[STGroup alloc] init] autorelease];
  g.errMgr = [[[ErrorManager alloc] init:errors] autorelease];
  CompiledST * code = [[[[Compiler alloc] init:g] autorelease] compile:template];
  NSString * expected = [@"1:3: anonymous template has 0 arg(s) but mapped across 1 value(s)" stringByAppendingString:newline];
  [self assertEquals:expected param1:[errors description]];
}

- (void) testAnonIncludeArgMismatch2 {
  STErrorListener * errors = [[[ErrorBuffer alloc] init] autorelease];
  NSString * template = @"<a,b:{x|foo}>";
  STGroup * g = [[[STGroup alloc] init] autorelease];
  g.errMgr = [[[ErrorManager alloc] init:errors] autorelease];
  CompiledST * code = [[[[Compiler alloc] init:g] autorelease] compile:template];
  NSString * expected = [@"1:5: anonymous template has 1 arg(s) but mapped across 2 value(s)" stringByAppendingString:newline];
  [self assertEquals:expected param1:[errors description]];
}

- (void) testAnonIncludeArgMismatch3 {
  STErrorListener * errors = [[[ErrorBuffer alloc] init] autorelease];
  NSString * template = @"<a:{x|foo},{bar}>";
  STGroup * g = [[[STGroup alloc] init] autorelease];
  g.errMgr = [[[ErrorManager alloc] init:errors] autorelease];
  CompiledST * code = [[[[Compiler alloc] init:g] autorelease] compile:template];
  NSString * expected = [@"1:11: anonymous template has 0 arg(s) but mapped across 1 value(s)" stringByAppendingString:newline];
  [self assertEquals:expected param1:[errors description]];
}

- (void) testIndirectIncludeWitArgs {
  NSString * template = @"hi <(foo)(a,b)>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, load_attr 1, tostr, load_attr 2, load_attr 3, new_ind 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , foo, a, b]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testProp {
  NSString * template = @"hi <a.b>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, load_attr 1, load_prop 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , a, b]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testProp2 {
  NSString * template = @"<u.id>: <u.name>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [@"load_attr 0, load_prop 1, write, write_str 2, " stringByAppendingString:@"load_attr 0, load_prop 3, write"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[u, id, : , name]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testMap {
  NSString * template = @"<name:bold()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, null, new 1 1, map, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, bold]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testMapAsOption {
  NSString * template = @"<a; wrap=name:bold()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [@"load_attr 0, options, load_attr 1, null, new 2 1, map, " stringByAppendingString:@"store_option 4, write_opt"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[a, name, bold]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testMapArg {
  NSString * template = @"<name:bold(x)>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, map, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, x, bold]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testIndirectMapArg {
  NSString * template = @"<name:(t)(x)>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, load_attr 1, tostr, null, load_attr 2, new_ind 2, map, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, t, x]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testRepeatedMap {
  NSString * template = @"<name:bold():italics()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, null, new 1 1, map, null, new 2 1, map, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, bold, italics]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testRepeatedMapArg {
  NSString * template = @"<name:bold(x):italics(x,y)>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [@"load_attr 0, null, load_attr 1, new 2 2, map, " stringByAppendingString:@"null, load_attr 1, load_attr 3, new 4 3, map, write"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, x, bold, y, italics]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testRotMap {
  NSString * template = @"<name:bold(),italics()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, null, new 1 1, null, new 2 1, rot_map 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, bold, italics]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testRotMapArg {
  NSString * template = @"<name:bold(x),italics()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, null, load_attr 1, new 2 2, null, new 3 1, rot_map 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, x, bold, italics]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testZipMap {
  NSString * template = @"<names,phones:bold()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[names, phones, bold]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testZipMapArg {
  NSString * template = @"<names,phones:bold(x)>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, load_attr 1, null, null, load_attr 2, new 3 3, zip_map 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[names, phones, x, bold]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testAnonMap {
  NSString * template = @"<name:{n | <n>}>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, null, new 1 1, map, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[name, _sub1]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testAnonZipMap {
  NSString * template = @"<a,b:{x,y | <x><y>}>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"load_attr 0, load_attr 1, null, null, new 2 2, zip_map 2, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[a, b, _sub1]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testIf {
  NSString * template = @"go: <if(name)>hi, foo<endif>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, load_attr 1, brf 12, write_str 2";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[go: , name, hi, foo]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testIfElse {
  NSString * template = @"go: <if(name)>hi, foo<else>bye<endif>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [[[[[@"write_str 0, " stringByAppendingString:@"load_attr 1, "] stringByAppendingString:@"brf 15, "] stringByAppendingString:@"write_str 2, "] stringByAppendingString:@"br 18, "] stringByAppendingString:@"write_str 3"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[go: , name, hi, foo, bye]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testElseIf {
  NSString * template = @"go: <if(name)>hi, foo<elseif(user)>a user<endif>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [[[[[[[@"write_str 0, " stringByAppendingString:@"load_attr 1, "] stringByAppendingString:@"brf 15, "] stringByAppendingString:@"write_str 2, "] stringByAppendingString:@"br 24, "] stringByAppendingString:@"load_attr 3, "] stringByAppendingString:@"brf 24, "] stringByAppendingString:@"write_str 4"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[go: , name, hi, foo, user, a user]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testElseIfElse {
  NSString * template = @"go: <if(name)>hi, foo<elseif(user)>a user<else>bye<endif>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [[[[[[[[[@"write_str 0, " stringByAppendingString:@"load_attr 1, "] stringByAppendingString:@"brf 15, "] stringByAppendingString:@"write_str 2, "] stringByAppendingString:@"br 30, "] stringByAppendingString:@"load_attr 3, "] stringByAppendingString:@"brf 27, "] stringByAppendingString:@"write_str 4, "] stringByAppendingString:@"br 30, "] stringByAppendingString:@"write_str 5"];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[go: , name, hi, foo, user, a user, bye]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testOption {
  NSString * template = @"hi <name; separator=\"x\">";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, load_attr 1, options, load_str 2, store_option 3, write_opt";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , name, x]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testOptionAsTemplate {
  NSString * template = @"hi <name; separator={, }>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"write_str 0, load_attr 1, options, new 2 0, store_option 3, write_opt";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[hi , name, _sub1]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testOptions {
  NSString * template = @"hi <name; anchor, wrap=foo(), separator=\", \">";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = [[[[[[[[[@"write_str 0, " stringByAppendingString:@"load_attr 1, "] stringByAppendingString:@"options, "] stringByAppendingString:@"load_str 2, "] stringByAppendingString:@"store_option 0, "] stringByAppendingString:@"new 3 0, "] stringByAppendingString:@"store_option 4, "] stringByAppendingString:@"load_str 4, "] stringByAppendingString:@"store_option 3, "] stringByAppendingString:@"write_opt"];
  NSString * stringsExpected = @"[hi , name, true, foo, , ]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
}

- (void) testEmptyList {
  NSString * template = @"<[]>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"list, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testList {
  NSString * template = @"<[a,b]>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:template];
  NSString * asmExpected = @"list, load_attr 0, add, load_attr 1, add, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[a, b]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testEmbeddedRegion {
  NSString * template = @"<@r>foo<@end>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:@"a" param1:template];
  NSString * asmExpected = @"new 0 0, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[region__a__r]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

- (void) testRegion {
  NSString * template = @"x:<@r()>";
  CompiledST * code = [[[[Compiler alloc] init] autorelease] compile:@"a" param1:template];
  NSString * asmExpected = @"write_str 0, new 1 0, write";
  NSString * asmResult = [code instrs];
  [self assertEquals:asmExpected param1:asmResult];
  NSString * stringsExpected = @"[x:, region__a__r]";
  NSString * stringsResult = [Arrays description:code.strings];
  [self assertEquals:stringsExpected param1:stringsResult];
}

@end
