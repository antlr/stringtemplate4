#import "TestRenderers.h"

@implementation TestRenderers

- (void) test01RendererForGroup
{
    NSString *templates = @"dateThing(created) ::= \"datetime: <created>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSDate class] r:[DateRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"dateThing"];
    NSCalendar *gregorian = [NSCalendar currentCalendar];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    [comps setYear:2005];
    [comps setMonth:07];
    [comps setDay:05];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate *aDate = [gregorian dateFromComponents:comps];
    [st add:@"created" value:aDate];
    NSString *expecting = @"datetime: 7/5/05 12:00 AM";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test02RendererWithFormat {
    NSString *templates = @"dateThing(created) ::= << date: <created; format=\"yyyy.MM.dd\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSDate class] r:[DateRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"dateThing"];
    NSCalendar *gregorian = [NSCalendar currentCalendar];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    [comps setYear:2005];
    [comps setMonth:07];
    [comps setDay:05];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate *aDate = [gregorian dateFromComponents:comps];
    [st add:@"created" value:aDate];
    NSString *expecting = @" date: 2005.07.05 ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test03RendererWithPredefinedFormat {
    NSString *templates = @"dateThing(created) ::= << datetime: <created; format=\"short\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSDate class] r:[DateRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"dateThing"];
    NSCalendar *gregorian = [NSCalendar currentCalendar];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    [comps setYear:2005];
    [comps setMonth:07];
    [comps setDay:05];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate *aDate = [gregorian dateFromComponents:comps];
    [st add:@"created" value:aDate];
    NSString *expecting = @" datetime: 7/5/05 12:00 AM ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test04RendererWithPredefinedFormat2 {
    NSString *templates = @"dateThing(created) ::= << datetime: <created; format=\"full\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSDate class] r:[DateRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"dateThing"];
    NSCalendar *gregorian = [NSCalendar currentCalendar];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    [comps setYear:2005];
    [comps setMonth:07];
    [comps setDay:05];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate *aDate = [gregorian dateFromComponents:comps];
    [st add:@"created" value:aDate];
    NSString *expecting = @" datetime: Tuesday, July 5, 2005 12:00:00 AM PT ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test05RendererWithPredefinedFormat3 {
    NSString *templates = @"dateThing(created) ::= << date: <created; format=\"date:medium\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSDate class] r:[DateRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"dateThing"];
    NSCalendar *gregorian = [NSCalendar currentCalendar];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    [comps setYear:2005];
    [comps setMonth:07];
    [comps setDay:05];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate *aDate = [gregorian dateFromComponents:comps];
    [st add:@"created" value:aDate];
    NSString *expecting = @" date: Jul 5, 2005 ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test06RendererWithPredefinedFormat4 {
    NSString *templates = @"dateThing(created) ::= << time: <created; format=\"time:medium\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSDate class] r:[DateRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"dateThing"];
    NSCalendar *gregorian = [NSCalendar currentCalendar];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    [comps setYear:2005];
    [comps setMonth:07];
    [comps setDay:05];
    [comps setHour:0];
    [comps setMinute:0];
    [comps setSecond:0];
    NSDate *aDate = [gregorian dateFromComponents:comps];
    [st add:@"created" value:aDate];
    NSString *expecting = @" time: 12:00:00 AM ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test07StringRendererWithPrintfFormat {
    NSString *templates = @"foo(x) ::= << <x; format=\"%6s\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[NSString class] r:[StringRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:@"hi"];
    NSString *expecting = @"     hi ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test08NumberRendererWithPrintfFormat {
    NSString *templates = @"foo(x,y) ::= << <x; format=\"%d\"> <y; format=\"%2.3f\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[ACNumber class] r:[NumberRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[ACNumber numberWithInteger:-2100]];
    [st add:@"y" value:[ACNumber numberWithDouble:3.14159]];
    NSString *expecting = @" -2100 3.142 ";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test09InstanceofRenderer {
    NSString *templates = @"numberThing(x,y,z) ::= \"numbers: <x>, <y>; <z>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[ACNumber class] r:[NumberRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"numberThing"];
    [st add:@"x" value:[ACNumber numberWithInteger:-2100]];
    [st add:@"y" value:[ACNumber numberWithDouble:3.14159]];
    [st add:@"z" value:@"hi"];
    NSString *expecting = @"numbers: -2100, 3.14159; hi";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test10LocaleWithNumberRenderer {
    NSString *templates = @"foo(x,y) ::= << <x; format=\"%,d\"> <y; format=\"%,2.3f\"> >>\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]];
    [group registerRenderer:[ACNumber class] r:[NumberRenderer newRenderer]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[ACNumber numberWithInteger:-2100]];
    [st add:@"y" value:[ACNumber numberWithDouble:3.14159]];
    [st.impl dump];
    NSString *result = [st render:[[NSLocale alloc] initWithLocaleIdentifier:@"pl"]];
    NSString *expecting = @" -2\u00A0100 3,142 ";
    [self assertEquals:expecting result:result];
}

- (void) test11RendererWithFormatAndList {
    NSString *template = @"The names: <names; format=\"upper\">";
    STGroup *group = [STGroup newSTGroup];
    [group registerRenderer:[NSString class] r:[StringRenderer newRenderer]];
    ST *st = [ST newST:group template:template];
    st.groupThatCreatedThisInstance = group;
    [st add:@"names" value:@"ter"];
    [st add:@"names" value:@"tom"];
    [st add:@"names" value:@"sriram"];
    NSString *expecting = @"The names: TERTOMSRIRAM";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test12RendererWithFormatAndSeparator {
    NSString *template = @"The names: <names; separator=\" and \", format=\"upper\">";
    STGroup *group = [STGroup newSTGroup];
    [group registerRenderer:[NSString class] r:[StringRenderer newRenderer]];
    ST *st = [ST newST:group template:template];
    st.groupThatCreatedThisInstance = group;
    [st add:@"names" value:@"ter"];
    [st add:@"names" value:@"tom"];
    [st add:@"names" value:@"sriram"];
    NSString *expecting = @"The names: TER and TOM and SRIRAM";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

- (void) test13RendererWithFormatAndSeparatorAndNull {
    NSString *template = @"The names: <names; separator=\" and \", null=\"n/a\", format=\"upper\">";
    STGroup *group = [STGroup newSTGroup];
    [group registerRenderer:[NSString class] r:[StringRenderer newRenderer]];
    ST *st = [ST newST:group template:template];
    st.groupThatCreatedThisInstance = group;
    AMutableArray *names = [AMutableArray arrayWithCapacity:8];
    [names addObject:@"ter"];
    [names addObject:nil];
    [names addObject:@"sriram"];
    [st add:@"names" value:names];
    NSString *expecting = @"The names: TER and N/A and SRIRAM";
    NSString *result = [st render];
    [self assertEquals:expecting result:result];
}

@end
