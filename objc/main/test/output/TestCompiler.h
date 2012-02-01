#import "Before.h"
#import "Test.h"
#import "STErrorListener.h"
#import "STGroup.h"
#import "CompiledST.h"
#import "Compiler.h"
#import "ErrorBuffer.h"
#import "ErrorManager.h"
#import "Arrays.h"
#import "Assert.h"

@interface TestCompiler : BaseTest {
}

- (void) setUp;
- (void) testAttr;
- (void) testInclude;
- (void) testSuperInclude;
- (void) testSuperIncludeWithArgs;
- (void) testSuperIncludeWithNamedArgs;
- (void) testIncludeWithArgs;
- (void) testAnonIncludeArgs;
- (void) testAnonIncludeArgMismatch;
- (void) testAnonIncludeArgMismatch2;
- (void) testAnonIncludeArgMismatch3;
- (void) testIndirectIncludeWitArgs;
- (void) testProp;
- (void) testProp2;
- (void) testMap;
- (void) testMapAsOption;
- (void) testMapArg;
- (void) testIndirectMapArg;
- (void) testRepeatedMap;
- (void) testRepeatedMapArg;
- (void) testRotMap;
- (void) testRotMapArg;
- (void) testZipMap;
- (void) testZipMapArg;
- (void) testAnonMap;
- (void) testAnonZipMap;
- (void) testIf;
- (void) testIfElse;
- (void) testElseIf;
- (void) testElseIfElse;
- (void) testOption;
- (void) testOptionAsTemplate;
- (void) testOptions;
- (void) testEmptyList;
- (void) testList;
- (void) testEmbeddedRegion;
- (void) testRegion;
@end
