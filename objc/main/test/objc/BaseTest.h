//
//  BaseTest.h
//  BaseTest
//
//  Created by Alan Condit on 4/3/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "STGroup.h"
#import "Compiler.h"
#import "Misc.h"
#import "Writer.h"
#import "STException.h"
#import "Assert.h"

@interface User : NSObject {
    NSInteger num;
    NSString *name;
    BOOL manager;
    BOOL parkingSpot;
}

@property (assign) NSInteger num;
@property (retain) NSString *name;
@property (assign) BOOL manager;
@property (assign) BOOL parkingSpot;

+ (id) new;
+ (id) newUser:(NSInteger)aNum name:(NSString *)aName;

- (id) init;
- (id) init:(int)aNum name:(NSString *)aName;
- (void)dealloc;
- (BOOL) hasParkingSpot;
@end

@interface HashableUser : User {
}

- (id) init:(int)aNum name:(NSString *)name;
- (int) hash;
- (BOOL) isEqualTo:(NSObject *)obj;
@end

extern NSString *const tmpdir;
extern NSString *const newline;

@interface BaseTest : SenTestCase {
    NSString *randomDir;
}

+ (NSString *) randomDir;
+ (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content;

- (void) setUp;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected;
- (void) checkTokens:(NSString *)template expected:(NSString *)expected delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;

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
