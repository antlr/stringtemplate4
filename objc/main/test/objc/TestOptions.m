
#import "TestOptions.h"

@implementation TestOptions

- (void) test01Separator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; separator=\", \">!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, Tom, Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test02SeparatorWithSpaces
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; separator= \", \">!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    //[System.out println:[st.impl.ast toStringTree]];
    NSLog( @"%@", [st.impl.ast toStringTree]);
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, Tom, Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test03AttrSeparator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name,sep" template:@"hi <name; separator=sep>!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"sep" value:@", "];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, Tom, Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test04IncludeSeparator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"foo" template:@"|"];
    [group defineTemplate:@"test" argsS:@"name,sep" template:@"hi <name; separator=foo()>!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"sep" value:@", "];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter|Tom|Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test05SubtemplateSeparator
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name,sep" template:@"hi <name; separator={<sep> _}>!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"sep" value:@","];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, _Tom, _Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test06SeparatorWithNullFirstValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; null=\"n/a\", separator=\", \">!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Tom"];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi n/a, Tom, Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test07SeparatorWithNull2ndValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"hi <name; null=\"n/a\", separator=\", \">!" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st.impl dump];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"hi Ter, n/a, Sumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test08NullValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; null=\"n/a\">" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:nil];
    NSString *expected = @"n/a";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test09ListApplyWithNullValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name:{n | <n>}; null=\"n/a\">" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"Tern/aSumana";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test10DoubleListApplyWithNullValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name:{n | <n>}:{n | [<n>]}; null=\"n/a\">" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    [st add:@"name" value:nil];
    [st add:@"name" value:@"Sumana"];
    NSString *expected = @"[Ter]n/a[Sumana]";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test11MissingValueAndNullOption
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; null=\"n/a\">" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    NSString *expected = @"n/a";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test12OptionDoesntApplyToNestedTemplate
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"foo" template:@"<zippo>"];
    [group defineTemplate:@"test" argsS:@"zippo" template:@"<foo(); null=\"n/a\">" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"zippo" value:nil];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
}

- (void) test13IllegalOption
{
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroup *group = [STGroup newSTGroup];
    [group setListener:errors];
    [group defineTemplate:@"test" argsS:@"name" template:@"<name; bad=\"ugly\">" templateToken:nil];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"name" value:@"Ter"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but have \"%@\"", expected, result);
    expected = @"[test 1:7: no such option: bad]";
    STAssertTrue( [expected isEqualTo:[errors.errors description]], @"Expected \"%@\" but have \"%@\"", expected, [errors.errors description]);
}

@end
