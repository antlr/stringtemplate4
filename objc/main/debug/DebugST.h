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
#import "ST.h"
#import "AttributeRenderer.h"
#import "AutoIndentWriter.h"
#import "DateRenderer.h"
#import "Interpreter.h"
#import "ModelAdaptor.h"
#import "NoIndentWriter.h"
#import "NumberRenderer.h"
#import "STErrorListener.h"
#import "STGroup.h"
#import "STGroupDir.h"
#import "STGroupFile.h"
#import "StringRenderer.h"
#import "Writer.h"
#import "ErrorBuffer.h"
#import <Cocoa/Cocoa.h>
#import "ErrorManager.h"
#import "StringWriter.h"
#import "ConstructionEvent.h"

@interface State : NSObject {
    
    /**
     * Track all events that occur during rendering.
     */
    AMutableArray *interpEvents;
}

@property (retain) AMutableArray *interpEvents;

- (id) init;
@end

/**
 * To avoid polluting ST instances with debug info when not debugging.
 * Setting debug mode in STGroup makes it create these instead of STs.
 */

@interface DebugST : ST {
    
    /**
     * Record who made us? ConstructionEvent creates Exception to grab stack
     */
    ConstructionEvent *newSTEvent;
    
    /**
     * Track construction-time add attribute "events"; used for ST user-level debugging
     */
    NSMutableDictionary *addAttrEvents;
    AMutableArray *events;
}

@property(retain) ConstructionEvent *newSTEvent;
@property(retain) NSMutableDictionary *addAttrEvents;
@property(retain) AMutableArray *events;

+ (id) newDebugSTWithProto:(ST *)proto;
- (id) init;
- (id) initWithProto:(ST *)proto;
- (ST *) add:(NSString *)name value:(id)value;
- (AMutableArray *) inspect;
- (AMutableArray *) inspect:(NSInteger)lineWidth;
- (AMutableArray *) inspectLocale:(NSLocale *)locale;
- (AMutableArray *) inspect:(ErrorManager *)errMgr locale:(NSLocale *)locale lineWidth:(NSInteger)lineWidth;
- (AMutableArray *) getEvents:(NSInteger)lineWidth;
- (AMutableArray *) getEventsLocale:(NSLocale *)locale;
- (AMutableArray *) getEventsLocale:(NSLocale *)locale lineWidth:(NSInteger)lineWidth;
@end
