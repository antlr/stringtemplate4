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
    STAssertTrue( [[aCoord toString] isEqualToString:@"3:5"], @"expected \"3:5\" got %@", [aCoord description]);
    NSLog( @"Coordinate =%@", [aCoord description]);
}

- (void) testWriterNew
{
    Writer *aWriter;
    aWriter = [Writer newWriter];
    NSInteger len = [aWriter length];
    STAssertTrue( (len == 0), @"Expected len = 0, got len = %d", len);
    [aWriter appendString:@"Test String"];
    STAssertTrue( [@"Test String" isEqualTo:[aWriter description]], @"Expected \"Test String\" but got \"%@\".", [aWriter description]);
    //STAssertTrue( [aWriter compare:@"Test String"], @"Expected \"Test String\" but got \"%@\".", aWriter);
}

- (void) testWriterWithCapacity
{
    Writer *aWriter;
    aWriter = [Writer newWriter];
    NSInteger len = [aWriter count];
    STAssertTrue( (len == 0), @"Expected len = 0, got len = %d", len);
    STAssertTrue( (len == 0), @"Expected len = 0, got len = %d", len);
    [aWriter appendString:@"Test String"];
    STAssertTrue( [@"Test String" isEqualTo:[aWriter description]], @"Expected \"Test String\" but got \"%@\".", [aWriter description]);
    //STAssertTrue( [aWriter compare:@"Test String"], @"Expected \"Test String\" but got \"%@\".", aWriter);
}

@end
