#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import <GHUnit/GHTestCase.h>
#import "ST.h"
#import "STGroup.h"
#import "BaseTest.h"

@interface TestFunctions_Anon1 : AMutableArray {
}

- (id) init;

@end

@interface TestFunctions_Anon2 : AMutableArray {
}

+ (id) newAnon;
- (id) init;
@end

@interface TestFunctions_Anon3 : AMutableArray {
}

+ (id) newAnon;
- (id) init;
@end

@interface TestFunctions_Anon4 : AMutableArray {
}

+ (id) newAnon;
- (id) init;
@end

@interface TestFunctions_Anon5 : AMutableArray {
}

+ (id) newAnon;
- (id) init;
@end

@interface TestFunctions_Anon6 : AMutableArray {
}

+ (id) newAnon:(id)a b:(id)b;
- (id) init:a b:(id)b;
@end

@interface TestFunctions_Anon7 : AMutableArray {
}

+ (id) newAnon:(id)a b:(id)b;
- (id) init:a b:(id)b;
@end

@interface TestFunctions : BaseTest {
}

- (void) test01First;
- (void) test02Length;
- (void) test03LengthWithNullValues;
- (void) test04FirstOp;
- (void) test05TruncOp;
- (void) test06RestOp;
- (void) test07RestOpEmptyList;
- (void) test08ReUseOfRestResult;
- (void) test09LastOp;
- (void) test10StripOp;
- (void) test11LengthStrip;
- (void) test12CombinedOp;
- (void) test13CatListAndSingleAttribute;
- (void) test14ReUseOfCat;
- (void) test15CatListAndEmptyAttributes;
- (void) test16NestedOp;
- (void) test17FirstWithOneAttributeOp;
- (void) test18LastWithOneAttributeOp;
- (void) test19LastWithLengthOneListAttributeOp;
- (void) test20RestWithOneAttributeOp;
- (void) test21RestWithLengthOneListAttributeOp;
- (void) test22RepeatedRestOp;
- (void) test23IncomingLists;
- (void) test24FirstWithCatAttribute;
- (void) test25FirstWithListOfMaps;
- (void) test26FirstWithListOfMaps2;
- (void) test27Trim;
- (void) test28Strlen;
- (void) test29Reverse;
@end
