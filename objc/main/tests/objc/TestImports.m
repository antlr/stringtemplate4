#import "TestImports.h"

@implementation TestImports

- (void) testImportTemplate {
  NSString *dir1 = [self randomDir];
  NSString *a = @"a() ::= <<dir1 a>>\n";
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 fileName:@"a.st" content:a];
  [self writeFile:dir1 fileName:@"b.st" content:b];
  NSString *dir2 = [self randomDir];
  a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir2 fileName:@"a.st" content:a];
  STGroup *group1 = [STGroupDir newSTGroupDir:dir1];
  STGroup *group2 = [STGroupDir newSTGroupDir:dir2];
  [group2 importTemplates:group1];
  ST *st = [group2 getInstanceOf:@"b"];
  NSString *expected = @"dir1 b";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
  st = [group2 getInstanceOf:@"a"];
  expected = @" dir1 b ";
  result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testImportStatementWithDir {
  NSString *dir1 = [NSString stringWithFormat:@"%@/dir1", [self randomDir]];
  NSString *dir2 = [NSString stringWithFormat:@"%@/dir2", [self randomDir]];
  NSString *a = [NSString stringWithFormat:@"import \"%@\"\na() ::= <<dir1 a>>\n", dir2];
  [self writeFile:dir1 fileName:@"a.stg" content:a];
  a = @"a() ::= <<dir2 a>>\n";
  NSString *b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir2 fileName:@"a.st" content:a];
  [self writeFile:dir2 fileName:@"b.st" content:b];
  STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/a.stg", dir1]];
  ST *st = [group getInstanceOf:@"b"];
  NSString *expected = @"dir2 b";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testImportStatementWithFile {
  NSString *dir = [self randomDir];
  NSString *groupFile = [NSString stringWithFormat:@"import \"%@/group2.stg\"\na() ::= \"g1 a\"\nb() ::= \"<c()>\"\n", dir];
  [self writeFile:dir fileName:@"group1.stg" content:groupFile];
  groupFile = @"c() ::= \"g2 c\"\n";
  [self writeFile:dir fileName:@"group2.stg" content:groupFile];
  STGroup *group1 = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/group1.stg", dir]];
  ST *st = [group1 getInstanceOf:@"c"];
  NSString *expected = @"g2 c";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testImportTemplateInGroupFileFromDir {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir fileName:@"x/a.st" content:a];
  NSString *groupFile = @"b() ::= \"group file b\"\nc() ::= \"group file c\"\n";
  [self writeFile:dir fileName:@"y/group.stg" content:groupFile];
  STGroup *group1 = [STGroupDir newSTGroupDir:[NSString stringWithFormat:@"%@/x", dir]];
  STGroup *group2 = [STGroupFile newSTGroupDir:[NSString stringWithFormat:@"%@/y/group.stg", dir]];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"a"];
  [st.impl dump];
  NSString *expected = @" group file b ";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testImportTemplateInGroupFileFromGroupFile {
  NSString *dir = [self randomDir];
  NSString *groupFile = @"a() ::= \"g1 a\"\nb() ::= \"<c()>\"\n";
  [self writeFile:dir fileName:@"x/group.stg" content:groupFile];
  groupFile = @"b() ::= \"g2 b\"\nc() ::= \"g2 c\"\n";
  [self writeFile:dir fileName:@"y/group.stg" content:groupFile];
  STGroup *group1 = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/x/group.stg", dir]];
  STGroup *group2 = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/y/group.stg", dir]];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"b"];
  NSString *expected = @"g2 c";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testImportTemplateFromSubdir {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << <subdir/b()> >>\n";
  NSString *b = @"b() ::= <<x's subdir/b>>\n";
  [self writeFile:dir fileName:@"x/subdir/a.st" content:a];
  [self writeFile:dir fileName:@"y/subdir/b.st" content:b];
  STGroup *group1 = [STGroupDir newSTGroupDir:[NSString stringWithFormat:@"%@/x", dir]];
  STGroup *group2 = [STGroupDir newSTGroupDir:[NSString stringWithFormat:@"%@/y", dir]];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"subdir/a"];
  NSString *expected = @" x's subdir/b ";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testImportTemplateFromGroupFile {
  NSString *dir = [self randomDir];
  NSString *a = @"a() ::= << <subdir/b()> >>\n";
  [self writeFile:dir fileName:@"x/subdir/a.st" content:a];
  NSString *groupFile = @"a() ::= \"group file: a\"\nb() ::= \"group file: b\"\n";
  [self writeFile:dir fileName:@"y/subdir.stg" content:groupFile];
  STGroup *group1 = [STGroupDir newSTGroupDir:[NSString stringWithFormat:@"%@/x", dir]];
  STGroup *group2 = [STGroupDir newSTGroupDir:[NSString stringWithFormat:@"%@/y", dir]];
  [group1 importTemplates:group2];
  ST *st = [group1 getInstanceOf:@"subdir/a"];
  NSString *expected = @" group file: b ";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testPolymorphicTemplateReference {
  NSString *dir1 = [self randomDir];
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 fileName:@"b.st" content:b];
  NSString *dir2 = [self randomDir];
  NSString *a = @"a() ::= << <b()> >>\n";
  b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir2 fileName:@"a.st" content:a];
  [self writeFile:dir2 fileName:@"b.st" content:b];
  STGroup *group1 = [STGroupDir newSTGroupDir:dir1];
  STGroup *group2 = [STGroupDir newSTGroupDir:dir2];
  [group1 importTemplates:group2];
  ST *st = [group2 getInstanceOf:@"a"];
  NSString *expected = @" dir2 b ";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
  st = [group1 getInstanceOf:@"a"];
  expected = @" dir1 b ";
  result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testSuper {
  NSString *dir1 = [self randomDir];
  NSString *a = @"a() ::= <<dir1 a>>\n";
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 fileName:@"a.st" content:a];
  [self writeFile:dir1 fileName:@"b.st" content:b];
  NSString *dir2 = [self randomDir];
  a = @"a() ::= << [<super.a()>] >>\n";
  [self writeFile:dir2 fileName:@"a.st" content:a];
  STGroup *group1 = [[STGroupDir newSTGroupDir:dir1] autorelease];
  STGroup *group2 = [[STGroupDir newSTGroupDir:dir2] autorelease];
  [group2 importTemplates:group1];
  ST *st = [group2 getInstanceOf:@"a"];
  NSString *expected = @" [dir1 a] ";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
}

- (void) testUnloadImportedTemplate {
  NSString *dir1 = [self randomDir];
  NSString *a = @"a() ::= <<dir1 a>>\n";
  NSString *b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 fileName:@"a.st" content:a];
  [self writeFile:dir1 fileName:@"b.st" content:b];
  NSString *dir2 = [self randomDir];
  a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir2 fileName:@"a.st" content:a];
  STGroup *group1 = [STGroupDir newSTGroupDir:dir1];
  STGroup *group2 = [STGroupDir newSTGroupDir:dir2];
  [group2 importTemplates:group1];
  ST *st = [group2 getInstanceOf:@"a"];
  ST *st2 = [group2 getInstanceOf:@"b"];
  int originalHashCode = [st hash];
  int originalHashCode2 = [st2 hash];
  [group1 unload];
  st = [group2 getInstanceOf:@"a"];
  int newHashCode = [st hash];
  [self assertEquals:originalHashCode == newHashCode arg1:NO];
  NSString *expected = @" dir1 b ";
  NSString *result = [st render];
  [self assertEquals:expected result:result];
  st = [group2 getInstanceOf:@"b"];
  int newHashCode2 = [st hash];
  expected = @"(originalHashCode2 == newHashCode2) = YES";
    result = [NSString stringWithFormat:@"(originalHashCode2 == newHashCode2) = %@", ((originalHashCode2 == newHashCode2) ? @"YES" : @"NO")];
  [self assertEquals:expected result:result];
  expected = @"dir1 b";
  result = [st render];
  [self assertEquals:expected result:result];
}

@end
