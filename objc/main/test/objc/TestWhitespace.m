#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "AutoIndentWriter.h"
#import "STGroup.h"
#import "ST.h"
#import "CompiledST.h"
#import "StringWriter.h"
#import "TestWhitespace.h"

@implementation TestWhitespace

- (void) test01TrimmedSubtemplates
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n | <n>}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @"TerTomSumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test02TrimmedSubtemplatesNoArgs
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" template:@"[<foo({ foo })>]"];
    [group defineTemplate:@"foo" argsS:@"x" template:@"<x>"];
    ST *st = [group getInstanceOf:@"test"];
    NSString *expected = @"[ foo ]";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test03TrimmedSubtemplatesArgs
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{x|  foo }>"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @" foo  foo  foo ";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test04TrimJustOneWSInSubtemplates
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n |  <n> }>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @" Ter  Tom  Sumana !";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test05TrimNewlineInSubtemplates
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n |\n<n>}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @"TerTomSumana!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test06LeaveNewlineOnEndInSubtemplates
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"<names:{n |\n<n>\n}>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @"Ter\nTom\nSumana\n!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test07TabBeforeEndInSubtemplates
{
    STGroup *group = [STGroup newSTGroup];
    [group defineTemplate:@"test" argsS:@"names" template:@"  <names:{n |\n    <n>\n  }>!"];
    ST *st = [group getInstanceOf:@"test"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sumana"];
    NSString *expected = @"    Ter\n    Tom\n    Sumana\n!";
    NSString *result = [st render];
    [st.impl dump];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test08EmptyExprAsFirstLineGetsNoOutput
{
    ST *st = [ST newSTWithTemplate:@"<users>\nend\n"];
    NSString *expected = @"end\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test09EmptyLineWithIndent
{
    ST *st = [ST newSTWithTemplate:@"begin\n    \nend\n"];
    NSString *expected = @"begin\n\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test10EmptyLine
{
    ST *st = [ST newSTWithTemplate:@"begin\n\nend\n"];
    NSString *expected = @"begin\n\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test11SizeZeroOnLineByItselfGetsNoOutput
{
    ST *st = [ST newSTWithTemplate:@"begin\n<name>\n<users>\n<users>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test12SizeZeroOnLineWithIndentGetsNoOutput
{
    ST *st = [ST newSTWithTemplate:@"begin\n  <name>\n	<users>\n	<users>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test13SizeZeroOnLineWithMultipleExpr
{
    ST *st = [ST newSTWithTemplate:@"begin\n  <name>\n	<users><users>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test14IFExpr
{
    ST *st = [ST newSTWithTemplate:@"begin\n<if(x)><endif>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test15IndentedIFExpr
{
    ST *st = [ST newSTWithTemplate:@"begin\n    <if(x)><endif>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test16IFElseExpr
{
    ST *st = [ST newSTWithTemplate:@"begin\n<if(users)><else><endif>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test17IFOnMultipleLines
{
    ST *st = [ST newSTWithTemplate:@"begin\n<if(users)>\nfoo\n<else>\nbar\n<endif>\nend\n"];
    NSString *expected = @"begin\nbar\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test18NestedIFOnMultipleLines
{
    ST *st = [ST newSTWithTemplate:@"begin\n<if(x)>\n<if(y)>\nfoo\n<else>\nbar\n<endif>\n<endif>\nend\n"];
    [st add:@"x" value:@"x"];
    NSString *expected = @"begin\nbar\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test19LineBreak
{
    ST *st = [ST newSTWithTemplate:@"Foo <\\\\>\n  \t  bar\n"];
    StringWriter *sw = [StringWriter newWriter];
    [st write:[AutoIndentWriter newWriter:sw newLine:@"\n"]];
    NSString *result = [sw description];
    NSString *expected = @"Foo bar\n";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test20LineBreak2
{
    ST *st = [ST newSTWithTemplate:@"Foo <\\\\>       \n  \t  bar\n"];
    StringWriter *sw = [StringWriter newWriter];
    [st write:[AutoIndentWriter newWriter:sw newLine:@"\n"]];
    NSString *result = [sw description];
    NSString *expected = @"Foo bar\n";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test21LineBreakNoWhiteSpace
{
    ST *st = [ST newSTWithTemplate:@"Foo <\\\\>\nbar\n"];
    StringWriter *sw = [StringWriter newWriter];
    [st write:[AutoIndentWriter newWriter:sw newLine:@"\n"]];
    NSString *result = [sw description];
    NSString *expected = @"Foo bar\n";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test22NewlineNormalizationInTemplateString
{
    ST *st = [ST newSTWithTemplate:@"Foo\r\nBar\n"];
    StringWriter *sw = [StringWriter newWriter];
    [st write:[AutoIndentWriter newWriter:sw newLine:@"\n"]];
    NSString *result = [sw description];
    NSString *expected = @"Foo\nBar\n";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test23NewlineNormalizationInTemplateStringPC
{
    ST *st = [ST newSTWithTemplate:@"Foo\r\nBar\n"];
    StringWriter *sw = [StringWriter newWriter];
    [st write:[AutoIndentWriter newWriter:sw newLine:@"\r\n"]];
    NSString *result = [sw description];
    NSString *expected = @"Foo\r\nBar\r\n";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test24NewlineNormalizationInAttribute
{
    ST *st = [ST newSTWithTemplate:@"Foo\r\n<name>\n"];
    [st add:@"name" value:@"a\nb\r\nc"];
    StringWriter *sw = [StringWriter newWriter];
    [st write:[AutoIndentWriter newWriter:sw newLine:@"\n"]];
    NSString *result = [sw description];
    NSString *expected = @"Foo\na\nb\nc\n";
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test25CommentOnlyLineGivesNoOutput
{
    ST *st = [ST newSTWithTemplate:@"begin\n<! ignore !>\nend\n"];
    NSString *expected = [NSString stringWithFormat:@"begin%@end%@", @"\n",  @"\n" ];
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test26CommentOnlyLineGivesNoOutput2
{
    ST *st = [ST newSTWithTemplate:@"begin\n    <! ignore !>\nend\n"];
    NSString *expected = @"begin\nend\n";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

@end
