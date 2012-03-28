#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import "ErrorBuffer.h"

@interface Field_anon1 : NSObject {
  NSString *name;
  NSInteger n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;

@property (retain) NSString *name;
@property (assign) NSInteger n;
@end

@interface Field_anon2 : NSObject {
  NSString *name;
  NSInteger n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;

@property (retain) NSString *name;
@property (assign) NSInteger n;
@end

@interface Field_anon3 : NSObject {
  NSString *name;
  NSInteger n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;

@property (retain) NSString *name;
@property (assign) NSInteger n;
@end

@interface Counter : NSObject {
  int n;
}

- (id) init;
- (NSString *) description;
- (NSString *) toString;

@property (assign) NSInteger n;
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
#ifdef DONTUSENOMO
- (void) test08aRefToAnotherTemplateInSameGroup;
- (void) test08bRefToAnotherTemplateInSameSubdir;
#endif
- (void) test09DupDef;
- (void) test10Alias;
- (void) test11AliasWithArgs;
- (void) test12SimpleDefaultArg;
- (void) test13DefaultArgument;
- (void) test14BooleanDefaultArguments;
- (void) test15DefaultArgument2;
- (void) test16SubtemplateAsDefaultArgSeesOtherArgs;
- (void) test17EarlyEvalOfDefaultArgs;
- (void) test18DefaultArgumentAsSimpleTemplate;
- (void) test19DefaultArgumentManuallySet;
- (void) test20DefaultArgumentSeesVarFromDynamicScoping;
- (void) test21DefaultArgumentImplicitlySet2;
- (void) test22DefaultArgumentAsTemplate;
- (void) test23DefaultArgumentAsTemplate2;
- (void) test24DoNotUseDefaultArgument;
- (void) test25DefaultArgumentInParensToEvalEarly;
- (void) test26TrueFalseArgs;
- (void) test27NamedArgsInOrder;
- (void) test28NamedArgsOutOfOrder;
- (void) test29UnknownNamedArg;
- (void) test30MissingNamedArg;
- (void) test31NamedArgsNotAllowInIndirectInclude;
- (void) test32CantSeeGroupDirIfGroupFileOfSameName;
- (void) test33UnloadingSimpleGroup;
- (void) test34UnloadingGroupFile;
- (void) test35GroupFileImport;
#ifdef DONTUSEYET
- (void) test36GetTemplateNames;
- (void) test37UnloadWithImports;
#endif
#ifdef DONTUSENOMO
- (void) test33FullyQualifiedGetInstanceOf;
- (void) test34FullyQualifiedTemplateRef;
- (void) test35FullyQualifiedTemplateRef2;
#endif

@end
