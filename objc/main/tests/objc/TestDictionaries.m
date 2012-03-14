#import "TestDictionaries.h"

@implementation TestDictionaries

- (void) test01Dict
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/test.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"type" value:@"int"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"int x = 0;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test02DictValuesAreTemplates
{
    NSString *templates = @"typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] \nvar(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/test.stg", tmpdir]];
    ST *st = [group getInstanceOf:@"var"];
    [st.impl dump];
    [st add:@"w" value:@"L"];
    [st add:@"type" value:@"int"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"int x = 0L;";
    NSString *result = [st render];
    result = (result != nil) ? result : @"result = nil";
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test03DictKeyLookupViaTemplate
{
    NSString *templates = @"typeInit ::= [\"int\":{0<w>}, \"float\":{0.0<w>}] \nvar(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"w" value:@"L"];
    [st add:@"type" value:[ST newSTWithTemplate:@"int"]];
    [st add:@"name" value:@"x"];
    NSString *expected = @"int x = 0L;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test04DictKeyLookupAsNonToStringableObject
{
    NSString *templates = @"foo(m,k) ::= \"<m.(k)>\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"foo"];
    AMutableDictionary *m = [[AMutableDictionary dictionaryWithCapacity:5] autorelease];
    [m setObject:[[[HashableUser alloc] init:99 name:@"parrt"] autorelease] forKey:@"first"];
    [m setObject:[[[HashableUser alloc] init:172036 name:@"tombu"] autorelease] forKey:@"second"];
    [m setObject:[[[HashableUser alloc] init:391 name:@"sriram"] autorelease] forKey:@"third"];
    [st add:@"m" value:m];
    [st add:@"k" value:[[HashableUser alloc] init:172036 name:@"tombu"]];
    NSString *expected = @"second";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test05DictMissingDefaultValueIsEmpty
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nvar(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"w" value:@"L"];
    [st add:@"type" value:@"double"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"double x = ;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test06DictMissingDefaultValueIsEmptyForNullKey
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nvar(type,w,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"w" value:@"L"];
    [st add:@"type" value:nil];
    [st add:@"name" value:@"x"];
    NSString *expected = @" x = ;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test07DictHiddenByFormalArg
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nvar(typeInit,type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"type" value:@"int"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"int x = ;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test08DictEmptyValueAndAngleBracketStrings
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":, \"double\":<<0.0L>>] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"type" value:@"float"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"float x = ;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test09DictDefaultValue
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", default:\"null\"] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"type" value:@"UserRecord"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"UserRecord x = null;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test10DictNullKeyGetsDefaultValue
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", default:\"null\"] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"name" value:@"x"];
    NSString *expected = @" x = null;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test11DictEmptyDefaultValue
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", default:] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
    STGroupFile *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    [group setListener:errors];
    [group load];
    NSString *expected = @"[test.stg 1:33: missing value for key at ']']";
    NSString *result = [errors.errors description];
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test12DictDefaultValueIsKey
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", default:key] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"type" value:@"UserRecord"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"UserRecord x = UserRecord;";
    NSString *result = [st render];
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}


/**
 *Test that a map can have only the default entry.
 */
- (void) test13DictDefaultStringAsKey
{
    NSString *templates = @"typeInit ::= [\"default\":\"foo\"] \nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"var"];
    [st add:@"type" value:@"default"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"default x = foo;";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}


/**
 *Test that a map can return a <b>string</b> with the word: default.
 */
- (void) test14DictDefaultIsDefaultString
{
    NSString *templates = @"map ::= [default: \"default\"] \nt() ::= << <map.(\"1\")> >>\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"t"];
    NSString *expected = @" default ";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test15DictViaEnclosingTemplates
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nintermediate(type,name) ::= \"<var(type,name)>\"\nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *st = [group getInstanceOf:@"intermediate"];
    [st add:@"type" value:@"int"];
    [st add:@"name" value:@"x"];
    NSString *expected = @"int x = 0;";
    NSString *result = [st render];
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) test16DictViaEnclosingTemplates2
{
    NSString *templates = @"typeInit ::= [\"int\":\"0\", \"float\":\"0.0\"] \nintermediate(stuff) ::= \"<stuff>\"\nvar(type,name) ::= \"<type> <name> = <typeInit.(type)>;\"\n";
    [self writeFile:tmpdir fileName:@"test.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/test.stg"]];
    ST *interm = [group getInstanceOf:@"intermediate"];
    ST *var = [group getInstanceOf:@"var"];
    [var add:@"type" value:@"int"];
    [var add:@"name" value:@"x"];
    [interm add:@"stuff" value:var];
    NSString *expected = @"int x = 0;";
    NSString *result = [interm render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) Test17AccessDictionaryFromAnonymousTemplate
{
    NSString *dir = tmpdir;
    NSString *g = @"a() ::= <<[<[\"foo\",\"a\"]:{x|<if(values.(x))><x><endif>}>]>>\nvalues ::= [\n    \"a\":false,\n    default:true\n]\n";
    [self writeFile:dir fileName:@"g.stg" content:g];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/g.stg"]];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"[foo]";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

- (void) Test18AccessDictionaryFromAnonymousTemplateInRegion
{
    NSString *dir = tmpdir;
    NSString *g = @"a() ::= <<[<@r()>]>>\n@a.r() ::= <<\n<[\"foo\",\"a\"]:{x|<if(values.(x))><x><endif>}>\n>>\nvalues ::= [\n    \"a\":false,\n    default:true\n]\n";
    [self writeFile:dir fileName:@"g.stg" content:g];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/g.stg"]];
    ST *st = [group getInstanceOf:@"a"];
    NSString *expected = @"[foo]";
    NSString *result = [st render];
    result = ((result != nil) ? result : @"result = nil");
    STAssertTrue([expected isEqualTo:result], @"Expected \"%@\" but had \"%@\"", expected, result );
    return;
}

@end
