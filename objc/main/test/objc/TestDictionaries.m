#import "TestDictionaries.h"

@implementation TestDictionaries

- (void) testDict {
  NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"type" value:@"int"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"int x = 0;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictValuesAreTemplates {
  NSString *templates = @"typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] \nvar(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\"\;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st.impl dump];
  [st add:@"w" value:@"L"];
  [st add:@"type" value:@"int"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"int x = 0L;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictKeyLookupViaTemplate {
  NSString *templates = @"typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] \nvar(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"w" value:@"L"];
  [st add:@"type" value:[[[ST alloc] init:@"int"] autorelease]];
  [st add:@"name" value:@"x"];
  NSString *expected = @"int x = 0L;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictKeyLookupAsNonToStringableObject {
  NSString *templates = @"foo(m,k) ::= \"<m.(k)>\"\n";
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"foo"];
  NSMutableDictionary *m = [[[NSMutableDictionary alloc] init] autorelease];
  [m setObject:[[[HashableUser alloc] init:99 arg1:@"parrt"] autorelease] arg1:@"first"];
  [m setObject:[[[HashableUser alloc] init:172036 arg1:@"tombu"] autorelease] arg1:@"second"];
  [m setObject:[[[HashableUser alloc] init:391 arg1:@"sriram"] autorelease] arg1:@"third"];
  [st add:@"m" value:m];
  [st add:@"k" value:[[[HashableUser alloc] init:172036 arg1:@"tombu"] autorelease]];
  NSString *expected = @"second";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictMissingDefaultValueIsEmpty {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] " stringByAppendingString:newline] stringByAppendingString:@"var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"w" value:@"L"];
  [st add:@"type" value:@"double"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"double x = ;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictMissingDefaultValueIsEmptyForNullKey {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] " stringByAppendingString:newline] stringByAppendingString:@"var(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"w" value:@"L"];
  [st add:@"type" value:nil];
  [st add:@"name" value:@"x"];
  NSString *expected = @" x = ;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictHiddenByFormalArg {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] " stringByAppendingString:newline] stringByAppendingString:@"var(typeInit,type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"type" value:@"int"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"int x = ;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictEmptyValueAndAngleBracketStrings {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", \"float\":, \"double\":<<0.0L>>] " stringByAppendingString:newline] stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"type" value:@"float"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"float x = ;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictDefaultValue {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", default:\"null\"] " stringByAppendingString:newline] stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"type" value:@"UserRecord"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"UserRecord x = null;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictNullKeyGetsDefaultValue {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", default:\"null\"] " stringByAppendingString:newline] stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"name" value:@"x"];
  NSString *expected = @" x = null;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictEmptyDefaultValue {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", default:] " stringByAppendingString:newline] stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
  STGroupFile *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  [group setListener:errors];
  [group load];
  NSString *expected = @"[test.stg 1:33: missing value for key at ']']";
  NSString *result = [errors.errors description];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictDefaultValueIsKey {
  NSString *templates = [[@"typeInit ::= [\"int\":\"0\", default:key] " stringByAppendingString:newline] stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"type" value:@"UserRecord"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"UserRecord x = UserRecord;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}


/**
 *Test that a map can have only the default entry.
 */
- (void) testDictDefaultStringAsKey {
  NSString *templates = [[@"typeInit ::= [\"default\":\"foo\"] " stringByAppendingString:newline] stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"var"];
  [st add:@"type" value:@"default"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"default x = foo;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}


/**
 *Test that a map can return a <b>string</b> with the word: default.
 */
- (void) testDictDefaultIsDefaultString {
  NSString *templates = [[@"map ::= [default: \"default\"] " stringByAppendingString:newline] stringByAppendingString:@"t() ::= << <map.(\"1\")> >>"] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"t"];
  NSString *expected = @" default ";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictViaEnclosingTemplates {
  NSString *templates = [[[@"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] " stringByAppendingString:newline] stringByAppendingString:@"intermediate(type,name) ::= \"<var(type,name)>\""] + newline stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"intermediate"];
  [st add:@"type" value:@"int"];
  [st add:@"name" value:@"x"];
  NSString *expected = @"int x = 0;";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) testDictViaEnclosingTemplates2 {
  NSString *templates = [[[@"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] " stringByAppendingString:newline] stringByAppendingString:@"intermediate(stuff) ::= \"<stuff>\""] + newline stringByAppendingString:@"var(type,name) ::= \"<type> <name> = <typeInit.(type)>;\""] + newline;
  [self writeFile:tmpdir arg1:@"test.stg" arg2:templates];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"test.stg"]] autorelease];
  ST *interm = [group getInstanceOf:@"intermediate"];
  ST *var = [group getInstanceOf:@"var"];
  [var add:@"type" value:@"int"];
  [var add:@"name" value:@"x"];
  [interm add:@"stuff" value:var];
  NSString *expected = @"int x = 0;";
  NSString *result = [interm render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) TestAccessDictionaryFromAnonymousTemplate {
  NSString *dir = tmpdir;
  NSString *g = [[[[@"a() ::= <<[<[\"foo\",\"a\"]:{x|<if(values.(x))><x><endif>}>]>>\n" stringByAppendingString:@"values ::= [\n"] stringByAppendingString:@"    \"a\":false,\n"] stringByAppendingString:@"    default:true\n"] stringByAppendingString:@"]\n"];
  [self writeFile:dir arg1:@"g.stg" arg2:g];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"g.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"a"];
  NSString *expected = @"[foo]";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

- (void) TestAccessDictionaryFromAnonymousTemplateInRegion {
  NSString *dir = tmpdir;
  NSString *g = [[[[[[[@"a() ::= <<[<@r()>]>>\n" stringByAppendingString:@"@a.r() ::= <<\n"] stringByAppendingString:@"<[\"foo\",\"a\"]:{x|<if(values.(x))><x><endif>}>\n"] stringByAppendingString:@">>\n"] stringByAppendingString:@"values ::= [\n"] stringByAppendingString:@"    \"a\":false,\n"] stringByAppendingString:@"    default:true\n"] stringByAppendingString:@"]\n"];
  [self writeFile:dir arg1:@"g.stg" arg2:g];
  STGroup *group = [[[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"g.stg"]] autorelease];
  ST *st = [group getInstanceOf:@"a"];
  NSString *expected = @"[foo]";
  NSString *result = [st render];
  STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
}

@end
