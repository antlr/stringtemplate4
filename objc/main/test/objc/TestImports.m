#import "TestImports.h"

@implementation TestImports

- (void) testImportTemplate {
  NSString *dir1 = [self randomDir];
  NSString *a = @"a() ::= <<dir1 a>>\n";
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 arg1:@"a.st" arg2:a];
  [self writeFile:dir1 arg1:@"b.st" arg2:b];
  NSString *dir2 = [self randomDir];
  a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir2 arg1:@"a.st" arg2:a];
  STGroup *group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup *group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group2 importTemplates:group1];
  ST *st = [group2 getInstanceOf:@"b"];
  NSString *expected = @"dir1 b";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
  st = [group2 getInstanceOf:@"a"];
  expected = @" dir1 b ";
  result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testImportStatementWithDir {
  NSString *dir1 = [[self randomDir] stringByAppendingString:@"/dir1"];
  NSString *dir2 = [[self randomDir] stringByAppendingString:@"/dir2"];
  NSString *a = [[[@"import \"" stringByAppendingString:dir2] stringByAppendingString:@"\"\n"] stringByAppendingString:@"a() ::= <<dir1 a>>\n"];
  [self writeFile:dir1 arg1:@"a.stg" arg2:a];
  a = @"a() ::= <<dir2 a>>\n";
  NSString *b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir2 arg1:@"a.st" arg2:a];
  [self writeFile:dir2 arg1:@"b.st" arg2:b];
  STGroup *group = [[[STGroupFile alloc] init:[dir1 stringByAppendingString:@"/a.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"b"];
  NSString *expected = @"dir2 b";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testImportStatementWithFile {
  NSString *dir = [self randomDir];
  NSString *groupFile = [[[[@"import \"" stringByAppendingString:dir] stringByAppendingString:@"/group2.stg\"\n"] stringByAppendingString:@"a() ::= \"g1 a\"\n"] stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir arg1:@"group1.stg" arg2:groupFile];
  groupFile = @"c() ::= \"g2 c\"\n";
  [self writeFile:dir arg1:@"group2.stg" arg2:groupFile];
  STGroup *group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group1.stg"]] autorelease];
  ST *st = [group1 getInstanceOf:@"c"];
  NSString *expected = @"g2 c";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testImportTemplateInGroupFileFromDir {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir arg1:@"x/a.st" arg2:a];
  NSString *groupFile = [@"b() ::= \"group file b\"\n" stringByAppendingString:@"c() ::= \"group file c\"\n"];
  [self writeFile:dir arg1:@"y/group.stg" arg2:groupFile];
  STGroup *group1 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/x"]] autorelease];
  STGroup *group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/y/group.stg"]] autorelease];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"a"];
  [st.impl dump];
  NSString *expected = @" group file b ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testImportTemplateInGroupFileFromGroupFile {
  NSString *dir = [self randomDir];
  NSString *groupFile = [@"a() ::= \"g1 a\"\n" stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir arg1:@"x/group.stg" arg2:groupFile];
  groupFile = [@"b() ::= \"g2 b\"\n" stringByAppendingString:@"c() ::= \"g2 c\"\n"];
  [self writeFile:dir arg1:@"y/group.stg" arg2:groupFile];
  STGroup *group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/x/group.stg"]] autorelease];
  STGroup *group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/y/group.stg"]] autorelease];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"b"];
  NSString *expected = @"g2 c";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testImportTemplateFromSubdir {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << <subdir/b()> >>\n";
  NSString *b = @"b() ::= <<x's subdir/b>>\n";
  [self writeFile:dir arg1:@"x/subdir/a.st" arg2:a];
  [self writeFile:dir arg1:@"y/subdir/b.st" arg2:b];
  STGroup *group1 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/x"]] autorelease];
  STGroup *group2 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/y"]] autorelease];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"subdir/a"];
  NSString *expected = @" x's subdir/b ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testImportTemplateFromGroupFile {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << <subdir/b()> >>\n";
  [self writeFile:dir arg1:@"x/subdir/a.st" arg2:a];
  NSString *groupFile = [@"a() ::= \"group file: a\"\n" stringByAppendingString:@"b() ::= \"group file: b\"\n"];
  [self writeFile:dir arg1:@"y/subdir.stg" arg2:groupFile];
  STGroup *group1 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/x"]] autorelease];
  STGroup *group2 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/y"]] autorelease];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"subdir/a"];
  NSString *expected = @" group file: b ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testPolymorphicTemplateReference {
  NSString *dir1 = [self randomDir];
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 arg1:@"b.st" arg2:b];
  NSString *dir2 = [self randomDir];
  NSString *a = @"a() ::= << <b()> >>\n";
  b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir2 arg1:@"a.st" arg2:a];
  [self writeFile:dir2 arg1:@"b.st" arg2:b];
  STGroup *group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup *group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group1 importTemplates:group2];
  ST *st = [group2 getInstanceOf:@"a"];
  NSString *expected = @" dir2 b ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
  st = [group1 getInstanceOf:@"a"];
  expected = @" dir1 b ";
  result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testSuper {
  NSString *dir1 = [self randomDir];
  NSString *a = @"a() ::= <<dir1 a>>\n";
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 arg1:@"a.st" arg2:a];
  [self writeFile:dir1 arg1:@"b.st" arg2:b];
  NSString *dir2 = [self randomDir];
  a = @"a() ::= << [<super.a()>] >>\n";
  [self writeFile:dir2 arg1:@"a.st" arg2:a];
  STGroup *group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup *group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group2 importTemplates:group1];
  ST *st = [group2 getInstanceOf:@"a"];
  NSString *expected = @" [dir1 a] ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
}

- (void) testUnloadImportedTemplate {
  NSString *dir1 = [self randomDir];
  NSString *a = @"a() ::= <<dir1 a>>\n";
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 arg1:@"a.st" arg2:a];
  [self writeFile:dir1 arg1:@"b.st" arg2:b];
  NSString *dir2 = [self randomDir];
  a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir2 arg1:@"a.st" arg2:a];
  STGroup *group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup *group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group2 importTemplates:group1];
  ST *st = [group2 getInstanceOf:@"a"];
  ST *st2 = [group2 getInstanceOf:@"b"];
  int originalHashCode = [System identityHashCode:st];
  int originalHashCode2 = [System identityHashCode:st2];
  [group1 unload];
  st = [group2 getInstanceOf:@"a"];
  int newHashCode = [System identityHashCode:st];
  [self assertEquals:originalHashCode == newHashCode arg1:NO];
  NSString *expected = @" dir1 b ";
  NSString *result = [st render];
  [self assertEquals:expected arg1:result];
  st = [group2 getInstanceOf:@"b"];
  int newHashCode2 = [System identityHashCode:st];
  [self assertEquals:originalHashCode2 == newHashCode2 arg1:NO];
  result = [st render];
  expected = @"dir1 b";
  [self assertEquals:expected arg1:result];
}

@end
