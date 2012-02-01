#import <ANTLR/ANTLR.h>
#import <Cocoa/Cocoa.h>
#import "BaseTest.h"

@implementation StreamVacuum

- (id) initWithIn:(InputStream *)in {
  if (self = [super init]) {
    buf = [[[StringBuffer alloc] init] autorelease];
    in = [[[BufferedReader alloc] init:[[[InputStreamReader alloc] init:in] autorelease]] autorelease];
  }
  return self;
}

- (void) start {
  sucker = [[[Thread alloc] init:self] autorelease];
  [sucker start];
}

- (void) run {

  @try {
    NSString * line = [in readLine];

    while (line != nil) {
      [buf append:line];
      [buf append:'\n'];
      line = [in readLine];
    }

  }
  @catch (IOException * ioe) {
    [System.err println:@"can't read output from process"];
  }
}


/**
 * wait for the thread to finish
 */
- (void) join {
  [sucker join];
}

- (NSString *) description {
  return [buf description];
}

- (void) dealloc {
  [buf release];
  [in release];
  [sucker release];
  [super dealloc];
}

@end

@implementation User

@synthesize manager;
@synthesize name;

- (id) init:(int)id name:(NSString *)name {
  if (self = [super init]) {
    id = id;
    name = name;
  }
  return self;
}

- (BOOL) hasParkingSpot {
  return YES;
}

- (void) dealloc {
  [name release];
  [super dealloc];
}

@end

@implementation HashableUser

- (id) init:(int)id name:(NSString *)name {
  if (self = [super init:id param1:name]) {
  }
  return self;
}

- (int) hash {
  return id;
}

- (BOOL) isEqualTo:(NSObject *)o {
  if ([o conformsToProtocol:@protocol(HashableUser)]) {
    HashableUser * hu = (HashableUser *)o;
    return id == hu.id && [name isEqualTo:hu.name];
  }
  return NO;
}

@end

NSString * const pathSep = [System getProperty:@"path.separator"];
NSString * const tmpdir = [System getProperty:@"java.io.tmpdir"];
NSString * const newline = Misc.newline;

/**
 * When runnning from Maven, the junit tests are run via the surefire plugin. It sets the
 * classpath for the test environment into the following property. We need to pick this up
 * for the junit tests that are going to generate and try to run code.
 */
NSString * const SUREFIRE_CLASSPATH = [System getProperty:@"surefire.test.class.path" param1:@""];
NSString * const CLASSPATH = [System getProperty:@"java.class.path"] + ([SUREFIRE_CLASSPATH isEqualToString:@""] ? @"" : [pathSep stringByAppendingString:SUREFIRE_CLASSPATH]);

@implementation BaseTest

@synthesize randomDir;

- (void) setUp {
  STGroup.defaultGroup = [[[STGroup alloc] init] autorelease];
  Compiler.subtemplateCount = 0;
  STGroup.debug = NO;
}

- (void) writeTestFile:(NSString *)main dirName:(NSString *)dirName {
  ST * outputFileST = [[[ST alloc] init:[[[[[[[[[@"import org.antlr.runtime.*;\n" stringByAppendingString:@"import org.stringtemplate.v4.*;\n"] stringByAppendingString:@"import org.antlr.runtime.tree.*;\n"] stringByAppendingString:@"\n"] stringByAppendingString:@"public class Test {\n"] stringByAppendingString:@"    public static void main(String[] args) throws Exception {\n"] stringByAppendingString:@"        <code>\n"] stringByAppendingString:@"        System.out.println(result);\n"] stringByAppendingString:@"    }\n"] stringByAppendingString:@"}"]] autorelease];
  [outputFileST add:@"code" param1:main];
  [self writeFile:dirName fileName:@"Test.java" content:[outputFileST render]];
}

- (NSString *) java:(NSString *)mainClassName extraCLASSPATH:(NSString *)extraCLASSPATH workingDirName:(NSString *)workingDirName {
  NSString * classpathOption = @"-classpath";
  NSString * path = [[@"." stringByAppendingString:pathSep] stringByAppendingString:CLASSPATH];
  if (extraCLASSPATH != nil)
    path = [[[[@"." stringByAppendingString:pathSep] stringByAppendingString:extraCLASSPATH] stringByAppendingString:pathSep] stringByAppendingString:CLASSPATH];
  NSArray * args = [NSArray arrayWithObjects:@"java", classpathOption, path, mainClassName, nil];
  [System.out println:[@"executing: " stringByAppendingString:[Arrays description:args]]];
  return [self exec:args envp:nil workingDirName:workingDirName];
}

- (void) jar:(NSString *)fileName files:(NSArray *)files workingDirName:(NSString *)workingDirName {
  NSArray * cmd = [NSArray arrayWithObjects:@"jar", @"cf", fileName, @"Test.class", nil];
  NSMutableArray * list = [[[NSMutableArray alloc] init] autorelease];
  [list addObjectsFromArray:[Arrays asList:cmd]];
  [list addObjectsFromArray:[Arrays asList:files]];
  NSArray * a = [NSArray array];
  [list toArray:a];
  [self exec:a envp:nil workingDirName:workingDirName];
}

- (void) compile:(NSString *)fileName workingDirName:(NSString *)workingDirName {
  NSString * classpathOption = @"-classpath";
  NSArray * args = [NSArray arrayWithObjects:@"javac", classpathOption, [[@"." stringByAppendingString:pathSep] stringByAppendingString:CLASSPATH], fileName, nil];
  [self exec:args envp:nil workingDirName:workingDirName];
}

- (NSString *) exec:(NSArray *)args envp:(NSArray *)envp workingDirName:(NSString *)workingDirName {
  NSString * cmdLine = [Arrays description:args];
  File * workingDir = [[[File alloc] init:workingDirName] autorelease];

  @try {
    Process * process = [[Runtime runtime] exec:args param1:envp param2:workingDir];
    StreamVacuum * stdout = [[[StreamVacuum alloc] init:[process inputStream]] autorelease];
    StreamVacuum * stderr = [[[StreamVacuum alloc] init:[process errorStream]] autorelease];
    [stdout start];
    [stderr start];
    [process waitFor];
    [stdout join];
    [stderr join];
    if ([[stdout description] length] > 0) {
      return [stdout description];
    }
    if ([[stderr description] length] > 0) {
      [System.err println:[@"compile stderr from: " stringByAppendingString:cmdLine]];
      [System.err println:stderr];
    }
    int ret = [process exitValue];
    if (ret != 0)
      [System.err println:@"failed"];
  }
  @catch (NSException * e) {
    [System.err println:@"can't exec compilation"];
    [e printStackTrace:System.err];
  }
  return nil;
}

+ (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content {

  @try {
    File * f = [[[File alloc] init:dir param1:fileName] autorelease];
    if (![[f parentFile] exists])
      [[f parentFile] mkdirs];
    FileWriter * w = [[[FileWriter alloc] init:f] autorelease];
    BufferedWriter * bw = [[[BufferedWriter alloc] init:w] autorelease];
    [bw write:content];
    [bw close];
    [w close];
  }
  @catch (IOException * ioe) {
    [System.err println:@"can't write file"];
    [ioe printStackTrace:System.err];
  }
}

- (void) checkTokens:(NSString *)template expected:(NSString *)expected {
  [self checkTokens:template expected:expected delimiterStartChar:'<' delimiterStopChar:'>'];
}

- (void) checkTokens:(NSString *)template expected:(NSString *)expected delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar {
  STLexer * lexer = [[[STLexer alloc] init:STGroup.DEFAULT_ERR_MGR param1:[[[ANTLRStringStream alloc] init:template] autorelease] param2:nil param3:delimiterStartChar param4:delimiterStopChar] autorelease];
  CommonTokenStream * tokens = [[[CommonTokenStream alloc] init:lexer] autorelease];
  StringBuffer * buf = [[[StringBuffer alloc] init] autorelease];
  [buf append:@"["];
  int i = 1;
  Token * t = [tokens LT:i];

  while ([t type] != Token.EOF) {
    if (i > 1)
      [buf append:@", "];
    [buf append:t];
    i++;
    t = [tokens LT:i];
  }

  [buf append:@"]"];
  NSString * result = [buf description];
  [self assertEquals:expected param1:result];
}

+ (NSString *) randomDir {
  NSString * randomDir = [tmpdir stringByAppendingString:@"dir"] + [String valueOf:(int)([Math random] * 100000)];
  File * f = [[[File alloc] init:randomDir] autorelease];
  [f mkdirs];
  return randomDir;
}

@end
