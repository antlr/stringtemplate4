#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "STGroup.h"
#import "CompiledST.h"
#import "Compiler.h"
#import "ErrorBuffer.h"
#import "ErrorManager.h"
#import "BaseTest.h"
#import "Writer.h"

@interface TestCompiler : BaseTest {
}

- (void) test01Attr;
- (void) test02Include;
- (void) test02aIncludeWithPassThrough;
- (void) test02bIncludeWithPartialPassThrough;
- (void) test03SuperInclude;
- (void) test04SuperIncludeWithArgs;
- (void) test05SuperIncludeWithNamedArgs;
- (void) test06IncludeWithArgs;
- (void) test07AnonIncludeArgs;
- (void) test08AnonIncludeArgMismatch;
- (void) test09AnonIncludeArgMismatch2;
- (void) test10AnonIncludeArgMismatch3;
- (void) test11IndirectIncludeWithArgs;
- (void) test12Prop;
- (void) test13Prop2;
- (void) test14Map;
- (void) test15MapAsOption;
- (void) test16MapArg;
- (void) test17IndirectMapArg;
- (void) test18RepeatedMap;
- (void) test19RepeatedMapArg;
- (void) test20RotMap;
- (void) test21RotMapArg;
- (void) test22ZipMap;
- (void) test23ZipMapArg;
- (void) test24AnonMap;
- (void) test25AnonZipMap;
- (void) test26If;
- (void) test27IfElse;
- (void) test28ElseIf;
- (void) test29ElseIfElse;
- (void) test30Option;
- (void) test31OptionAsTemplate;
- (void) test32Options;
- (void) test33EmptyList;
- (void) test34List;
- (void) test35EmbeddedRegion;
- (void) test36Region;
@end
