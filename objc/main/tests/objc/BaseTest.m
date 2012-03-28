//
//  BaseTest.m
//  BaseTest
//
//  Created by Alan Condit on 4/3/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//

#import "BaseTest.h"
#import "STGroup.h"
#import "STLexer.h"
#import "Writer.h"
#import "ErrorManager.h"
#import "STGroup.h"
#import "Compiler.h"

NSString *const tmpdir = @"~/Documents/tmp";
NSString *const newline = @"\n"/* Misc.newline */;

@implementation User

@synthesize num;
@synthesize name;
@synthesize manager;
@synthesize parkingSpot;

+ (id) new
{
    return [[User alloc] init];
}

+ (id) newUser:(NSInteger)aNum name:(NSString *)aName
{
    return [[User alloc] init:aNum name:aName];
}

- (id) init
{
    if ( (self=[super init]) != nil ) {
        num = 0;
        name = @"";
        manager = YES;
        parkingSpot = YES;
    }
    return self;
}

- (id) init:(int)aNum name:(NSString *)aName
{
    if ( (self=[super init]) != nil ) {
        num = aNum;
        name = aName;
        manager = YES;
        parkingSpot = YES;
    }
    return self;
}

- (BOOL) hasParkingSpot
{
    return YES;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in User" );
#endif
    if ( name ) [name release];
    [super dealloc];
}

@end

@implementation HashableUser

- (id) init:(int)aNum name:(NSString *)aName
{
    if ( (self=[super init:aNum name:aName]) != nil ) {
    }
    return self;
}

- (int) hash
{
    return num;
}

- (BOOL) isEqualTo:(NSObject *)obj
{
    if ([obj isKindOfClass:[HashableUser class]]) {
        HashableUser *hu = (HashableUser *)obj;
        return num == hu.num && [name isEqualTo:hu.name];
    }
    return NO;
}

@end

@implementation BaseTest

@synthesize randomDir;

- (void)setUp
{
//     [super setUp];
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    
//    [super tearDown];
}

- (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content
{
    NSString *path;
    NSFileHandle *fh;
    NSError *error;
    NSString *str;
    NSArray *cs;
    BOOL isDirectory;
    BOOL dirExists;
    
    @try {
        NSFileManager *nfm = [[NSFileManager alloc] init];
        path = [dir stringByExpandingTildeInPath];
        dirExists = [nfm fileExistsAtPath:path isDirectory:&isDirectory];
        if ( !dirExists ) {
            [nfm createDirectoryAtPath:(NSString *)path withIntermediateDirectories:YES attributes:nil error:&error];
        }
        path = [path stringByAppendingPathComponent:fileName];
        // NSFileHandle *f = [[File alloc] init:dir arg1:fileName];
        fh = [NSFileHandle fileHandleForWritingAtPath:path];
        if (fh == nil) {
            NSData *data = [NSData dataWithContentsOfFile:content];
            if ([nfm createFileAtPath:path contents:data attributes:nil]) {
                fh = [NSFileHandle fileHandleForWritingAtPath:path];
            }
        }
#ifdef DONTUSENOMO
        if (![[f parentFile] exists])
            [[f parentFile] mkdirs];
#endif
        FileWriter *fw = [[FileWriter newWriterWithFH:fh] retain];
        BufferedWriter *bw = [[BufferedWriter newWriter:fw] retain];
        [bw writeStr:content];
//        [fw writeStr:content];
        [bw close];
        [fw close];
    }
    @catch (IOException *ioe) {
        //[System.err println:@"can't write file"];
        //NSLog( @"can't write file" );
        //[ioe printStackTrace:System.err];
        cs = [ioe callStackSymbols];
        for (int i=0; i < [cs count]; i++ ) {
            str = [cs objectAtIndex:i];
            //NSLog( @"CallStack = %@\n", str );
        }
    }
}

- (void) checkTokens:(NSString *)template expected:(NSString *)expected
{
    [self checkTokens:template expected:expected delimiterStartChar:'<' delimiterStopChar:'>'];
}

- (void) checkTokens:(NSString *)template expected:(NSString *)expected delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar
{
    STLexer *lexer = [[STLexer newSTLexer:STGroup.DEFAULT_ERR_MGR input:[ANTLRStringStream newANTLRStringStream:template] templateToken:nil delimiterStartChar:delimiterStartChar delimiterStopChar:delimiterStopChar] retain];
    CommonTokenStream *tokens = [[CommonTokenStream newCommonTokenStreamWithTokenSource:lexer] retain];
    NSMutableString *buf = [[NSMutableString stringWithCapacity:30] retain];
    [buf appendString:@"["];
    int i = 1;
    CommonToken *t = [tokens LT:i];
    while (t.type != TokenTypeEOF) {
        if (i > 1)
            [buf appendString:@", "];
        [buf appendString:[t description]];
        i++;
        t = [tokens LT:i];
    }
    
    [buf appendString:@"]"];
    NSString *result = [NSString stringWithString:buf];
    STAssertTrue( [expected isEqualToString:result], @"Expected %@, but got \"%@\"", expected, result );
}

- (NSString *) getRandomDir
{
    BOOL isDir;
    NSError *error;
    NSFileManager *defaultManager;
    randomDir = [NSString stringWithFormat:@"%@/tmpdir%d", [tmpdir stringByExpandingTildeInPath], (int)arc4random()];
    defaultManager = [NSFileManager defaultManager];
    if (![defaultManager fileExistsAtPath:randomDir isDirectory:&isDir]) {
        if ([defaultManager createDirectoryAtPath:randomDir withIntermediateDirectories:YES attributes:nil error:&error] ) {
            //NSLog( @"Created \"%@\"", randomDir );
            return randomDir;
        }
    }
    return nil;
}

@end

@implementation Strings

+ (id) newStringsWithArray:(AMutableArray *)anArray
{
    return [[[Strings alloc] initWithArray:(AMutableArray *)anArray] retain];
}

- (id) initWithArray:(AMutableArray *)anArray
{
    self=[super init];
    if ( self != nil ) {
        if ( [anArray isKindOfClass:[NSArray class]] ) {
            thisArray = [anArray retain];
        }
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Strings" );
#endif
    if ( thisArray ) [thisArray release];
    [super dealloc];
}

- (void) addObject:(id)anObject
{
    [thisArray addObject:anObject];
}

- (id) objectAtIndex:(NSInteger)idx
{
    return [thisArray objectAtIndex:idx];
}

- (NSString *)description
{
    NSInteger i;
    NSMutableString *str = nil;

    if (thisArray != nil) {
        NSInteger count;
        count = [thisArray count];
        id obj;
        str = [NSMutableString stringWithString:@"["];
        for (i=0; i < count; i++ ) {
            obj = [thisArray objectAtIndex:i];
            if ( obj != nil ) {
                if ([obj isKindOfClass:[NSString class]]) {
                    [str appendString:obj];
                    //NSLog( @"String %d = %@\n", i, obj);
                } else {
                    [str appendString:[obj description]];
                }
            }
            else {
                [str appendString:@"obj=<nil>"];
            }
            if ( i < count-1) {
                [str appendString:@", "];
            }
        }
        [str appendString:@"]"];
    }
    return [NSString stringWithString:str];
}

- (NSString *) toString
{
    return [self description];
}

@synthesize thisArray;
@end

