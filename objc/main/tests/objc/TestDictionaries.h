#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "BaseTest.h"
#import <GHUnit/GHTestCase.h>
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"

@interface TestDictionaries : BaseTest {
}

- (void) test01Dict;
- (void) test02DictValuesAreTemplates;
- (void) test03DictKeyLookupViaTemplate;
- (void) test04DictKeyLookupAsNonToStringableObject;
- (void) test05DictMissingDefaultValueIsEmpty;
- (void) test06DictMissingDefaultValueIsEmptyForNullKey;
- (void) test07DictHiddenByFormalArg;
- (void) test08DictEmptyValueAndAngleBracketStrings;
- (void) test09DictDefaultValue;
- (void) test10DictNullKeyGetsDefaultValue;
- (void) test11DictEmptyDefaultValue;
- (void) test12DictDefaultValueIsKey;
- (void) test13DictDefaultStringAsKey;
- (void) test14DictDefaultIsDefaultString;
- (void) test15DictViaEnclosingTemplates;
- (void) test16DictViaEnclosingTemplates2;
- (void) Test17AccessDictionaryFromAnonymousTemplate;
- (void) Test18AccessDictionaryFromAnonymousTemplateInRegion;
@end
