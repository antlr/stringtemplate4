#import "TestRenderers.h"

@implementation TestRenderers

- (void) testRendererForGroup {
  NSString * templates = @"dateThing(created) ::= \"datetime: <created>\"\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[GregorianCalendar class] param1:[[[DateRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"dateThing"];
  [st add:@"created" param1:[[[GregorianCalendar alloc] init:2005 param1:07 - 1 param2:05] autorelease]];
  NSString * expecting = @"datetime: 7/5/05 12:00 AM";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithFormat {
  NSString * templates = @"dateThing(created) ::= << date: <created; format=\"yyyy.MM.dd\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[GregorianCalendar class] param1:[[[DateRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"dateThing"];
  [st add:@"created" param1:[[[GregorianCalendar alloc] init:2005 param1:07 - 1 param2:05] autorelease]];
  NSString * expecting = @" date: 2005.07.05 ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithPredefinedFormat {
  NSString * templates = @"dateThing(created) ::= << datetime: <created; format=\"short\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[GregorianCalendar class] param1:[[[DateRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"dateThing"];
  [st add:@"created" param1:[[[GregorianCalendar alloc] init:2005 param1:07 - 1 param2:05] autorelease]];
  NSString * expecting = @" datetime: 7/5/05 12:00 AM ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithPredefinedFormat2 {
  NSString * templates = @"dateThing(created) ::= << datetime: <created; format=\"full\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[GregorianCalendar class] param1:[[[DateRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"dateThing"];
  [st add:@"created" param1:[[[GregorianCalendar alloc] init:2005 param1:07 - 1 param2:05] autorelease]];
  NSString * expecting = @" datetime: Tuesday, July 5, 2005 12:00:00 AM PDT ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithPredefinedFormat3 {
  NSString * templates = @"dateThing(created) ::= << date: <created; format=\"date:medium\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[GregorianCalendar class] param1:[[[DateRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"dateThing"];
  [st add:@"created" param1:[[[GregorianCalendar alloc] init:2005 param1:07 - 1 param2:05] autorelease]];
  NSString * expecting = @" date: Jul 5, 2005 ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithPredefinedFormat4 {
  NSString * templates = @"dateThing(created) ::= << time: <created; format=\"time:medium\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[GregorianCalendar class] param1:[[[DateRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"dateThing"];
  [st add:@"created" param1:[[[GregorianCalendar alloc] init:2005 param1:07 - 1 param2:05] autorelease]];
  NSString * expecting = @" time: 12:00:00 AM ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testStringRendererWithPrintfFormat {
  NSString * templates = @"foo(x) ::= << <x; format=\"%6s\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[NSString class] param1:[[[StringRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"foo"];
  [st add:@"x" param1:@"hi"];
  NSString * expecting = @"     hi ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testNumberRendererWithPrintfFormat {
  NSString * templates = @"foo(x,y) ::= << <x; format=\"%d\"> <y; format=\"%2.3f\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[NSNumber class] param1:[[[NumberRenderer alloc] init] autorelease]];
  [group registerRenderer:[NSNumber class] param1:[[[NumberRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"foo"];
  [st add:@"x" param1:-2100];
  [st add:@"y" param1:3.14159];
  NSString * expecting = @" -2100 3.142 ";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testInstanceofRenderer {
  NSString * templates = @"numberThing(x,y,z) ::= \"numbers: <x>, <y>; <z>\"\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[NSNumber class] param1:[[[NumberRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"numberThing"];
  [st add:@"x" param1:-2100];
  [st add:@"y" param1:3.14159];
  [st add:@"z" param1:@"hi"];
  NSString * expecting = @"numbers: -2100, 3.14159; hi";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testLocaleWithNumberRenderer {
  NSString * templates = @"foo(x,y) ::= << <x; format=\"%,d\"> <y; format=\"%,2.3f\"> >>\n";
  [self writeFile:tmpdir param1:@"t.stg" param2:templates];
  STGroup * group = [[[STGroupFile alloc] init:[tmpdir stringByAppendingString:@"/t.stg"]] autorelease];
  [group registerRenderer:[NSNumber class] param1:[[[NumberRenderer alloc] init] autorelease]];
  [group registerRenderer:[NSNumber class] param1:[[[NumberRenderer alloc] init] autorelease]];
  ST * st = [group getInstanceOf:@"foo"];
  [st add:@"x" param1:-2100];
  [st add:@"y" param1:3.14159];
  NSString * expecting = @" -2Ê100 3,142 ";
  NSString * result = [st render:[[[Locale alloc] init:@"pl"] autorelease]];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithFormatAndList {
  NSString * template = @"The names: <names; format=\"upper\">";
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group registerRenderer:[NSString class] param1:[[[StringRenderer alloc] init] autorelease]];
  ST * st = [[[ST alloc] init:template] autorelease];
  st.groupThatCreatedThisInstance = group;
  [st add:@"names" param1:@"ter"];
  [st add:@"names" param1:@"tom"];
  [st add:@"names" param1:@"sriram"];
  NSString * expecting = @"The names: TERTOMSRIRAM";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithFormatAndSeparator {
  NSString * template = @"The names: <names; separator=\" and \", format=\"upper\">";
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group registerRenderer:[NSString class] param1:[[[StringRenderer alloc] init] autorelease]];
  ST * st = [[[ST alloc] init:template] autorelease];
  st.groupThatCreatedThisInstance = group;
  [st add:@"names" param1:@"ter"];
  [st add:@"names" param1:@"tom"];
  [st add:@"names" param1:@"sriram"];
  NSString * expecting = @"The names: TER and TOM and SRIRAM";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

- (void) testRendererWithFormatAndSeparatorAndNull {
  NSString * template = @"The names: <names; separator=\" and \", null=\"n/a\", format=\"upper\">";
  STGroup * group = [[[STGroup alloc] init] autorelease];
  [group registerRenderer:[NSString class] param1:[[[StringRenderer alloc] init] autorelease]];
  ST * st = [[[ST alloc] init:template] autorelease];
  st.groupThatCreatedThisInstance = group;
  NSMutableArray * names = [[[NSMutableArray alloc] init] autorelease];
  [names addObject:@"ter"];
  [names addObject:nil];
  [names addObject:@"sriram"];
  [st add:@"names" param1:names];
  NSString * expecting = @"The names: TER and N/A and SRIRAM";
  NSString * result = [st render];
  [self assertEquals:expecting param1:result];
}

@end
