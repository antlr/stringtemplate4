#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <GHUnit/GHTestCase.h>
#import "ErrorBuffer.h"
#import "STException.h"
#import "STRuntimeMessage.h"
#import "StringWriter.h"
#import "BaseTest.h"

@interface TestCoreBasics_Anon1 : AMutableArray {
}

+ (id) newAnon;
- (id) init;

@end

@interface TestCoreBasics_Anon2 : NSObject {
    AMutableDictionary *aDict;
}

+ (id) newAnon;
- (id) init;
- (void) setObject:(id)anObj forKey:(id)aName;
- (id) objectForKey:(id)aKey;

@property (retain) AMutableDictionary *aDict;
@end

@interface TestCoreBasics_Anon3 : NSObject {
    AMutableDictionary *aDict;
}

+ (id) newAnon;
- (id) init;
- (void) setObject:(id)anObj forKey:(id)aName;
- (id) objectForKey:(id)aKey;

@property (retain) AMutableDictionary *aDict;
@end


@interface TestCoreBasics_Anon4 : AMutableArray {
}

+ (id) newAnon;
- (id) init;

@end

@interface TestCoreBasics : BaseTest {
}
- (void)setUp;
- (void)tearDown;

- (void) test01NullAttr;
- (void) test02Attr;
//- (void) test02aChainAttr;
- (void) test03SetUnknownAttr;
- (void) test04MultiAttr;
- (void) test05AttrIsList;
- (void) test06AttrIsArray;
- (void) test07Prop;
- (void) test08PropWithNoAttr;
- (void) test09STProp;
- (void) test10BooleanISProp;
- (void) test11BooleanHASProp;
- (void) test12NullAttrProp;
- (void) test13NoSuchProp;
- (void) test14NullIndirectProp;
- (void) test15PropConvertsToString;
- (void) test16Include;
- (void) test17IncludeWithArg;
- (void) test18IncludeWithArg2;
//#ifdef DONTUSEYET
- (void) test18aPassThruWithDefaultValue;
- (void) test18bPassThruWithDefaultValueThatLacksDefinitionAbove;
- (void) test18cPassThruPartialArgs;
- (void) test18dPassThruNoMissingArgs;
//#endif
- (void) test19IncludeWithNestedArgs;
- (void) test20DefineTemplate;
- (void) test21Map;
- (void) test22IndirectMap;
- (void) test23MapWithExprAsTemplateName;
- (void) test24ParallelMap;
- (void) test25ParallelMapWith3Versus2Elements;
- (void) test26ParallelMapThenMap;
- (void) test27MapThenParallelMap;
- (void) test28MapIndexes;
- (void) test29MapIndexes2;
- (void) test30MapSingleValue;
- (void) test31MapNullValue;
- (void) test32MapNullValueInList;
- (void) test33RepeatedMap;
- (void) test34RoundRobinMap;
- (void) test35TrueCond;
- (void) test36EmptyIFTemplate;
- (void) test37CondParens;
- (void) test38FalseCond;
- (void) test39FalseCond2;
- (void) test40FalseCondWithFormalArgs;
- (void) test41ElseIf2;
- (void) test42ElseIf3;
- (void) test43NotTrueCond;
- (void) test44NotFalseCond;
- (void) test45ParensInConditonal;
- (void) test46ParensInConditonal2;
- (void) test47TrueCondWithElse;
- (void) test48FalseCondWithElse;
- (void) test49ElseIf;
- (void) test50ElseIfNoElseAllFalse;
- (void) test51ElseIfAllExprFalse;
- (void) test52Or;
- (void) test53MapConditionAndEscapeInside;
- (void) test54And;
- (void) test55AndNot;
- (void) test56CharLiterals;
//- (void) test57UnicodeLiterals;
- (void) test58SubtemplateExpr;
- (void) test59Separator;
- (void) test60SeparatorInList;
- (void) test61EarlyEvalIndent;
- (void) test62EarlyEvalNoIndent;
- (void) test63ArrayOfTemplates;
- (void) test64ArrayOfTemplatesInTemplate;     
- (void) test65ListOfTemplates;     
- (void) test66ListOfTemplatesInTemplate;
- (void) playing;

@end
