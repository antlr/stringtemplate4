/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr and Alan Condit
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
#import "TestOptions.h"

@implementation TestOptions

- (void) test01Separator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; separator=\", \">!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, Tom, Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test02SeparatorWithSpaces
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; separator= \", \">!"];
    ST *st = [group getInstanceOf:@"test"];
    NSLog( @"%@", [st.impl.ast descriptionTree]);
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, Tom, Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test03AttrSeparator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name,sep" template:@"hi <name; separator=sep>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"sep" value:@", "];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, Tom, Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test04IncludeSeparator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"foo" template:@"|"];
    [group defineTemplate:@"test" argsS:@"name,sep" template:@"hi <name; separator=foo()>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"sep" value:@", "];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter|Tom|Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test05SubtemplateSeparator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name,sep" template:@"hi <name; separator={<sep> _}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"sep" value:@","];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, _Tom, _Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test06SeparatorWithNullFirstValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; null=\"n/a\", separator=\", \">!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi n/a, Tom, Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test07SeparatorWithNull2ndValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; null=\"n/a\", separator=\", \">!"];
    ST *st = [group getInstanceOf:@"test"];
    [st.impl dump];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, n/a, Sumana!";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test08NullValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; null=\"n/a\">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:nil];
    NSString *expected = @"n/a";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test09ListApplyWithNullValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name:{n | <n>}; null=\"n/a\">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"Tern/aSumana";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test10DoubleListApplyWithNullValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name:{n | <n>}:{n | [<n>]}; null=\"n/a\">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"[Ter]n/a[Sumana]";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test11MissingValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; null=\"n/a\">"];
    ST *st = [group getInstanceOf:@"test"];
    NSString *expected = @"n/a";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test12OptionDoesntApplyToNestedTemplate
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"foo" template:@"<zippo>"];
    [group defineTemplate:@"test" argsS:@"zippo" template:@"<foo(); null=\"n/a\">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"zippo" value:nil];
    NSString *expected = @"";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test13IllegalOption
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [STGroup newSTGroup];
    [group setListener:errors];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; bad=\"ugly\">"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
    expected = @"[test 1:7: no such option: bad]";
    result = [errors.errors description];
    [self assertEquals:expected result:result];
}

@end
