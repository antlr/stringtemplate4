#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "NSMutableArray.h"
#import "NSMutableDictionary.h"
#import "NSMutableArray.h"
#import "NSMutableDictionary.h"
#import "Assert.h"

@interface TestFunctions_Anon1 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions_Anon2 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions_Anon3 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions_Anon4 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions_Anon5 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions_Anon6 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions_Anon7 : NSObject <NSMutableArray> {
}

- (void) init;
@end

@interface TestFunctions : BaseTest {
}

- (void) testFirst;
- (void) testLength;
- (void) testLengthWithNullValues;
- (void) testFirstOp;
- (void) testTruncOp;
- (void) testRestOp;
- (void) testRestOpEmptyList;
- (void) testReUseOfRestResult;
- (void) testLastOp;
- (void) testStripOp;
- (void) testLengthStrip;
- (void) testCombinedOp;
- (void) testCatListAndSingleAttribute;
- (void) testReUseOfCat;
- (void) testCatListAndEmptyAttributes;
- (void) testNestedOp;
- (void) testFirstWithOneAttributeOp;
- (void) testLastWithOneAttributeOp;
- (void) testLastWithLengthOneListAttributeOp;
- (void) testRestWithOneAttributeOp;
- (void) testRestWithLengthOneListAttributeOp;
- (void) testRepeatedRestOp;
- (void) testIncomingLists;
- (void) testFirstWithCatAttribute;
- (void) testFirstWithListOfMaps;
- (void) testFirstWithListOfMaps2;
- (void) testTrim;
- (void) testStrlen;
- (void) testReverse;
@end
