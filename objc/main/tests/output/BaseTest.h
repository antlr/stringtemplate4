#import "ANTLRStringStream.h"
#import "CommonTokenStream.h"
#import "Token.h"
#import "Before.h"
#import "ST.h"
#import "STGroup.h"
#import "Compiler.h"
#import "STLexer.h"
#import "Misc.h"
#import "NSMutableArray.h"
#import "Arrays.h"
#import "NSMutableArray.h"
#import "Assert.h"

@interface StreamVacuum : NSObject <Runnable> {
  StringBuffer *buf;
  BufferedReader *in;
  Thread *sucker;
}

- (id) initWithIn:(InputStream *)in;
- (void) start;
- (void) run;
- (void) join;
- (NSString *) description;
@end

@interface User : NSObject {
  int id;
  NSString *name;
}

@property(nonatomic, readonly) BOOL manager;
@property(nonatomic, retain, readonly) NSString *name;
- (id) init:(int)id name:(NSString *)name;
- (BOOL) hasParkingSpot;
@end

@interface HashableUser : User {
}

- (id) init:(int)id name:(NSString *)name;
- (int) hash;
- (BOOL) isEqualTo:(NSObject *)o;
@end

extern NSString *const pathSep;
extern NSString *const tmpdir;
extern NSString *const newline;

/**
 * When runnning from Maven, the junit tests are run via the surefire plugin. It sets the
 * classpath for the test environment into the following property. We need to pick this up
 * for the junit tests that are going to generate and try to run code.
 */
extern NSString *const SUREFIRE_CLASSPATH;
extern NSString *const CLASSPATH;

@interface BaseTest : NSObject {
}

@property(nonatomic, retain, readonly) NSString *randomDir;
- (void) setUp;
- (void) writeTestFile:(NSString *)main dirName:(NSString *)dirName;
- (NSString *) java:(NSString *)mainClassName extraCLASSPATH:(NSString *)extraCLASSPATH workingDirName:(NSString *)workingDirName;
- (void) jar:(NSString *)fileName files:(NSArray *)files workingDirName:(NSString *)workingDirName;
- (void) compile:(NSString *)fileName workingDirName:(NSString *)workingDirName;
- (NSString *) exec:(NSArray *)args envp:(NSArray *)envp workingDirName:(NSString *)workingDirName;
+ (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
@end
