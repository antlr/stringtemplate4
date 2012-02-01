//
//  InstanceScope.h
//  ST4
//
//  Created by Alan Condit on 4/6/11.
//  Copyright 2011 Alan Condit. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "ST.h"

@interface InstanceScope : NSObject {
    
	__strong InstanceScope *parent;	        // template that invoked us
	__strong ST *st;      				    // template we're executing
	NSInteger ret_ip; 				// return address
    __strong AMutableArray *events;
    __strong AMutableArray *childEvalTemplateEvents;
}

+ (id) newInstanceScope:(InstanceScope *)aParent who:(ST *)aWho;
- (id) init:(InstanceScope *)aParent who:(ST *)aWho;
- (void) dealloc;

@property (retain) InstanceScope *parent;
@property (retain) ST *st;
@property (assign) NSInteger ret_ip;
@property (retain) AMutableArray *events;
@property (retain) AMutableArray *childEvalTemplateEvents;

@end
