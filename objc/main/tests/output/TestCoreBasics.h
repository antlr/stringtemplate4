#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ErrorBuffer.h"
#import "STException.h"
#import "STRuntimeMessage.h"
#import "StringWriter.h"
#import "BaseTest.h"

@interface TestCoreBasics_Anon1 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestCoreBasics_Anon2 : NSObject <NSMutableDictionary> {
}

- (void) init;
@end

@interface TestCoreBasics_Anon3 : NSObject <LinkedHashMap> {
}

- (void) init;
@end

@interface TestCoreBasics_Anon4 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestCoreBasics : BaseTest {
}

- (void) testNullAttr;
- (void) testAttr;
- (void) testChainAttr;
- (void) testSetUnknownAttr;
- (void) testMultiAttr;
- (void) testAttrIsList;
- (void) testAttrIsArray;
- (void) testProp;
- (void) testPropWithNoAttr;
- (void) testMapAcrossDictionaryUsesKeys;
- (void) testSTProp;
- (void) testBooleanISProp;
- (void) testBooleanHASProp;
- (void) testNullAttrProp;
- (void) testNoSuchProp;
- (void) testNullIndirectProp;
- (void) testPropConvertsToString;
- (void) testInclude;
- (void) testIncludeWithArg;
- (void) testIncludeWithArg2;
- (void) testIncludeWithNestedArgs;
- (void) testDefineTemplate;
- (void) testMap;
- (void) testIndirectMap;
- (void) testMapWithExprAsTemplateName;
- (void) testParallelMap;
- (void) testParallelMapWith3Versus2Elements;
- (void) testParallelMapThenMap;
- (void) testMapThenParallelMap;
- (void) testMapIndexes;
- (void) testMapIndexes2;
- (void) testMapSingleValue;
- (void) testMapNullValue;
- (void) testMapNullValueInList;
- (void) testRepeatedMap;
- (void) testRoundRobinMap;
- (void) testTrueCond;
- (void) testEmptyIFTemplate;
- (void) testCondParens;
- (void) testFalseCond;
- (void) testFalseCond2;
- (void) testFalseCondWithFormalArgs;
- (void) testElseIf2;
- (void) testElseIf3;
- (void) testNotTrueCond;
- (void) testNotFalseCond;
- (void) testParensInConditonal;
- (void) testParensInConditonal2;
- (void) testTrueCondWithElse;
- (void) testFalseCondWithElse;
- (void) testElseIf;
- (void) testElseIfNoElseAllFalse;
- (void) testElseIfAllExprFalse;
- (void) testOr;
- (void) testMapConditionAndEscapeInside;
- (void) testAnd;
- (void) testAndNot;
- (void) testCharLiterals;
- (void) testUnicodeLiterals;
- (void) testSubtemplateExpr;
- (void) testSeparator;
- (void) testSeparatorInList;
- (void) testEarlyEvalIndent;
- (void) testEarlyEvalNoIndent;
- (void) playing;
@end
