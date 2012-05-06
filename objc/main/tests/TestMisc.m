//
//  TestMisc.m
//  ST4
//
//  Created by Alan Condit on 1/29/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//
#import <Foundation/Foundation.h>

#import "TestMisc.h"
#import "Coordinate.h"
#import "Writer.h"


#define STAssertTrue GHAssertTrue

@implementation TestMisc

- (void)setUp
{
    [super setUp];
    
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    
    [super tearDown];
}

- (void) testCoordinate
{
    Coordinate *aCoord;
    aCoord = [Coordinate newCoordinate:3 b:5];
    NSString *expected = @"3:5";
    NSString *result = [aCoord description];
    [self assertEquals:expected result:result];
    NSLog( @"Coordinate =%@", [aCoord description]);
}

- (void) testWriterNew
{
    Writer *aWriter;
    aWriter = [Writer newWriter];
    NSInteger len = [aWriter length];
    NSString *expected = @"len = 0";
    NSString *result = [NSString stringWithFormat:@"len = %d", len];
    [self assertEquals:expected result:result];
    [aWriter appendString:@"Test String"];
    expected = @"Test String";
    result = [aWriter description];
    [self assertEquals:expected result:result];
    //STAssertTrue( [aWriter compare:@"Test String"], @"Expected \"Test String\" but got \"%@\".", aWriter);
}

- (void) testWriterWithCapacity
{
    Writer *aWriter;
    aWriter = [Writer newWriter];
    NSInteger len = [aWriter count];
    NSString *expected = @"len = 0";
    NSString *result = [NSString stringWithFormat:@"len = %d", len];
    [self assertEquals:expected result:result];
    [aWriter appendString:@"Test String"];
    expected = @"Test String";
    result = [aWriter description];
    [self assertEquals:expected result:result];
    //STAssertTrue( [aWriter compare:@"Test String"], @"Expected \"Test String\" but got \"%@\".", aWriter);
}

@end
