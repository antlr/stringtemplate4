//
//  ST4Tests.h
//  ST4Tests
//
//  Created by Alan Condit on 4/4/11.
//  Copyright 2011 Alan's MachineWorks. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <SenTestingKit/SenTestingKit.h>


@interface ST4Tests : SenTestCase {
    
}

- (void)setUp;
- (void)tearDown;

- (void) testCoordinate;
- (void) testWriterNew;
- (void) testWriterWithCapacity;
@end
