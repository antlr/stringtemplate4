#import "TestRegions.h"

@implementation TestRegions

- (void) testEmbeddedRegion {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[@"a() ::= <<\n" stringByAppendingString:@"[<@r>bar<@end>]\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"group.stg" param2:groupFile];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = @"[bar]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testRegion {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[@"a() ::= <<\n" stringByAppendingString:@"[<@r()>]\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"group.stg" param2:groupFile];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = @"[]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testDefineRegionInSubgroup {
  NSString * dir = [self randomDir];
  NSString * g1 = @"a() ::= <<[<@r()>]>>\n";
  [self writeFile:dir param1:@"g1.stg" param2:g1];
  NSString * g2 = @"@a.r() ::= <<foo>>\n";
  [self writeFile:dir param1:@"g2.stg" param2:g2];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g1.stg"]] autorelease];
  STGroup * group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g2.stg"]] autorelease];
  [group2 importTemplates:group1];
  ST * st = [group2 getInstanceOf:@"a"];
  NSString * expected = @"[foo]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testDefineRegionInSubgroupThatRefsSuper {
  NSString * dir = [self randomDir];
  NSString * g1 = @"a() ::= <<[<@r>foo<@end>]>>\n";
  [self writeFile:dir param1:@"g1.stg" param2:g1];
  NSString * g2 = @"@a.r() ::= <<(<@super.r()>)>>\n";
  [self writeFile:dir param1:@"g2.stg" param2:g2];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g1.stg"]] autorelease];
  STGroup * group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g2.stg"]] autorelease];
  [group2 importTemplates:group1];
  ST * st = [group2 getInstanceOf:@"a"];
  NSString * expected = @"[(foo)]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testDefineRegionInSubgroup2 {
  NSString * dir = [self randomDir];
  NSString * g1 = @"a() ::= <<[<@r()>]>>\n";
  [self writeFile:dir param1:@"g1.stg" param2:g1];
  NSString * g2 = @"@a.r() ::= <<foo>>>\n";
  [self writeFile:dir param1:@"g2.stg" param2:g2];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g1.stg"]] autorelease];
  STGroup * group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g2.stg"]] autorelease];
  [group1 importTemplates:group2];
  ST * st = [group1 getInstanceOf:@"a"];
  NSString * expected = @"[]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testDefineRegionInSameGroup {
  NSString * dir = [self randomDir];
  NSString * g = [@"a() ::= <<[<@r()>]>>\n" stringByAppendingString:@"@a.r() ::= <<foo>>\n"];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = @"[foo]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testAnonymousTemplateInRegion {
  NSString * dir = [self randomDir];
  NSString * g = [[[@"a() ::= <<[<@r()>]>>\n" stringByAppendingString:@"@a.r() ::= <<\n"] stringByAppendingString:@"<[\"foo\"]:{x|<x>}>\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = @"[foo]";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testCantDefineEmbeddedRegionAgain {
  NSString * dir = [self randomDir];
  NSString * g = [@"a() ::= <<[<@r>foo<@end>]>>\n" stringByAppendingString:@"@a.r() ::= <<bar>>\n"];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [group setListener:errors];
  [group load];
  NSString * expected = [@"g.stg 2:3: region a.r is embedded and thus already implicitly defined" stringByAppendingString:newline];
  NSString * result = [errors description];
  [self assertEquals:expected param1:result];
}

- (void) testCantDefineEmbeddedRegionAgainInTemplate {
  NSString * dir = [self randomDir];
  NSString * g = [[[[[@"a() ::= <<\n" stringByAppendingString:@"[\n"] stringByAppendingString:@"<@r>foo<@end>\n"] stringByAppendingString:@"<@r()>"] stringByAppendingString:@"]\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [group setListener:errors];
  [group load];
  NSString * expected = [@"g.stg 3:2: redefinition of region a.r" stringByAppendingString:newline];
  NSString * result = [errors description];
  [self assertEquals:expected param1:result];
}

- (void) testMissingRegionName {
  NSString * dir = [self randomDir];
  NSString * g = @"@t.() ::= \"\"\n";
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ErrorBuffer * errors = [[[ErrorBuffer alloc] init] autorelease];
  [group setListener:errors];
  [group load];
  NSString * expected = [@"g.stg 1:3: missing ID at '('" stringByAppendingString:newline];
  NSString * result = [errors description];
  [self assertEquals:expected param1:result];
}

- (void) testIndentBeforeRegionIsIgnored {
  NSString * dir = [self randomDir];
  NSString * g = [[[[@"a() ::= <<[\n" stringByAppendingString:@"  <@r>\n"] stringByAppendingString:@"  foo\n"] stringByAppendingString:@"  <@end>\n"] stringByAppendingString:@"]>>\n"];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = [[@"[\n" stringByAppendingString:@"  foo\n"] stringByAppendingString:@"]"];
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testRegionOverrideStripsNewlines {
  NSString * dir = [self randomDir];
  NSString * g = [[[@"a() ::= \"X<@r()>Y\"" stringByAppendingString:@"@a.r() ::= <<\n"] stringByAppendingString:@"foo\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  NSString * sub = [@"@a.r() ::= \"A<@super.r()>B\"" stringByAppendingString:newline];
  [self writeFile:dir param1:@"sub.stg" param2:sub];
  STGroupFile * subGroup = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/sub.stg"]] autorelease];
  [subGroup importTemplates:group];
  ST * st = [subGroup getInstanceOf:@"a"];
  NSString * result = [st render];
  NSString * expecting = @"XAfooBY";
  [self assertEquals:expecting param1:result];
}

- (void) testRegionOverrideRefSuperRegion {
  NSString * dir = [self randomDir];
  NSString * g = [@"a() ::= \"X<@r()>Y\"" stringByAppendingString:@"@a.r() ::= \"foo\""] + newline;
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  NSString * sub = [@"@a.r() ::= \"A<@super.r()>B\"" stringByAppendingString:newline];
  [self writeFile:dir param1:@"sub.stg" param2:sub];
  STGroupFile * subGroup = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/sub.stg"]] autorelease];
  [subGroup importTemplates:group];
  ST * st = [subGroup getInstanceOf:@"a"];
  NSString * result = [st render];
  NSString * expecting = @"XAfooBY";
  [self assertEquals:expecting param1:result];
}

- (void) testRegionOverrideRefSuperRegion3Levels {
  NSString * dir = [self randomDir];
  NSString * g = [@"a() ::= \"X<@r()>Y\"" stringByAppendingString:@"@a.r() ::= \"foo\""] + newline;
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  NSString * sub = [@"@a.r() ::= \"<@super.r()>2\"" stringByAppendingString:newline];
  [self writeFile:dir param1:@"sub.stg" param2:sub];
  STGroupFile * subGroup = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/sub.stg"]] autorelease];
  [subGroup importTemplates:group];
  NSString * subsub = [@"@a.r() ::= \"<@super.r()>3\"" stringByAppendingString:newline];
  [self writeFile:dir param1:@"subsub.stg" param2:subsub];
  STGroupFile * subSubGroup = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/subsub.stg"]] autorelease];
  [subSubGroup importTemplates:subGroup];
  ST * st = [subSubGroup getInstanceOf:@"a"];
  NSString * result = [st render];
  NSString * expecting = @"Xfoo23Y";
  [self assertEquals:expecting param1:result];
}

- (void) testRegionOverrideRefSuperImplicitRegion {
  NSString * dir = [self randomDir];
  NSString * g = [@"a() ::= \"X<@r>foo<@end>Y\"" stringByAppendingString:newline];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  NSString * sub = [@"@a.r() ::= \"A<@super.r()>\"" stringByAppendingString:newline];
  [self writeFile:dir param1:@"sub.stg" param2:sub];
  STGroupFile * subGroup = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/sub.stg"]] autorelease];
  [subGroup importTemplates:group];
  ST * st = [subGroup getInstanceOf:@"a"];
  NSString * result = [st render];
  NSString * expecting = @"XAfooY";
  [self assertEquals:expecting param1:result];
}

- (void) testUnknownRegionDefError {
  NSString * dir = [self randomDir];
  NSString * g = [[[@"a() ::= <<\n" stringByAppendingString:@"X<@r()>Y"] stringByAppendingString:@">>\n"] stringByAppendingString:@"@a.q() ::= \"foo\""] + newline;
  STErrorListener * errors = [[[ErrorBuffer alloc] init] autorelease];
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  [group setListener:errors];
  ST * st = [group getInstanceOf:@"a"];
  [st render];
  NSString * result = [errors description];
  NSString * expecting = [@"g.stg 4:3: template a doesn't have a region called q" stringByAppendingString:newline];
  [self assertEquals:expecting param1:result];
}

- (void) testSuperRegionRefMissingOk {
  NSString * dir = [self randomDir];
  NSString * g = [@"a() ::= \"X<@r()>Y\"" stringByAppendingString:@"@a.r() ::= \"foo\""] + newline;
  [self writeFile:dir param1:@"g.stg" param2:g];
  STGroupFile * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  NSString * sub = [@"@a.r() ::= \"A<@super.q()>B\"" stringByAppendingString:newline];
  STErrorListener * errors = [[[ErrorBuffer alloc] init] autorelease];
  [group setListener:errors];
  [self writeFile:dir param1:@"sub.stg" param2:sub];
  STGroupFile * subGroup = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/sub.stg"]] autorelease];
  [subGroup importTemplates:group];
  ST * st = [subGroup getInstanceOf:@"a"];
  NSString * result = [st render];
  NSString * expecting = @"XABY";
  [self assertEquals:expecting param1:result];
}

- (void) testEmbeddedRegionOnOneLine {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[[[@"a() ::= <<\n" stringByAppendingString:@"[\n"] stringByAppendingString:@"  <@r>bar<@end>\n"] stringByAppendingString:@"]\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"group.stg" param2:groupFile];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  [st.impl dump];
  NSString * expected = [[[@"[" stringByAppendingString:newline] stringByAppendingString:@"  bar"] + newline stringByAppendingString:@"]"];
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testEmbeddedRegionTagsOnSeparateLines {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[[[[[@"a() ::= <<\n" stringByAppendingString:@"[\n"] stringByAppendingString:@"  <@r>\n"] stringByAppendingString:@"  bar\n"] stringByAppendingString:@"  <@end>\n"] stringByAppendingString:@"]\n"] stringByAppendingString:@">>\n"];
  [self writeFile:dir param1:@"group.stg" param2:groupFile];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"a"];
  NSString * expected = [[[@"[" stringByAppendingString:newline] stringByAppendingString:@"  bar"] + newline stringByAppendingString:@"]"];
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

@end
