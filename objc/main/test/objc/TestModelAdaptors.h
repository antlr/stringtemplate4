#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ModelAdaptor.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "STNoSuchPropertyException.h"
#import "STRuntimeMessage.h"

@interface UserAdaptor : NSObject <ModelAdaptor> {
}

- (NSObject *) getProperty:(ST *)self o:(NSObject *)o property:(NSObject *)property propertyName:(NSString *)propertyName;
@end

@interface UserAdaptorConst : NSObject <ModelAdaptor> {
}

- (NSObject *) getProperty:(ST *)self o:(NSObject *)o property:(NSObject *)property propertyName:(NSString *)propertyName;
@end

@interface SuperUser : User {
  int bitmask;
}

@property(nonatomic, retain, readonly) NSString *name;
- (id) init:(int)id name:(NSString *)name;
@end

@interface TestModelAdaptors : SenTestCase {
}

- (void) testSimpleAdaptor;
- (void) testAdaptorAndBadProp;
- (void) testAdaptorCoversSubclass;
- (void) testWeCanResetAdaptorCacheInvalidatedUponAdaptorReset;
- (void) testSeesMostSpecificAdaptor;
@end
