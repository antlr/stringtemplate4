#import "TestModelAdaptors.h"
#import "STException.h"
#import "ErrorBufferAllErrors.h"

@implementation UserAdaptor

- (id) getProperty:(Interpreter *)interp
               who:(ST *)aWho
               obj:(id)anObj
          property:(id)aProperty
      propertyName:(NSString *)aPropertyName
{
    if ([aPropertyName isEqualToString:@"num"])
        return ((User *)anObj).num;
    if ([aPropertyName isEqualToString:@"name"])
        return [((User *)anObj) name];
    @throw [[STNoSuchPropertyException newException:[@"User." stringByAppendingString:aPropertyName]] autorelease];
}

@end

@implementation UserAdaptorConst

- (id) getProperty:(Interpreter *)interp
               who:(ST *)aWho
               obj:(id)anObj
          property:(id)aProperty
      propertyName:(NSString *)aPropertyName
{
    if ([aPropertyName isEqualToString:@"num"])
        return @"const num value";
    if ([aPropertyName isEqualToString:@"name"])
        return @"const name value";
    @throw [STNoSuchPropertyException newException:[NSString stringWithFormat:@"User.%@", aPropertyName]];
}

@end

@implementation SuperUser

@synthesize name;

- (id) init:(int)aNum name:(NSString *)aName
{
    self=[super init:aNum name:aName];
    if ( self ) {
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
    NSString *templates = @"foo(x) ::= \"<x.num>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group registerModelAdaptor:[User class] adaptor:[[UserAdaptor alloc] init]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"100: parrt";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test02AdaptorAndBadProp
{
    ErrorBufferAllErrors *errors = [[ErrorBufferAllErrors alloc] init];
    NSString *templates = @"foo(x) ::= \"<x.qqq>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group setListener:errors];
    [group registerModelAdaptor:[User class] adaptor:[[UserAdaptor alloc] init]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
    STRuntimeMessage *msg = (STRuntimeMessage *)[errors.errors objectAtIndex:0];
    STNoSuchPropertyException *e = (STNoSuchPropertyException *)msg.cause;
    expected = @"User.qqq";
    result = e.propertyName;
    [self assertEquals:expected result:result];
}

- (void) test03AdaptorCoversSubclass
{
    NSString *templates = @"foo(x) ::= \"<x.num>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group registerModelAdaptor:[User class] adaptor:[[UserAdaptor alloc] init]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[[[SuperUser alloc] init:100 name:@"parrt"] autorelease]];
    NSString *expected = @"100: super parrt";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test04WeCanResetAdaptorCacheInvalidatedUponAdaptorReset
{
    NSString *templates = @"foo(x) ::= \"<x.num>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group registerModelAdaptor:[User class] adaptor:[[UserAdaptor alloc] init]];
    [group getModelAdaptor:[User class]];
    [group getModelAdaptor:[SuperUser class]];
    [group registerModelAdaptor:[User class] adaptor:[[[UserAdaptorConst alloc] init] autorelease]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"const num value: const name value";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
}

- (void) test05SeesMostSpecificAdaptor
{
    NSString *templates = @"foo(x) ::= \"<x.num>: <x.name>\"\n";
    [self writeFile:tmpdir fileName:@"foo.stg" content:templates];
    STGroup *group = [STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/foo.stg", tmpdir]];
    [group registerModelAdaptor:[User class] adaptor:[[[UserAdaptor alloc] init] autorelease]];
    [group registerModelAdaptor:[SuperUser class] adaptor:[[[UserAdaptorConst alloc] init] autorelease]];
    ST *st = [group getInstanceOf:@"foo"];
    [st add:@"x" value:[User newUser:100 name:@"parrt"]];
    NSString *expected = @"100: parrt";
    NSString *result = [st render];
    [self assertEquals:expected result:result];
    [st remove:@"x"];
    [st add:@"x" value:[[[SuperUser alloc] init:100 name:@"parrt"] autorelease]];
    expected = @"const num value: const name value";
    result = [st render];
    [self assertEquals:expected result:result];
}

@end
