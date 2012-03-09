#import "TestFunctions.h"
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
    return;
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
    return;
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
    return;
}

- (void) test04FirstOp
{
    ST *st = [ST newSTWithTemplate:@"<first(names)>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test05TruncOp
{
    ST *st = [ST newSTWithTemplate:@"<trunc(names); separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    NSString *expected = @"Ter, Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test06RestOp
{
    ST *st = [ST newSTWithTemplate:@"<rest(names); separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    NSString *expected = @"Tom, Sriram";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test07RestOpEmptyList
{
    ST *st = [ST newSTWithTemplate:@"<rest(names); separator=\", \">"];
    [st add:@"names" value:[AMutableArray arrayWithCapacity:16]];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test08ReUseOfRestResult
{
    NSString *templates =@"a(names) ::= \"<b(rest(names))>\"\nb(x) ::= \"<x>, <x>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *st = [group getInstanceOf:@"a"];
    AMutableArray *names = [AMutableArray arrayWithCapacity:16];
    [names addObject:@"Ter"];
    [names addObject:@"Tom"];
    [st add:@"names" value:names];
    NSString *expected = @"Tom, Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test09LastOp
{
    ST *st = [ST newSTWithTemplate:@"<last(names)>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    NSString *expected = @"Sriram";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test10StripOp
{
    ST *st = [ST newSTWithTemplate:@"<strip(names); null=\"n/a\">"];
    [st add:@"names" value:nil];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:nil];
    [st add:@"names" value:nil];
    [st add:@"names" value:@"Sriram"];
    [st add:@"names" value:nil];
    NSString *expected = @"TomSriram";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test11LengthStrip
{
    ST *st = [ST newSTWithTemplate:@"<length(strip(names))>"];
    [st add:@"names" value:nil];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:nil];
    [st add:@"names" value:nil];
    [st add:@"names" value:@"Sriram"];
    [st add:@"names" value:nil];
    NSString *expected = @"2";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test12CombinedOp
{
    ST *st = [ST newSTWithTemplate:@"<[first(mine),rest(yours)]; separator=\", \">"];
    [st add:@"mine" value:@"1"];
    [st add:@"mine" value:@"2"];
    [st add:@"mine" value:@"3"];
    [st add:@"yours" value:@"a"];
    [st add:@"yours" value:@"b"];
    NSString *expected = @"1, b";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test13CatListAndSingleAttribute
{
    ST *st = [ST newSTWithTemplate:@"<[mine,yours]; separator=\", \">"];
    [st add:@"mine" value:@"1"];
    [st add:@"mine" value:@"2"];
    [st add:@"mine" value:@"3"];
    [st add:@"yours" value:@"a"];
    NSString *expected = @"1, 2, 3, a";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test14ReUseOfCat
{
    NSString *templates = @"a(mine,yours) ::= \"<b([mine,yours])>\"\nb(x) ::= \"<x>, <x>\"\n";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingPathComponent:@"t.stg"]];
    ST *st = [group getInstanceOf:@"a"];
    AMutableArray *mine = [AMutableArray arrayWithCapacity:16];
    [mine addObject:@"Ter"];
    [mine addObject:@"Tom"];
    [st add:@"mine" value:mine];
    AMutableArray *yours = [AMutableArray arrayWithCapacity:16];
    [yours addObject:@"Foo"];
    [st add:@"yours" value:yours];
    NSString *expected = @"TerTomFoo, TerTomFoo";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test15CatListAndEmptyAttributes
{
    ST *st = [ST newSTWithTemplate:@"<[x,mine,y,yours,z]; separator=\", \">"];
    [st add:@"mine" value:@"1"];
    [st add:@"mine" value:@"2"];
    [st add:@"mine" value:@"3"];
    [st add:@"yours" value:@"a"];
    NSString *expected = @"1, 2, 3, a";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test16NestedOp
{
    ST *st = [ST newSTWithTemplate:@"<first(rest(names))>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    NSString *expected = @"Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test17FirstWithOneAttributeOp
{
    ST *st = [ST newSTWithTemplate:@"<first(names)>"];
    [st add:@"names" value:@"Ter"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test18LastWithOneAttributeOp
{
    ST *st = [ST newSTWithTemplate:@"<last(names)>"];
    [st add:@"names" value:@"Ter"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test19LastWithLengthOneListAttributeOp
{
    ST *st = [ST newSTWithTemplate:@"<last(names)>"];
    [st add:@"names" value:((TestFunctions_Anon4 *)[[TestFunctions_Anon4 alloc] init])];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test20RestWithOneAttributeOp
{
    ST *st = [ST newSTWithTemplate:@"<rest(names)>"];
    [st add:@"names" value:@"Ter"];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test21RestWithLengthOneListAttributeOp
{
    ST *st = [ST newSTWithTemplate:@"<rest(names)>"];
    [st add:@"names" value:((TestFunctions_Anon5 *)[[TestFunctions_Anon5 alloc] init])];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test22RepeatedRestOp
{
    ST *st = [ST newSTWithTemplate:@"<rest(names)>, <rest(names)>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    NSString *expected = @"Tom, Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test23IncomingLists
{
    ST *st = [ST newSTWithTemplate:@"<rest(names)>, <rest(names)>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    NSString *expected = @"Tom, Tom";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test24FirstWithCatAttribute
{
    ST *st = [ST newSTWithTemplate:@"<first([names,phones])>"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"phones" value:@"1"];
    [st add:@"phones" value:@"2"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test25FirstWithListOfMaps
{
    ST *st = [ST newSTWithTemplate:@"<first(maps).Ter>"];
    AMutableDictionary *m1 = [AMutableDictionary dictionaryWithCapacity:16];
    AMutableDictionary *m2 = [AMutableDictionary dictionaryWithCapacity:16];
    [m1 setObject:@"x5707" forKey:@"Ter"];
    [st add:@"maps" value:m1];
    [m2 setObject:@"x5332" forKey:@"Tom"];
    [st add:@"maps" value:m2];
    NSString *expected = @"x5707";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    AMutableArray *list = [TestFunctions_Anon6 newAnon:m1 b:m2];
    [st add:@"maps" value:list];
    expected = @"x5707";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test26FirstWithListOfMaps2
{
    ST *st = [ST newSTWithTemplate:@"<first(maps):{ m | <m>!}>"];
    AMutableDictionary *m1 = [AMutableDictionary dictionaryWithCapacity:16];
    AMutableDictionary *m2 = [AMutableDictionary dictionaryWithCapacity:16];
    [m1 setObject:@"x5707" forKey:@"Ter"];
    [st add:@"maps" value:m1];
    [m2 setObject:@"x5332" forKey:@"Tom"];
    [st add:@"maps" value:m2];
    NSString *expected = @"Ter!";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    AMutableArray *list = [TestFunctions_Anon7 newAnon:m1 b:m2];
    [st add:@"maps" value:list];
    expected = @"Ter!";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test27Trim
{
    ST *st = [ST newSTWithTemplate:@"<trim(name)>"];
    [st add:@"name" value:@" Ter  \n"];
    NSString *expected = @"Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test28Strlen
{
    ST *st = [ST newSTWithTemplate:@"<strlen(name)>"];
    [st add:@"name" value:@"012345"];
    NSString *expected = @"6";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

- (void) test29Reverse
{
    ST *st = [ST newSTWithTemplate:@"<reverse(names); separator=\", \">"];
    [st add:@"names" value:@"Ter"];
    [st add:@"names" value:@"Tom"];
    [st add:@"names" value:@"Sriram"];
    NSString *expected = @"Sriram, Tom, Ter";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    return;
}

@end
