#import "Test.h"
#import "ErrorBuffer.h"
#import "Assert.h"

@interface Field : NSObject {
  NSString * name;
  int n;
}

- (void) init;
- (NSString *) description;
@end

@interface Field : NSObject {
  NSString * name;
  int n;
}

- (void) init;
- (NSString *) description;
@end

@interface Field : NSObject {
  NSString * name;
  int n;
}

- (void) init;
- (NSString *) description;
@end

@interface Counter : NSObject {
  int n;
}

- (void) init;
- (NSString *) description;
@end

@interface TestGroups : BaseTest {
}

- (void) testSimpleGroup;
- (void) testSimpleGroupFromString;
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
@end
