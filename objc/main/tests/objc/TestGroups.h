#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "ErrorBuffer.h"

@interface Field_anon1 : NSObject {
  NSString *name;
  int n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;

@end

@interface Field_anon2 : NSObject {
  NSString *name;
  int n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;
@end

@interface Field_anon3 : NSObject {
  NSString *name;
  int n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;
@end

@interface Counter : NSObject {
  int n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;
@end

@interface TestGroups : BaseTest {
}

- (void) test01SimpleGroup;
- (void) test02SimpleGroupFromString;
- (void) test03GroupWithTwoTemplates;
- (void) test04Subdir;
- (void) test05SubdirWithSubtemplate;
- (void) test06GroupFileInDir;
- (void) test07SubSubdir;
- (void) test08GroupFileInSubDir;
- (void) test09RefToAnotherTemplateInSameGroup;
- (void) test10RefToAnotherTemplateInSameSubdir;
- (void) test11DupDef;
- (void) test12Alias;
- (void) test13AliasWithArgs;
- (void) test14SimpleDefaultArg;
- (void) test15DefaultArgument;
- (void) test16BooleanDefaultArguments;
- (void) test17DefaultArgument2;
- (void) test18SubtemplateAsDefaultArgSeesOtherArgs;
- (void) test19DefaultArgumentAsSimpleTemplate;
- (void) test20DefaultArgumentManuallySet;
- (void) test21DefaultArgumentSeesVarFromDynamicScoping;
- (void) test22DefaultArgumentImplicitlySet2;
- (void) test23DefaultArgumentAsTemplate;
- (void) test24DefaultArgumentAsTemplate2;
- (void) test25DoNotUseDefaultArgument;
- (void) test26DefaultArgumentInParensToEvalEarly;
- (void) test27TrueFalseArgs;
- (void) test28NamedArgsInOrder;
- (void) test29NamedArgsOutOfOrder;
- (void) test30UnknownNamedArg;
- (void) test31MissingNamedArg;
- (void) test32NamedArgsNotAllowInIndirectInclude;
- (void) test33CantSeeGroupDirIfGroupFileOfSameName;
- (void) test34FullyQualifiedGetInstanceOf;
- (void) test35FullyQualifiedTemplateRef;
- (void) test36FullyQualifiedTemplateRef2;
- (void) test37UnloadingSimpleGroup;
- (void) test38UnloadingGroupFile;

@end
