#import "TestModelAdaptors.h"

@implementation UserAdaptor

- (NSObject *) getProperty:(ST *)self o:(NSObject *)o property:(NSObject *)property propertyName:(NSString *)propertyName {
    if ([propertyName isEqualToString:@"id"])
        return ((User *)o).id;
    if ([propertyName isEqualToString:@"name"])
        return [((User *)o) name];
    @throw [[[STNoSuchPropertyException alloc] init:nil arg1:[@"User." stringByAppendingString:propertyName]] autorelease];
}

@end

@implementation UserAdaptorConst

- (NSObject *) getProperty:(ST *)self o:(NSObject *)o property:(NSObject *)property propertyName:(NSString *)propertyName {
    if ([propertyName isEqualToString:@"id"])
        return @"const id value";
    if ([propertyName isEqualToString:@"name"])
        return @"const name value";
    @throw [STNoSuchPropertyException newException:[NSString stringWithFormat:@"User.%@", propertyName]];
}

@end

@implementation SuperUser

@synthesize name;

- (id) init:(int)id name:(NSString *)name {
    if (self=[super init:id arg1:name]) {
        bitmask = 0x8080;
    }
    return self;
}

- (NSString *) name {
    return [@"super " stringByAppendingString:[super name]];
}

@end

@implementation TestModelAdaptors

- (void) test01SimpleAdaptor
{
    NSString *templates = @"foo(x) ::= \"<x.id>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group registerModelAdaptor:[User class] arg1:[[UserAdaptor alloc] init]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"100: parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test02AdaptorAndBadProp
{
    ErrorBufferAllErrors *errors = [[ErrorBufferAllErrors alloc] init];
    NSString *templates = @"foo(x) ::= \"<x.qqq>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/foo.stg"]];
    [group setListener:errors];
    [group registerModelAdaptor:[User class] arg1:[[UserAdaptor alloc] init]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    STRuntimeMessage *msg = (STRuntimeMessage *)[errors.errors get:0];
    STNoSuchPropertyException *e = (STNoSuchPropertyException *)msg.cause;
    NSString *expected = @"User.qqq";
    result = e.propertyName;
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test03AdaptorCoversSubclass
{
    NSString *templates = @"foo(x) ::= \"<x.id>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/foo.stg"]];
    [group registerModelAdaptor:[User class] arg1:[[UserAdaptor alloc] init]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[[[SuperUser alloc] init:100 arg1:@"parrt"] autorelease]];
    NSString *expected = @"100: super parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test04WeCanResetAdaptorCacheInvalidatedUponAdaptorReset
{
    NSString *templates = @"foo(x) ::= \"<x.id>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[tmpdir stringByAppendingString:@"/foo.stg"]];
    [group registerModelAdaptor:[User class] arg1:[[UserAdaptor alloc] init]];
    [group getModelAdaptor:[User class]];
    [group getModelAdaptor:[SuperUser class]];
    [group registerModelAdaptor:[User class] arg1:[[[UserAdaptorConst alloc] init] autorelease]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"const id value: const name value";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

- (void) test05SeesMostSpecificAdaptor
{
    NSString *templates = @"foo(x) ::= \"<x.id>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group registerModelAdaptor:[User class] arg1:[[[UserAdaptor alloc] init] autorelease]];
    [group registerModelAdaptor:[SuperUser class] arg1:[[[UserAdaptorConst alloc] init] autorelease]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"100: parrt";
    NSString *result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
    [st remove:@"x"];
    [st add:@"x" value:[[[SuperUser alloc] init:100 arg1:@"parrt"] autorelease]];
    expected = @"const id value: const name value";
    result = [st render];
    STAssertTrue( [expected isEqualTo:result], @"Expected \"%@\" but got \"%@\"", expected, result );
}

@end
