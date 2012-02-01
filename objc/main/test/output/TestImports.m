#import "TestImports.h"

@implementation TestImports

- (void) testImportDir {
  NSString * dir1 = [[self randomDir] stringByAppendingString:@"/dir1"];
  NSString * dir2 = [[self randomDir] stringByAppendingString:@"/dir2"];
  NSString * gstr = [[[@"import \"" stringByAppendingString:dir2] stringByAppendingString:@"\"\n"] stringByAppendingString:@"a() ::= <<dir1 a>>\n"];
  [self writeFile:dir1 param1:@"g.stg" param2:gstr];
  NSString * a = @"a() ::= <<dir2 a>>\n";
  NSString * b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir2 param1:@"a.st" param2:a];
  [self writeFile:dir2 param1:@"b.st" param2:b];
  STGroup * group = [[[STGroupFile alloc] init:[dir1 stringByAppendingString:@"/g.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"b"];
  NSString * expected = @"dir2 b";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportDirInJarViaCLASSPATH {
  NSString * root = [self randomDir];
  NSString * sub = [root stringByAppendingString:@"/sub"];
  NSString * base = [root stringByAppendingString:@"/base"];
  NSString * gstr = [@"import \"base\"\n" stringByAppendingString:@"a() ::= <<sub a>>\n"];
  [self writeFile:sub param1:@"g.stg" param2:gstr];
  NSString * a = @"a() ::= <<base a>>\n";
  NSString * b = @"b() ::= <<base b>>\n";
  [self writeFile:base param1:@"a.st" param2:a];
  [self writeFile:base param1:@"b.st" param2:b];
  [self writeTestFile:[[@"STGroup group = new STGroupFile(\"sub/g.stg\");\n" stringByAppendingString:@"ST st = group.getInstanceOf(\"b\");\n"] stringByAppendingString:@"String result = st.render();\n"] param1:root];
  [self compile:@"Test.java" param1:root];
  [self jar:@"test.jar" param1:[NSArray arrayWithObjects:@"sub", @"base", nil] param2:root];
  [[Runtime runtime] exec:[[[[@"rm -rf " stringByAppendingString:root] stringByAppendingString:@"/sub "] stringByAppendingString:root] stringByAppendingString:@"/base"]];
  NSString * result = [self java:@"Test" param1:@"test.jar" param2:root];
  NSString * expected = [@"base b" stringByAppendingString:newline];
  [self assertEquals:expected param1:result];
}

- (void) testImportGroupAtSameLevelInJar {
  NSString * root = [self randomDir];
  [System.out println:root];
  NSString * dir = [root stringByAppendingString:@"/org/foo/templates"];
  NSString * main = [[@"import \"lib.stg\"\n" stringByAppendingString:@"a() ::= <<main a calls <bold()>!>>\n"] stringByAppendingString:@"b() ::= <<main b>>\n"];
  [self writeFile:dir param1:@"main.stg" param2:main];
  NSString * lib = @"bold() ::= <<lib bold>>\n";
  [self writeFile:dir param1:@"lib.stg" param2:lib];
  [self writeTestFile:[[@"STGroup group = new STGroupFile(\"org/foo/templates/main.stg\");\n" stringByAppendingString:@"ST st = group.getInstanceOf(\"a\");\n"] stringByAppendingString:@"String result = st.render();\n"] param1:root];
  [self compile:@"Test.java" param1:root];
  [self jar:@"test.jar" param1:[NSArray arrayWithObjects:@"org", nil] param2:root];
  [[Runtime runtime] exec:[[@"rm -rf " stringByAppendingString:root] stringByAppendingString:@"/org"]];
  NSString * result = [self java:@"Test" param1:@"test.jar" param2:root];
  NSString * expected = [@"main a calls lib bold!" stringByAppendingString:newline];
  [self assertEquals:expected param1:result];
}

- (void) testImportGroupInJarViaCLASSPATH {
  NSString * root = [self randomDir];
  [System.out println:root];
  NSString * dir = [root stringByAppendingString:@"/org/foo/templates"];
  NSString * main = [[@"import \"org/foo/lib/lib.stg\"\n" stringByAppendingString:@"a() ::= <<main a calls <bold()>!>>\n"] stringByAppendingString:@"b() ::= <<main b>>\n"];
  [self writeFile:dir param1:@"main.stg" param2:main];
  NSString * lib = @"bold() ::= <<lib bold>>\n";
  dir = [root stringByAppendingString:@"/org/foo/lib"];
  [self writeFile:dir param1:@"lib.stg" param2:lib];
  [self writeTestFile:[[@"STGroup group = new STGroupFile(\"org/foo/templates/main.stg\");\n" stringByAppendingString:@"ST st = group.getInstanceOf(\"a\");\n"] stringByAppendingString:@"String result = st.render();\n"] param1:root];
  [self compile:@"Test.java" param1:root];
  [self jar:@"test.jar" param1:[NSArray arrayWithObjects:@"org", nil] param2:root];
  [[Runtime runtime] exec:[[@"rm -rf " stringByAppendingString:root] stringByAppendingString:@"/org"]];
  NSString * result = [self java:@"Test" param1:@"test.jar" param2:root];
  NSString * expected = [@"main a calls lib bold!" stringByAppendingString:newline];
  [self assertEquals:expected param1:result];
}

- (void) testImportRelativeDir {
  NSString * dir = [self randomDir];
  NSString * gstr = [@"import \"subdir\"\n" stringByAppendingString:@"a() ::= <<dir1 a>>\n"];
  [self writeFile:dir param1:@"g.stg" param2:gstr];
  NSString * a = @"a() ::= <<dir2 a>>\n";
  NSString * b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir param1:@"subdir/a.st" param2:a];
  [self writeFile:dir param1:@"subdir/b.st" param2:b];
  STGroup * group = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/g.stg"]] autorelease];
  ST * st = [group getInstanceOf:@"b"];
  NSString * expected = @"dir2 b";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportGroupFileSameDir {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[@"import \"group2.stg\"\n" stringByAppendingString:@"a() ::= \"g1 a\"\n"] stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir param1:@"group1.stg" param2:groupFile];
  groupFile = @"c() ::= \"g2 c\"\n";
  [self writeFile:dir param1:@"group2.stg" param2:groupFile];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group1.stg"]] autorelease];
  ST * st = [group1 getInstanceOf:@"c"];
  NSString * expected = @"g2 c";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportRelativeGroupFile {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[@"import \"subdir/group2.stg\"\n" stringByAppendingString:@"a() ::= \"g1 a\"\n"] stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir param1:@"group1.stg" param2:groupFile];
  groupFile = @"c() ::= \"g2 c\"\n";
  [self writeFile:dir param1:@"subdir/group2.stg" param2:groupFile];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group1.stg"]] autorelease];
  ST * st = [group1 getInstanceOf:@"c"];
  NSString * expected = @"g2 c";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportTemplateFileSameDir {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[@"import \"c.st\"\n" stringByAppendingString:@"a() ::= \"g1 a\"\n"] stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir param1:@"group1.stg" param2:groupFile];
  groupFile = @"c() ::= \"c\"\n";
  [self writeFile:dir param1:@"c.st" param2:groupFile];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group1.stg"]] autorelease];
  ST * st = [group1 getInstanceOf:@"c"];
  NSString * expected = @"c";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportRelativeTemplateFile {
  NSString * dir = [self randomDir];
  NSString * groupFile = [[@"import \"subdir/c.st\"\n" stringByAppendingString:@"a() ::= \"g1 a\"\n"] stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir param1:@"group1.stg" param2:groupFile];
  NSString * stFile = @"c() ::= \"c\"\n";
  [self writeFile:dir param1:@"subdir/c.st" param2:stFile];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/group1.stg"]] autorelease];
  ST * st = [group1 getInstanceOf:@"c"];
  NSString * expected = @"c";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportTemplateFromAnotherGroupObject {
  NSString * dir1 = [self randomDir];
  NSString * a = @"a() ::= <<dir1 a>>\n";
  NSString * b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 param1:@"a.st" param2:a];
  [self writeFile:dir1 param1:@"b.st" param2:b];
  NSString * dir2 = [self randomDir];
  a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir2 param1:@"a.st" param2:a];
  STGroup * group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup * group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group2 importTemplates:group1];
  ST * st = [group2 getInstanceOf:@"b"];
  NSString * expected = @"dir1 b";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
  st = [group2 getInstanceOf:@"a"];
  expected = @" dir1 b ";
  result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportTemplateInGroupFileFromDir {
  NSString * dir = [self randomDir];
  NSString * a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir param1:@"x/a.st" param2:a];
  NSString * groupFile = [@"b() ::= \"group file b\"\n" stringByAppendingString:@"c() ::= \"group file c\"\n"];
  [self writeFile:dir param1:@"y/group.stg" param2:groupFile];
  STGroup * group1 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/x"]] autorelease];
  STGroup * group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/y/group.stg"]] autorelease];
  [group1 importTemplates:group2];
  ST * st = [group1 getInstanceOf:@"a"];
  NSString * expected = @" group file b ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportTemplateInGroupFileFromGroupFile {
  NSString * dir = [self randomDir];
  NSString * groupFile = [@"a() ::= \"g1 a\"\n" stringByAppendingString:@"b() ::= \"<c()>\"\n"];
  [self writeFile:dir param1:@"x/group.stg" param2:groupFile];
  groupFile = [@"b() ::= \"g2 b\"\n" stringByAppendingString:@"c() ::= \"g2 c\"\n"];
  [self writeFile:dir param1:@"y/group.stg" param2:groupFile];
  STGroup * group1 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/x/group.stg"]] autorelease];
  STGroup * group2 = [[[STGroupFile alloc] init:[dir stringByAppendingString:@"/y/group.stg"]] autorelease];
  [group1 importTemplates:group2];
  ST * st = [group1 getInstanceOf:@"b"];
  NSString * expected = @"g2 c";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportTemplateFromSubdir {
  NSString * dir = [self randomDir];
  NSString * a = @"a() ::= << <subdir/b()> >>\n";
  NSString * b = @"b() ::= <<x's subdir/b>>\n";
  [self writeFile:dir param1:@"x/subdir/a.st" param2:a];
  [self writeFile:dir param1:@"y/subdir/b.st" param2:b];
  STGroup * group1 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/x"]] autorelease];
  STGroup * group2 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/y"]] autorelease];
  [group1 importTemplates:group2];
  ST * st = [group1 getInstanceOf:@"subdir/a"];
  NSString * expected = @" x's subdir/b ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testImportTemplateFromGroupFile {
  NSString * dir = [self randomDir];
  NSString * a = @"a() ::= << <subdir/b()> >>\n";
  [self writeFile:dir param1:@"x/subdir/a.st" param2:a];
  NSString * groupFile = [@"a() ::= \"group file: a\"\n" stringByAppendingString:@"b() ::= \"group file: b\"\n"];
  [self writeFile:dir param1:@"y/subdir.stg" param2:groupFile];
  STGroup * group1 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/x"]] autorelease];
  STGroup * group2 = [[[STGroupDir alloc] init:[dir stringByAppendingString:@"/y"]] autorelease];
  [group1 importTemplates:group2];
  ST * st = [group1 getInstanceOf:@"subdir/a"];
  NSString * expected = @" group file: b ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testPolymorphicTemplateReference {
  NSString * dir1 = [self randomDir];
  NSString * b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 param1:@"b.st" param2:b];
  NSString * dir2 = [self randomDir];
  NSString * a = @"a() ::= << <b()> >>\n";
  b = @"b() ::= <<dir2 b>>\n";
  [self writeFile:dir2 param1:@"a.st" param2:a];
  [self writeFile:dir2 param1:@"b.st" param2:b];
  STGroup * group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup * group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group1 importTemplates:group2];
  ST * st = [group2 getInstanceOf:@"a"];
  NSString * expected = @" dir2 b ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
  st = [group1 getInstanceOf:@"a"];
  expected = @" dir1 b ";
  result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testSuper {
  NSString * dir1 = [self randomDir];
  NSString * a = @"a() ::= <<dir1 a>>\n";
  NSString * b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 param1:@"a.st" param2:a];
  [self writeFile:dir1 param1:@"b.st" param2:b];
  NSString * dir2 = [self randomDir];
  a = @"a() ::= << [<super.a()>] >>\n";
  [self writeFile:dir2 param1:@"a.st" param2:a];
  STGroup * group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup * group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group2 importTemplates:group1];
  ST * st = [group2 getInstanceOf:@"a"];
  NSString * expected = @" [dir1 a] ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
}

- (void) testUnloadImportedTemplate {
  NSString * dir1 = [self randomDir];
  NSString * a = @"a() ::= <<dir1 a>>\n";
  NSString * b = @"b() ::= <<dir1 b>>\n";
  [self writeFile:dir1 param1:@"a.st" param2:a];
  [self writeFile:dir1 param1:@"b.st" param2:b];
  NSString * dir2 = [self randomDir];
  a = @"a() ::= << <b()> >>\n";
  [self writeFile:dir2 param1:@"a.st" param2:a];
  STGroup * group1 = [[[STGroupDir alloc] init:dir1] autorelease];
  STGroup * group2 = [[[STGroupDir alloc] init:dir2] autorelease];
  [group2 importTemplates:group1];
  ST * st = [group2 getInstanceOf:@"a"];
  ST * st2 = [group2 getInstanceOf:@"b"];
  int originalHashCode = [System identityHashCode:st];
  int originalHashCode2 = [System identityHashCode:st2];
  [group1 unload];
  st = [group2 getInstanceOf:@"a"];
  int newHashCode = [System identityHashCode:st];
  [self assertEquals:originalHashCode == newHashCode param1:NO];
  NSString * expected = @" dir1 b ";
  NSString * result = [st render];
  [self assertEquals:expected param1:result];
  st = [group2 getInstanceOf:@"b"];
  int newHashCode2 = [System identityHashCode:st];
  [self assertEquals:originalHashCode2 == newHashCode2 param1:NO];
  result = [st render];
  expected = @"dir1 b";
  [self assertEquals:expected param1:result];
}

@end
