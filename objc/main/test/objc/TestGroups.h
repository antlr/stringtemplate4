#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ErrorBuffer.h"

@interface Field_anon1 : NSObject {
  NSString *name;
  int n;
}

- (void) init;
- (NSString *) description;
- (NSString *) toString;

@end

@interface Field_anon2 : NSObject {
  NSString *name;
  int n;
}

- (void) init;
- (NSString *) description;
- (NSString *) toString;
@end

@interface Field_anon3 : NSObject {
  NSString *name;
  int n;
}

- (void) init;
- (NSString *) description;
- (NSString *) toString;
@end

@interface Counter : NSObject {
  int n;
}

- (void) init;
- (NSString *) description;
- (NSString *) toString;
@end

@interface TestGroups : SenTestCase {
    NSString *randomDir;
}

+ (NSString *) randomDir;

- (void) testSimpleGroup;
- (void) testGroupWithTwoTemplates;
- (void) testSubdir;
- (void) testAbsoluteTemplateRef;
- (void) testGroupFileInDir;
- (void) testSubSubdir;
- (void) testGroupFileInSubDir;
- (void) testRefToAnotherTemplateInSameGroup;
- (void) testRefToAnotherTemplateInSameSubdir;
- (void) testDupDef;
- (void) testAlias;
- (void) testAliasWithArgs;
- (void) testSimpleDefaultArg;
- (void) testDefaultArgument;
- (void) testBooleanDefaultArguments;
- (void) testDefaultArgument2;
- (void) testSubtemplateAsDefaultArgSeesOtherArgs;
- (void) testDefaultArgumentAsSimpleTemplate;
- (void) testDefaultArgumentManuallySet;
- (void) testDefaultArgumentSeesVarFromDynamicScoping;
- (void) testDefaultArgumentImplicitlySet2;
- (void) testDefaultArgumentAsTemplate;
- (void) testDefaultArgumentAsTemplate2;
- (void) testDoNotUseDefaultArgument;
- (void) testDefaultArgumentInParensToEvalEarly;
- (void) testTrueFalseArgs;
- (void) testNamedArgsInOrder;
- (void) testNamedArgsOutOfOrder;
- (void) testUnknownNamedArg;
- (void) testMissingNamedArg;
- (void) testNamedArgsNotAllowInIndirectInclude;
- (void) testCantSeeGroupDirIfGroupFileOfSameName;
- (void) testFullyQualifiedGetInstanceOf;
- (void) testFullyQualifiedTemplateRef;
- (void) testFullyQualifiedTemplateRef2;
- (void) testUnloadingSimpleGroup;
- (void) testUnloadingGroupFile;

@property(retain) NSString *randomDir;

@end
