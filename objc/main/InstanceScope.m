//
//  InstanceScope.m
//  ST4
//
//  Created by Alan Condit on 4/6/11.
//  Copyright 2011 Alan Condit. All rights reserved.
//

#import <ANTLR/ANTLR.h>
#import "InstanceScope.h"

@class InstanceScope;

@implementation InstanceScope

@synthesize parent;
@synthesize st;
@synthesize ret_ip;
@synthesize events;
@synthesize childEvalTemplateEvents;

/* Includes the EvalTemplateEvent for this template.  This
 *  is a subset of Interpreter.events field. The final
 *  EvalTemplateEvent is stored in 3 places:
 *
 *  	1. In enclosing instance's childTemplateEvents
 *  	2. In this event list
 *  	3. In the overall event list
 *
 *  The root ST has the final EvalTemplateEvent in its list.
 *
 *  All events get added to the enclosingInstance's event list.
 */

+ (id) newInstanceScope:(InstanceScope *)aParent who:(ST *)aWho
{
    return [[InstanceScope alloc] init:aParent who:aWho];
}

- (id) init:(InstanceScope *)aParent who:(ST *)aWho
{
    self = [super init];
    if ( self != nil ) {
        parent = [aParent retain];
        st = [aWho retain];
        events = [[AMutableArray arrayWithCapacity:5] retain];
        childEvalTemplateEvents = [[AMutableArray arrayWithCapacity:5] retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in InstanceScope" );
#endif
    if ( st ) [st release];
    if ( parent ) [parent release];
    if ( childEvalTemplateEvents ) [childEvalTemplateEvents release];
    if ( events ) [events release];
    [super dealloc];
}

#ifdef DONTUSEYET
- (AMutableArray *) events = new ArrayList<InterpEvent>();

/** All templates evaluated and embedded in this ST. Used
 *  for tree view in STViz.
 */
- (AMutableArray *) childEvalTemplateEvents = new ArrayList<EvalTemplateEvent>();
#endif

@end
