//
//  TestMisc.m
//  ST4
//
//  Created by Alan Condit on 1/29/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//
#import <Cocoa/Cocoa.h>

#import "TestMisc.h"
#import "Coordinate.h"
#import "Writer.h"


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
    STAssertTrue( [[aCoord toString] isEqualToString:@"3:5"], @"expected \"3:5\" got %@", [aCoord toString]);
    NSLog( @"Coordinate =%@", [aCoord toString]);
}

- (void) testWriterNew
{
    Writer *aWriter;
    aWriter = [Writer newWriter];
    NSInteger len = [aWriter length];
    STAssertTrue( (len == 0), @"Expected len = 0, got len = %d", len);
    [aWriter appendString:@"Test String"];
    STAssertTrue( [@"Test String" isEqualTo:[aWriter toString]], @"Expected \"Test String\" but got \"%@\".", [aWriter toString]);
    //STAssertTrue( [aWriter compare:@"Test String"], @"Expected \"Test String\" but got \"%@\".", aWriter);
}

- (void) testWriterWithCapacity
{
    Writer *aWriter;
    aWriter = [Writer newWriterWithCapacity:16];
    NSInteger len = [aWriter count];
    STAssertTrue( (len == 0), @"Expected len = 0, got len = %d", len);
    STAssertTrue( (len == 0), @"Expected len = 0, got len = %d", len);
    [aWriter appendString:@"Test String"];
    STAssertTrue( [@"Test String" isEqualTo:[aWriter toString]], @"Expected \"Test String\" but got \"%@\".", [aWriter toString]);
    //STAssertTrue( [aWriter compare:@"Test String"], @"Expected \"Test String\" but got \"%@\".", aWriter);
}

@end
