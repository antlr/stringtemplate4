/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "TestCoreBasics.h"
#import "STGroup.h"
#import "STGroupString.h"
#import "STGroupDir.h"
#import "STGroupFile.h"
#import "NoIndentWriter.h"
#import "ErrorBufferAllErrors.h"
#import "Writer.h"

@implementation TestCoreBasics_Anon1

+ (id) newAnon
{
    return [[TestCoreBasics_Anon1 alloc] init];
}

- (id) init
{
    self=[super initWithCapacity:16];
    if ( self != nil ) {
        [self addObject:@"Ter"];
        [self addObject:@"Tom"];
    }
    return self;
}

@end

@implementation TestCoreBasics_Anon2

@synthesize aDict;

+ (id) newAnon
{
    return [[TestCoreBasics_Anon2 alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        aDict = [AMutableDictionary dictionaryWithCapacity:5];
        [aDict setObject:@"b" forKey:@"a"];
    }
    return self;
}

- (void) setObject:(id)anObj forKey:(id)aName
{
    [aDict setObject:anObj forKey:aName];
}

- (id) objectForKey:(id)aKey
{
    return [aDict objectForKey:aKey];
}

@end

@implementation TestCoreBasics_Anon3

@synthesize aDict;

+ (id) newAnon
{
    return [[TestCoreBasics_Anon3 alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        aDict = [AMutableDictionary dictionaryWithCapacity:16];
        [self setObject:@"b" forKey:@"a"];
        [self setObject:@"d" forKey:@"c"];
    }
    return self;
}

- (void) setObject:(id)anObj forKey:(id)aName
{
    [aDict setObject:anObj forKey:aName];
}

- (id) objectForKey:(id)aKey
{
    return [aDict objectForKey:aKey];
}

@end

@implementation TestCoreBasics_Anon4

+ (id) newAnon
{
    return [[TestCoreBasics_Anon4 alloc] init];
}

- (id) init
{
    self=[super initWithCapacity:16];
    if ( self != nil ) {
        [self addObject:@"Ter"];
        [self addObject:@"Tom"];
    }
    return self;
}

@end

@implementation TestCoreBasics

- (void)setUp
{
    [super setUp];

    // Set-up code here.
    STGroup.resetDefaultGroup;
}

- (void)tearDown
{
    // Tear-down code here.
    [super tearDown];
}

- (void) test01NullAttr
{
    NSString *aTemplate = @"hi <name>!";
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    NSString *expected = @"hi !";
    NSString *result = [st render];
    [st release];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test02Attr
{
    NSString *aTemplate = @"hi <name>!";
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"hi Ter!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st release];
}

- (void) test02aChainAttr
{
    NSString * template = @"<x>:<names>!";
    ST *st = [[ST newSTWithTemplate:template] retain];
    [[[st add:@"names" value:@"Ter"] add:@"names" value:@"Tom"] addInt:@"x" value:1];
    NSString * expected = @"1:TerTom!";
    NSString * result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st release];
}

- (void) test03SetUnknownAttr
{
    NSString *aTemplate = @"t() ::= <<hi <name>!>>\n";
    ErrorBuffer *errors = [[ErrorBuffer newErrorBuffer] retain];
    [BaseTest writeFile:tmpdir fileName:@"t.stg" content:aTemplate];
    STGroupFile *group = [[STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]] retain];
    group.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    ST *st = [[group getInstanceOf:@"t"] retain];
    NSString *result = nil;
    
    @try {
        [st add:@"name" value:@"Ter"];
    }
    @catch (IllegalArgumentException *iae) {
        result = [iae reason];
    }
    NSString *expected = @"no such attribute: name";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [errors release];
    [st release];
    [group release];
}

- (void) test04MultiAttr
{
    NSString *aTemplate = @"hi <name>!";
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    NSString *expected = @"hi TerTom!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    
    [st release];
}

- (void) test05AttrIsList
{
    NSInteger cnt;
    NSString *aTemplate = @"hi <name>!";
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    AMutableArray *names = [TestCoreBasics_Anon1 newAnon];
    [st add:@"name" value:names];
    [st add:@"name" value:@"Sumana"]; // shouldn't alter my version of names list!
    NSString *expected = @"hi TerTomSumana!";  // ST sees 3 names
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    STAssertTrue( (cnt = [names count] == 2), @"Expected [names count] == 2 but got %d)", cnt );
    [st release];
}

- (void) test06AttrIsArray
{
    NSString *aTemplate = @"hi <name>!";
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    NSArray *names = [NSArray arrayWithObjects:@"Ter", @"Tom", nil];
    [st add:@"name" value:names];
    [st add:@"name" value:@"Sumana"]; // shouldn't alter my version of names list!
    NSString *expected = @"hi TerTomSumana!";  // ST sees 3 names
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st release];
}

- (void) test07Prop
{
    NSString *aTemplate = @"<u.num>: <u.name>"; // checks field and method getter
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    [st add:@"u" value:[User newUser:1 name:@"parrt"]];
    NSString *expected = @"1: parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st release];
}

- (void) test08PropWithNoAttr
{
    NSString *aTemplate = @"<foo.a>: <ick>";
    ST *st = [[ST newSTWithTemplate:aTemplate] retain];
    [st add:@"foo" value:[AMutableDictionary dictionaryWithObject:@"b" forKey:@"a"]];
    NSString *expected = @"b: ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st release];
}

- (void) test08aMapAcrossDictionaryUsesKeys
{
    NSString * template = @"<foo:{f | <f>}>"; // checks field and method getter
    ST * st = [[ST newSTWithTemplate:template] retain];
    [st add:@"foo" value:[AMutableDictionary dictionaryWithObjects:[NSArray arrayWithObjects:@"b", @"d", nil] forKeys:[NSArray arrayWithObjects:@"a", @"c", nil]]];
    NSString * expected = @"ac";
    NSString * result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st release];
}

- (void) test09STProp
{
    NSString *aTemplate = @"<t.x>"; // get x attr of template t
    ST *st = [ST newSTWithTemplate:aTemplate];
    ST *t = [ST newSTWithTemplate:@"<x>"];
    [t add:@"x" value:@"Ter"];
    [st add:@"t" value:t];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test10BooleanISProp
{
    NSString *aTemplate = @"<t.manager>"; // call isManager
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"t" value:[User newUser:32 name:@"Ter"]];
    NSString *expected = @"true";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test11BooleanHASProp
{
    NSString *aTemplate = @"<t.parkingSpot>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"t" value:[User newUser:32 name:@"Ter"]];
    NSString *expected = @"true";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test12NullAttrProp
{
    NSString *aTemplate = @"<u.ID>: <u.name>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @": ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test13NoSuchProp
{
    ErrorBufferAllErrors *errors = [ErrorBufferAllErrors newErrorBuffer];
    NSString *aTemplate = @"<u.qqq>";
    STGroup *group = [STGroupFile newSTGroupFile];
    group.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    ST *st = [ST newST:group template:aTemplate];
    [st add:@"u" value:[User newUser:1 name:@"parrt"]];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );

    STNoSuchPropertyException *e;
    if ( [errors.errors count] > 0 ) {
        STRuntimeMessage *msg = (STRuntimeMessage *)[errors.errors objectAtIndex:0];
        e = (STNoSuchPropertyException *)msg.cause;
    }
    //  [self assertEquals:@"org.stringtemplate.v4.test.BaseTest$User.qqq" arg1:e.propertyName];
    //STAssertTrue( [@"org.stringtemplate.v4.test.BaseTest$User.qqq" isEqualTo:e.propertyName], @"Expected \"%@\" but got \"%@\"", @"org.stringtemplate.v4.test.BaseTest$User.qqq", e.propertyName );
    expected = @"User.qqq";
    result = e.propertyName;
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test14NullIndirectProp
{
    ErrorBufferAllErrors *errors = [[ErrorBufferAllErrors alloc] init];
    STGroup *group = [STGroupFile newSTGroupFile];
    group.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    NSString *aTemplate = @"<u.(qqq)>";
    ST *st = [[ST alloc] init:group template:aTemplate];
    [st add:@"u" value:[User newUser:1 name:@"parrt"]];
    [st add:@"qqq" value:nil];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
/*    
    STRuntimeMessage *msg = (STRuntimeMessage *)[errors.errors objectAtIndex:0];
    STNoSuchPropertyException *e = (STNoSuchPropertyException *)msg.cause;
    //  [self assertEquals:@"org.stringtemplate.v4.test.BaseTest$User.null" arg1:e.propertyName];
    // expected = @"org.stringtemplate.v4.test.BaseTest$User.null";
    expected = @"User.null";
    result = e.propertyName;
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
 */
}

- (void) test15PropConvertsToString
{
    ErrorBufferAllErrors *errors = [[ErrorBufferAllErrors alloc] init];
    STGroup *group = [STGroupFile newSTGroupFile];
    group.errMgr = [ErrorManager newErrorManagerWithListener:errors];
    NSString *aTemplate = @"<u.(name)>";
    ST *st = [[ST alloc] init:group template:aTemplate];
    [st add:@"u" value:[User newUser:1 name:@"parrt"]];
    [st addInt:@"name" value:100];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
/*    
    STRuntimeMessage *msg = (STRuntimeMessage *)[errors.errors objectAtIndex:0];
    STNoSuchPropertyException *e = (STNoSuchPropertyException *)msg.cause;
    //  [self assertEquals:@"org.stringtemplate.v4.test.BaseTest$User.100" arg1:e.propertyName];
    expected = @"org.stringtemplate.v4.test.BaseTest$User.100";
    result = e.propertyName;
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
 */
}

- (void) test16Include
{
    NSString *aTemplate = @"load <box()>;";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st.impl.nativeGroup defineTemplate:@"box" template:@"kewl\ndaddy"];
    NSString *expected = @"load kewl\ndaddy;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test17IncludeWithArg
{
    NSString *aTemplate = @"load <box(\"arg\")>;";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st.impl.nativeGroup defineTemplate:@"box" argsS:@"x" template:@"kewl <x> daddy"];
    [st.impl dump];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"load kewl arg daddy;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test18IncludeWithArg2
{
    NSString *aTemplate = @"load <box(\"arg\", foo())>;";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st.impl.nativeGroup defineTemplate:@"box" argsS:@"x,y" template:@"kewl <x> <y> daddy"];
    [st.impl.nativeGroup defineTemplate:@"foo" template:@"blech"];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"load kewl arg blech daddy;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    
}

#ifdef DONTUSEYET
- (void) test18aPassThruWithDefaultValue
{
    // should not set y when it sees "no value" from above
    NSString *templates = @"a(x,y) ::= \"<b(...)>\"\nb(x,y={99}) ::= \"<x><y>\"\n";
    STGroupString *group = [STGroupString newSTGroupString:templates];
    ST *a = [group getInstanceOf:@"a"];
    [a add:@"x" value:@"x"];
    NSString *expected = @"x99";
    NSString *result = [a render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test18bPassThruWithDefaultValueThatLacksDefinitionAbove
{
    // should not set y when it sees "no definition" from above
    NSString *templates = @"a(x) ::= \"<b(...)>\"\nb(x,y={99}) ::= \"<x><y>\"\n";
    STGroupString *group = [STGroupString newSTGroupString:templates];
    ST *a = [group getInstanceOf:@"a"];
    [a add:@"x" value:@"x"];
    NSString *expected = @"x99";
    NSString *result = [a render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test18cPassThruPartialArgs
{
    NSString *templates = @"a(x,y) ::= \"<b(y={99},...)>\"\nb(x,y) ::= \"<x><y>\"\n";
    STGroupString *group = [STGroupString newSTGroupString:templates];
    ST *a = [group getInstanceOf:@"a"];
    [a add:@"x" value:@"x"];
    [a add:@"y" value:@"y"];
    NSString *expected = @"x99";
    NSString *result = [a render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test18dPassThruNoMissingArgs
{
    NSString *templates = @"a(x,y) ::= \"<b(y={99},x={1},...)>\"\nb(x,y) ::= \"<x><y>\"\n";
    STGroupString *group = [STGroupString newSTGroupString:templates];
    ST *a = [group getInstanceOf:@"a"];
    [a add:@"x" value:@"x"];
    [a add:@"y" value:@"y"];
    NSString *expected = @"199";
    NSString *result = [a render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}
#endif

- (void) test19IncludeWithNestedArgs
{
    NSString *aTemplate = @"load <box(foo(\"arg\"))>;";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st.impl.nativeGroup defineTemplate:@"box" argsS:@"y" template:@"kewl <y> daddy"];
    [st.impl.nativeGroup defineTemplate:@"foo" argsS:@"x" template:@"blech <x>"];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"load kewl blech arg daddy;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    
}

- (void) test20DefineTemplate
{
    STGroup *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"inc" argsS:@"x" template:@"<x>+1"];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi TerTomSumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    
}

- (void) test21Map
{
    STGroup *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"inc" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name:inc()>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi [Ter][Tom][Sumana]!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    
}

- (void) test22IndirectMap
{
    STGroup *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"inc" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"test" argsS:@"t,name" template:@"<name:(t)()>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"t" value:@"inc"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"[Ter][Tom][Sumana]!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test23MapWithExprAsTemplateName
{
    NSString *aTemplate = [NSString stringWithFormat:@"%@%@%@",
                           @"d ::= [\"foo\":\"bold\"]\n",
                           @"test(name) ::= \"<name:(d.foo)()>\"\n",
                           @"bold(x) ::= <<*<x>*>>\n"];
    [BaseTest writeFile:tmpdir fileName:@"t.stg" content:aTemplate];
    STGroupFile *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    //    [st.impl dump];
    NSString *expected = @"*Ter**Tom**Sumana*";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test24ParallelMap
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"test" argsS:@"names,phones" template:@"hi <names,phones:{n,p | <n>:<p>;}>"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    [st add:@"phones" value:@"x5001"];
    [st add:@"phones" value:@"x5002"];
    [st add:@"phones" value:@"x5003"];
    NSString *expected = @"hi Ter:x5001;Tom:x5002;Sumana:x5003;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test25ParallelMapWith3Versus2Elements
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"test" argsS:@"names,phones" template:@"hi <names,phones:{n,p | <n>:<p>;}>"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    [st add:@"phones" value:@"x5001"];
    [st add:@"phones" value:@"x5002"];
    NSString *expected = @"hi Ter:x5001;Tom:x5002;Sumana:;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test26ParallelMapThenMap
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"bold" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"test" argsS:@"names,phones" template:@"hi <names,phones:{n,p | <n>:<p>;}:bold()>"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    [st add:@"phones" value:@"x5001"];
    [st add:@"phones" value:@"x5002"];
    NSString *expected = @"hi [Ter:x5001;][Tom:x5002;][Sumana:;]";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test27MapThenParallelMap
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"bold" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"test" argsS:@"names,phones" template:@"hi <[names:bold()],phones:{n,p | <n>:<p>;}>"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    [st add:@"phones" value:@"x5001"];
    [st add:@"phones" value:@"x5002"];
    NSString *expected = @"hi [Ter]:x5001;[Tom]:x5002;[Sumana]:;";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test28MapIndexes
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"inc" argsS:@"x,i" template:@"<i>:<x>"];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name:{n|<inc(n,i)>}; separator=\", \">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"1:Ter, 2:Tom, 3:Sumana";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test29MapIndexes2
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name:{n | <i>:<n>}; separator=\", \">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"1:Ter, 2:Tom, 3:Sumana";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test30MapSingleValue
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"a" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name:a()>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"hi [Ter]!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test31MapNullValue
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"a" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name:a()>!"];
    ST *st = [group getInstanceOf:@"test"];
    NSString *expected = @"hi !";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test32MapNullValueInList
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; separator=\", \">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"Ter, Tom, Sumana";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test33RepeatedMap
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"a" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"b" argsS:@"x" template:@"(<x>)"];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name:a():b()>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi ([Ter])([Tom])([Sumana])!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test34RoundRobinMap
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"a" argsS:@"x" template:@"[<x>]"];
    [group defineTemplate:@"b" argsS:@"x" template:@"(<x>)"];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name:a(),b()>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi [Ter](Tom)[Sumana]!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    
}

- (void) test35TrueCond
{
    NSString *aTemplate = @"<if(name)>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test36EmptyIFTemplate
{
    NSString *aTemplate = @"<if(x)>fail<elseif(name)><endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test37CondParens
{
    NSString *aTemplate = @"<if(!(x||y)&&!z)>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test38FalseCond
{
    NSString *aTemplate = @"<if(name)>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test39FalseCond2
{
    NSString *aTemplate = @"<if(name)>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:nil];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test40FalseCondWithFormalArgs
{
    NSString *dir = [BaseTest randomDir];
    NSString *groupFile = [NSString stringWithFormat:@"a(scope) ::= <<%@foo%@    <if(scope)>oops<endif>%@bar%@>>", newline, newline, newline, newline];
    [BaseTest writeFile:dir fileName:@"group.stg" content:groupFile];
    STGroupFile *group = [STGroupFile newSTGroupFile:[dir stringByAppendingPathComponent:@"group.stg"]];
    ST *st = [group getInstanceOf:@"a"];
    [st.impl dump];
    NSString *expected = [NSString stringWithFormat:@"foo%@bar", newline];
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test41ElseIf2
{
    NSString *aTemplate = @"<if(x)>fail1<elseif(y)>fail2<elseif(z)>works<else>fail3<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"z" value:@"blort"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test42ElseIf3
{
    NSString *aTemplate = @"<if(x)><elseif(y)><elseif(z)>works<else><endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"z" value:@"blort"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test43NotTrueCond
{
    NSString *aTemplate = @"<if(!name)>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test44NotFalseCond
{
    NSString *aTemplate = @"<if(!name)>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test45ParensInConditonal
{
    NSString *aTemplate = @"<if((a||b)&&(c||d))>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st addInt:@"a" value:YES];
    [st addInt:@"b" value:YES];
    [st addInt:@"c" value:YES];
    [st addInt:@"d" value:YES];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test46ParensInConditonal2
{
    NSString *aTemplate = @"<if((!a||b)&&!(c||d))>broken<else>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st addInt:@"a" value:YES];
    [st addInt:@"b" value:YES];
    [st addInt:@"c" value:YES];
    [st addInt:@"d" value:YES];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test47TrueCondWithElse
{
    NSString *aTemplate = @"<if(name)>works<else>fail<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test48FalseCondWithElse
{
    NSString *aTemplate = @"<if(name)>fail<else>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test49ElseIf
{
    NSString *aTemplate = @"<if(name)>fail<elseif(id)>works<else>fail<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"id" value:@"2DF3DF"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test50ElseIfNoElseAllFalse
{
    NSString *aTemplate = @"<if(name)>fail<elseif(id)>fail<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test51ElseIfAllExprFalse
{
    NSString *aTemplate = @"<if(name)>fail<elseif(id)>fail<else>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test52Or
{
    NSString *aTemplate = @"<if(name||notThere)>works<else>fail<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test53MapConditionAndEscapeInside
{
    NSString *aTemplate = @"<if(m.name)>works \\\\<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    AMutableDictionary *m = [AMutableDictionary dictionaryWithCapacity:16];
    [m setObject:@"name" forKey:@"Ter"];
    [st add:@"m" value:m];
    NSString *expected = @"works \\";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test54And
{
    NSString *aTemplate = @"<if(name&&notThere)>fail<else>works<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test55AndNot
{
    NSString *aTemplate = @"<if(name&&!notThere)>works<else>fail<endif>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"works";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test56CharLiterals
{
    ST *st = [ST newSTWithTemplate:@"Foo <\\n><\\n><\\t> bar\n"];
    StringWriter *sw = [[StringWriter alloc] init];
    [st write:[[AutoIndentWriter alloc] init:sw newline:@"\n"]]; // force \n as newline
    NSString *result = [sw description];
    NSString *expected = @"Foo \n\n\t bar\n";     // expect \n in output
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    st = [ST newSTWithTemplate:[NSString stringWithFormat:@"Foo <\\n><\\t> bar%@", newline]];
    sw = [[StringWriter alloc] init];
    [st write:[[AutoIndentWriter alloc] init:sw newline:@"\n"]]; // force \n as newline
    expected = @"Foo \n\t bar\n";     // expect \n in output
    result = [sw description];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    st = [ST newSTWithTemplate:@"Foo<\\ >bar<\\n>"];
    sw = [[StringWriter alloc] init];
    [st write:[[AutoIndentWriter alloc] init:sw newline:@"\n"]]; // force \n as newline
    result = [sw description];
    expected = @"Foo bar\n"; // forced \n
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

#ifdef DONTUSENOMO
- (void) test57UnicodeLiterals
{
    ST *st = [ST newSTWithTemplate:@"Foo <\\uFEA5><\\n><\\u00C2> bar\n"];
    NSString *expected = [NSString stringWithFormat:@"Foo ?%@å bar%@", newline, newline];
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    st = [ST newSTWithTemplate:[NSString stringWithFormat:@"Foo <\\uFEA5><\\n><\\u00C2> bar%@", newline]];
    expected = [NSString stringWithFormat:@"Foo ?%@å bar%@", newline, newline];
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    st = [ST newSTWithTemplate:@"Foo<\\ >bar<\\n>"];
    expected = @"Foo bar\n";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}
#endif

- (void) test58SubtemplateExpr
{
    NSString *aTemplate = @"<{name\n}>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSString *expected = [@"name" stringByAppendingString:newline];
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test59Separator
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n | case <n>}; separator=\", \">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    NSString *expected = @"case Ter, case Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test60SeparatorInList
{
    STGroupFile *group = [STGroupFile newSTGroupFile];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n | case <n>}; separator=\", \">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:[TestCoreBasics_Anon3 newAnon]];
    NSString *expected = @"case Ter, case Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

/**
 * (...) forces early eval to string. early eval <(x)> using new
 * STWriter derived from type of current STWriter. e.g., AutoIndentWriter.
 *
 * early eval ignores indents; mostly for simply strings
 */
- (void) test61EarlyEvalIndent
{
    NSString *aTemplates = @"t() ::= <<  abc>>\nmain() ::= <<\n<t()>\n<(t())>\n  <t()>\n  <(t())>\n>>\n";
    [BaseTest writeFile:tmpdir fileName:@"t.stg" content:aTemplates];
    STGroupFile *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *st = [group getInstanceOf:@"main"];
    NSString *result = [st render];
    NSString *expected = @"  abc\n  abc\n    abc\n    abc";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

/*
 *  early eval ignores indents; mostly for simply strings
 */
- (void) test62EarlyEvalNoIndent
{
    NSString *aTemplate = @"t() ::= <<  abc>>\nmain() ::= <<\n<t()>\n<(t())>\n  <t()>\n  <(t())>\n>>\n";
    [BaseTest writeFile:tmpdir fileName:@"t.stg" content:aTemplate];
    STGroupFile *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *st = [group getInstanceOf:@"main"];
    StringWriter *sw = [StringWriter new];
    // NoIndentWriter *w = [NoIndentWriter newNoIdentWriterWithWriter:sw];
    NoIndentWriter *w = [NoIndentWriter newNoIdentWriter];
    [st write:w];
    NSString *result = [sw description];
    NSString *expected = @"abc\nabc\nabc\nabc";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test63ArrayOfTemplates
{
    NSString *template = @"<foo>!";
    ST *st = [ST newSTWithTemplate:template];
    //ST[] *t = new ST[] {[[ST newSTWithTemplate:@"hi"] newSTWithTemplate:@"mom"]};
    NSArray *t = [NSArray arrayWithObjects:[ST newSTWithTemplate:@"hi"], [ST newSTWithTemplate:@"mom"], nil];
    [st add:@"foo" value:t];
    NSString *expected = @"himom!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test64ArrayOfTemplatesInTemplate
{
    NSString *template = @"<foo>!";
    ST *st = [ST newSTWithTemplate:template];
    //ST[] t = new ST[] {new ST(@"hi"), new ST(@"mom")};
    NSArray *t = [NSArray arrayWithObjects:[ST newSTWithTemplate:@"hi"], [ST newSTWithTemplate:@"mom"], nil];
    [st add:@"foo" value:t];
     ST *wrapper = [ST newSTWithTemplate:@"<x>"];
    [wrapper add:@"x" value:st];
    NSString *expected = @"himom!";
    NSString *result = [wrapper render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test65ListOfTemplates
{
    NSString *template = @"<foo>!";
    ST *st = [ST newSTWithTemplate:template];
    //List<ST> t = new ArrayList<ST>() {{add(new ST(@"hi")); add(new ST(@"mom"));}};
    NSArray *t = [NSArray arrayWithObjects:[ST newSTWithTemplate:@"hi"], [ST newSTWithTemplate:@"mom"], nil];
    [st add:@"foo" value:t];
    NSString *expected = @"himom!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test66ListOfTemplatesInTemplate
{
    NSString *template = @"<foo>!";
    ST *st = [ST newSTWithTemplate:template];
    //List<ST> t = new ArrayList<ST>() {{add(new ST(@"hi")); add(new ST(@"mom"));}};
    NSArray *t = [NSArray arrayWithObjects:[ST newSTWithTemplate:@"hi"], [ST newSTWithTemplate:@"mom"], nil];
    [st add:@"foo" value:t];
    ST *wrapper = [ST newSTWithTemplate:@"<x>"];
    [wrapper add:@"x" value:st];
    NSString *expected = @"himom!";
    NSString *result = [wrapper render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) playing
{
    NSString *aTemplate = @"<a:t(x,y),u()>";
    ST *st = [ST newSTWithTemplate:aTemplate];
    [st.impl dump];
}

@end
