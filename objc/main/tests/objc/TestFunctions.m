#import "TestFunctions.h"
#import "BaseTest.h"
#import "STGroupFile.h"

@implementation TestFunctions_Anon1

+ (id) newAnon
{
    return [[TestFunctions_Anon1 alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        [self addObject:@"Ter"];
        [self addObject:@"Tom"];
    }
    return self;
}

@end

@implementation TestFunctions_Anon2

+ (id) newAnon
{
    return [[TestFunctions_Anon2 alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        [self addObject:@"Ter"];
        [self addObject:@"Tom"];
    }
    return self;
}

@end

@implementation TestFunctions_Anon3

+ (id) newAnon
{
    return [[TestFunctions_Anon3 alloc] init];
}

- (id) init
{
    if ( (self=[super init]) != nil ) {
        [self addObject:@"Ter"];
        [self addObject:nil];
        [self addObject:@"Tom"];
        [self addObject:nil];
    }
    return self;
}

@end

@implementation TestFunctions_Anon4

+ (id) newAnon
{
    return [[TestFunctions_Anon4 alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        [self addObject:@"Ter"];
    }
    return self;
}

@end

@implementation TestFunctions_Anon5

+ (id) newAnon
{
    return [[TestFunctions_Anon5 alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        [self addObject:@"Ter"];
    }
    return self;
}

@end

@implementation TestFunctions_Anon6

+ (id) newAnon:(id)a b:(id)b
{
    return [[TestFunctions_Anon6 alloc] init:a b:b];
}

- (id) init:(id)a b:(id)b
{
    if ( (self=[super init]) != nil ) {
        [self addObject:a];
        [self addObject:b];
    }
    return self;
}

@end

@implementation TestFunctions_Anon7

+ (id) newAnon:(id)a b:(id)b
{
    return [[TestFunctions_Anon7 alloc] init:a b:b];
}

- (id) init:(id)a b:(id)b
{
    self=[super init];
    if ( self != nil ) {
        [self addObject:a];
        [self addObject:b];
    }
    return self;
}

@end

@implementation TestFunctions

- (void) test01First
{
    NSString *template = @"<first(names)>";
    ST *st = [ST newSTWithTemplate:template];
    AMutableArray *names = [TestFunctions_Anon1 newAnon];
    [st add:@"names" value:names];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test02Length
{
    NSString *template = @"<length(names)>";
    ST *st = [ST newSTWithTemplate:template];
    AMutableArray *names = [TestFunctions_Anon2 newAnon];
    [st add:@"names" value:names];
    NSString *expected = @"2";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test03LengthWithNullValues
{
    NSString *template = @"<length(names)>";
    ST *st = [ST newSTWithTemplate:template];
    AMutableArray *names = [TestFunctions_Anon3 newAnon];
    [st add:@"names" value:names];
    NSString *expected = @"4";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test04FirstOp
{
    ST *e = [ST newSTWithTemplate:@"<first(names)>"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:@"Sriram"];
    NSString *expected = @"Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test05TruncOp
{
    ST *e = [ST newSTWithTemplate:@"<trunc(names); separator=\", \">"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:@"Sriram"];
    NSString *expected = @"Ter, Tom";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test06RestOp
{
    ST *e = [ST newSTWithTemplate:@"<rest(names); separator=\", \">"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:@"Sriram"];
    NSString *expected = @"Tom, Sriram";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test07RestOpEmptyList
{
    ST *e = [ST newSTWithTemplate:@"<rest(names); separator=\", \">"];
    [e add:@"names" value:[AMutableArray arrayWithCapacity:16]];
    NSString *expected = @"";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test08ReUseOfRestResult
{
    NSString *templates =@"a(names) ::= \"<b(rest(names))>\"\nb(x) ::= \"<x>, <x>\"\n";
    [BaseTest writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *e = [group getInstanceOf:@"a"];
    AMutableArray *names = [AMutableArray arrayWithCapacity:16];
    [names addObject:@"Ter"];
    [names addObject:@"Tom"];
    [e add:@"names" value:names];
    NSString *expected = @"Tom, Tom";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test09LastOp
{
    ST *e = [ST newSTWithTemplate:@"<last(names)>"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:@"Sriram"];
    NSString *expected = @"Sriram";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test10StripOp
{
    ST *e = [ST newSTWithTemplate:@"<strip(names); null=\"n/a\">"];
    [e add:@"names" value:nil];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:nil];
    [e add:@"names" value:nil];
    [e add:@"names" value:@"Sriram"];
    [e add:@"names" value:nil];
    NSString *expected = @"TomSriram";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test11LengthStrip
{
    ST *e = [ST newSTWithTemplate:@"<length(strip(names))>"];
    [e add:@"names" value:nil];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:nil];
    [e add:@"names" value:nil];
    [e add:@"names" value:@"Sriram"];
    [e add:@"names" value:nil];
    NSString *expected = @"2";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test12CombinedOp
{
    ST *e = [ST newSTWithTemplate:@"<[first(mine),rest(yours)]; separator=\", \">"];
    [e add:@"mine" value:@"1"];
    [e add:@"mine" value:@"2"];
    [e add:@"mine" value:@"3"];
    [e add:@"yours" value:@"a"];
    [e add:@"yours" value:@"b"];
    NSString *expected = @"1, b";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test13CatListAndSingleAttribute
{
    ST *e = [ST newSTWithTemplate:@"<[mine,yours]; separator=\", \">"];
    [e add:@"mine" value:@"1"];
    [e add:@"mine" value:@"2"];
    [e add:@"mine" value:@"3"];
    [e add:@"yours" value:@"a"];
    NSString *expected = @"1, 2, 3, a";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test14ReUseOfCat
{
    NSString *templates = @"a(mine,yours) ::= \"<b([mine,yours])>\"\nb(x) ::= \"<x>, <x>\"\n";
    [BaseTest writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *e = [group getInstanceOf:@"a"];
    AMutableArray *mine = [AMutableArray arrayWithCapacity:16];
    [mine addObject:@"Ter"];
    [mine addObject:@"Tom"];
    [e add:@"mine" value:mine];
    AMutableArray *yours = [AMutableArray arrayWithCapacity:16];
    [yours addObject:@"Foo"];
    [e add:@"yours" value:yours];
    NSString *expected = @"TerTomFoo, TerTomFoo";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test15CatListAndEmptyAttributes
{
    ST *e = [ST newSTWithTemplate:@"<[x,mine,y,yours,z]; separator=\", \">"];
    [e add:@"mine" value:@"1"];
    [e add:@"mine" value:@"2"];
    [e add:@"mine" value:@"3"];
    [e add:@"yours" value:@"a"];
    NSString *expected = @"1, 2, 3, a";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test16NestedOp
{
    ST *e = [ST newSTWithTemplate:@"<first(rest(names))>"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:@"Sriram"];
    NSString *expected = @"Tom";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test17FirstWithOneAttributeOp
{
    ST *e = [ST newSTWithTemplate:@"<first(names)>"];
    [e add:@"names" value:@"Ter"];
    NSString *expected = @"Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test18LastWithOneAttributeOp
{
    ST *e = [ST newSTWithTemplate:@"<last(names)>"];
    [e add:@"names" value:@"Ter"];
    NSString *expected = @"Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test19LastWithLengthOneListAttributeOp
{
    ST *e = [ST newSTWithTemplate:@"<last(names)>"];
    [e add:@"names" value:((TestFunctions_Anon4 *)[[TestFunctions_Anon4 alloc] init])];
    NSString *expected = @"Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test20RestWithOneAttributeOp
{
    ST *e = [ST newSTWithTemplate:@"<rest(names)>"];
    [e add:@"names" value:@"Ter"];
    NSString *expected = @"";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test21RestWithLengthOneListAttributeOp
{
    ST *e = [ST newSTWithTemplate:@"<rest(names)>"];
    [e add:@"names" value:((TestFunctions_Anon5 *)[[TestFunctions_Anon5 alloc] init])];
    NSString *expected = @"";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test22RepeatedRestOp
{
    ST *e = [ST newSTWithTemplate:@"<rest(names)>, <rest(names)>"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    NSString *expected = @"Tom, Tom";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test23IncomingLists
{
    ST *e = [ST newSTWithTemplate:@"<rest(names)>, <rest(names)>"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    NSString *expected = @"Tom, Tom";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test24FirstWithCatAttribute
{
    ST *e = [ST newSTWithTemplate:@"<first([names,phones])>"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"phones" value:@"1"];
    [e add:@"phones" value:@"2"];
    NSString *expected = @"Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test25FirstWithListOfMaps
{
    ST *e = [ST newSTWithTemplate:@"<first(maps).Ter>"];
    AMutableDictionary *m1 = [AMutableDictionary dictionaryWithCapacity:16];
    AMutableDictionary *m2 = [AMutableDictionary dictionaryWithCapacity:16];
    [m1 setObject:@"x5707" forKey:@"Ter"];
    [e add:@"maps" value:m1];
    [m2 setObject:@"x5332" forKey:@"Tom"];
    [e add:@"maps" value:m2];
    NSString *expected = @"x5707";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    AMutableArray *list = [TestFunctions_Anon6 newAnon:m1 b:m2];
    [e add:@"maps" value:list];
    expected = @"x5707";
    result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test26FirstWithListOfMaps2
{
    ST *e = [ST newSTWithTemplate:@"<first(maps):{ m | <m>!}>"];
    AMutableDictionary *m1 = [AMutableDictionary dictionaryWithCapacity:16];
    AMutableDictionary *m2 = [AMutableDictionary dictionaryWithCapacity:16];
    [m1 setObject:@"x5707" forKey:@"Ter"];
    [e add:@"maps" value:m1];
    [m2 setObject:@"x5332" forKey:@"Tom"];
    [e add:@"maps" value:m2];
    NSString *expected = @"Ter!";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    AMutableArray *list = [TestFunctions_Anon7 newAnon:m1 b:m2];
    [e add:@"maps" value:list];
    expected = @"Ter!";
    result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test27Trim
{
    ST *e = [ST newSTWithTemplate:@"<trim(name)>"];
    [e add:@"name" value:@" Ter  \n"];
    NSString *expected = @"Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test28Strlen
{
    ST *e = [ST newSTWithTemplate:@"<strlen(name)>"];
    [e add:@"name" value:@"012345"];
    NSString *expected = @"6";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test29Reverse
{
    ST *e = [ST newSTWithTemplate:@"<reverse(names); separator=\", \">"];
    [e add:@"names" value:@"Ter"];
    [e add:@"names" value:@"Tom"];
    [e add:@"names" value:@"Sriram"];
    NSString *expected = @"Sriram, Tom, Ter";
    NSString *result = [e render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

@end
