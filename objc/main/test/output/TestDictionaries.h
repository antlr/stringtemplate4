#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "ErrorBuffer.h"
#import "NSMutableDictionary.h"
#import "NSMutableDictionary.h"
#import "Assert.h"

@interface TestDictionaries : BaseTest {
}

- (void) testDict;
- (void) testDictValuesAreTemplates;
- (void) testDictKeyLookupViaTemplate;
- (void) testDictKeyLookupAsNonToStringableObject;
- (void) testDictMissingDefaultValueIsEmpty;
- (void) testDictMissingDefaultValueIsEmptyForNullKey;
- (void) testDictHiddenByFormalArg;
- (void) testDictEmptyValueAndAngleBracketStrings;
- (void) testDictDefaultValue;
- (void) testDictNullKeyGetsDefaultValue;
- (void) testDictEmptyDefaultValue;
- (void) testDictDefaultValueIsKey;
- (void) testDictDefaultStringAsKey;
- (void) testDictDefaultIsDefaultString;
- (void) testDictViaEnclosingTemplates;
- (void) testDictViaEnclosingTemplates2;
- (void) TestAccessDictionaryFromAnonymousTemplate;
- (void) TestAccessDictionaryFromAnonymousTemplateInRegion;
@end
