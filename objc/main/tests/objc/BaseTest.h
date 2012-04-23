//
//  BaseTest.h
//  BaseTest
//
//  Created by Alan Condit on 4/3/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
//#import <SenTestingKit/SenTestCase.h>
#import <GHUnit/GHTestCase.h>
#import "STGroup.h"
#import "Compiler.h"
#import "Misc.h"
#import "Writer.h"
#import "STException.h"
#import "Assert.h"

#define STAssertTrue GHAssertTrue
#define SenTestCase GHTestCase

extern NSString *const tmpdir;
extern NSString *const newline;

@interface User : NSObject {
    ACNumber *num;
    NSString *name;
    BOOL manager;
    BOOL parkingSpot;
}

@property (retain, getter=getNum, setter=setNum:) ACNumber *num;
@property (retain) NSString *name;
@property (assign) BOOL manager;
@property (assign) BOOL parkingSpot;

+ (id) new;
+ (id) newUser:(NSInteger)aNum name:(NSString *)aName;

- (id) init;
- (id) init:(NSInteger)aNum name:(NSString *)aName;
- (void)dealloc;
- (ACNumber *)getNum;
- (void)setNum:(ACNumber *)aNum;
- (BOOL) hasParkingSpot;
@end

@interface HashableUser : User {
}

- (id) init:(NSInteger)aNum name:(NSString *)name;
- (NSInteger) hash;
- (BOOL) isEqualTo:(NSObject *)obj;
@end

extern NSString *const tmpdir;
extern NSString *const newline;

@interface BaseTest : SenTestCase {
    NSString *randomDir;
}

- (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content;

- (void) setUp;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
- (NSString *)getRandomDir;

@property(retain) NSString *randomDir;

@end

@interface Strings : NSObject {
    AMutableArray *thisArray;
}

+ (id) newStringsWithArray:(AMutableArray *)anArray;
- (id) initWithArray:(AMutableArray *)anArray;

- (void)dealloc;
- (void) addObject:(id)anObject;
- (id) objectAtIndex:(NSInteger)idx;
- (NSString *)description;
- (NSString *)toString;

@property (retain) AMutableArray *thisArray;
@end
