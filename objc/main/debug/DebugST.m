/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#import "STErrorListener.h"
#import "DebugST.h"
#import "Writer.h"
#import "InterpEvent.h"
#import "AddAttributeEvent.h"
#import "CompiledST.h"
#import <ANTLR/AMutableArray.h>
#import "Interpreter.h"

@implementation State

@synthesize interpEvents;

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        interpEvents = [AMutableArray arrayWithCapacity:5];
    }
    return self;
}

- (void) dealloc
{
    [interpEvents release];
    [super dealloc];
}

@end

@implementation DebugST

@synthesize newSTEvent;
@synthesize addAttrEvents;
@synthesize events;

+ (id) newDebugSTWithProto:(ST *)proto
{
    return [[DebugST alloc] initWithProto:proto];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        newSTEvent = [[ConstructionEvent alloc] init];
        addAttrEvents = [NSMutableDictionary dictionaryWithCapacity:25];
    }
    return self;
}

- (id) initWithProto:(ST *)proto
{
    self=[super initWithProto:proto];
    if ( self != nil ) {
        newSTEvent = [[ConstructionEvent alloc] init];
        addAttrEvents = [NSMutableDictionary dictionaryWithCapacity:25];
    }
    return self;
}

- (ST *) add:(NSString *)name value:(id)value
{
    if (STGroup.debug) {
        [addAttrEvents map:name value:[[AddAttributeEvent alloc] init:name value:value]];
    }
    return [super add:name value:value];
}

- (AMutableArray *) inspect
{
    return [self inspectLocale:[NSLocale currentLocale]];
}

- (AMutableArray *) inspect:(NSInteger)lineWidth
{
    return [self inspect:impl.nativeGroup.errMgr locale:[NSLocale currentLocale] lineWidth:lineWidth];
}

- (AMutableArray *) inspectLocale:(NSLocale *)locale
{
    return [self inspect:impl.nativeGroup.errMgr locale:locale lineWidth:[ST NO_WRAP]];
}

- (AMutableArray *) inspect:(ErrorManager *)anErrMgr locale:(NSLocale *)locale lineWidth:(NSInteger)lineWidth
{
    ErrorBuffer *errors = [[ErrorBuffer alloc] init];
    [impl.nativeGroup setListener:errors];
    //    StringWriter *wr = [[StringWriter alloc] init];
    Writer *writer = [AutoIndentWriter newWriter];
    [writer setLineWidth:lineWidth];
    Interpreter *interp = [Interpreter newInterpreter:groupThatCreatedThisInstance locale:locale debug:YES];
    [interp exec:writer who:self];
//    [[STViz alloc] init:errMgr root:self output:[wr description] interp:interp trace:[interp executionTrace] errors:errors.errors];
    return [interp getEvents];
}

- (AMutableArray *) getEvents
{
    return [self getEventsLocale:[NSLocale currentLocale]];
}

- (AMutableArray *) getEvents:(NSInteger)lineWidth
{
    return [self getEventsLocale:[NSLocale currentLocale] lineWidth:lineWidth];
}

- (AMutableArray *) getEventsLocale:(NSLocale *)locale {
    return [self getEventsLocale:locale lineWidth:[ST NO_WRAP]];
}

- (AMutableArray *) getEventsLocale:(NSLocale *)locale lineWidth:(NSInteger)lineWidth
{
    Writer *aWriter = [AutoIndentWriter newWriter];
    [aWriter setLineWidth:lineWidth];
    Interpreter *interp = [Interpreter newInterpreter:groupThatCreatedThisInstance locale:locale debug:YES];
    [interp exec:aWriter who:self];
    return [interp getEvents];
}

- (void) dealloc
{
    [newSTEvent release];
    [addAttrEvents release];
    [super dealloc];
}

@end
