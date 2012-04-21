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
#import <ANTLR/ANTLR.h>
#import <ANTLR/CommonToken.h>
#import "Writer.h"
#import "STGroup.h"
#import "STErrorListener.h"
#import "ConstructionEvent.h"

#ifndef DEBUG_DEALLOC
#define DEBUG_DEALLOC
#endif
/**
 * <@r()>, <@r>...<@end>, and @t.r() ::= "..." defined manually by coder
 */

typedef enum {
    IMPLICIT,
    EMBEDDED,
    EXPLICIT
} RegionTypeEnum;

// RegionTypeEnum RegionTypeValueOf(NSString *text);
// (NSString *) RegionTypeDescription(RegionTypeEnum value);

@class CompiledST;
@class STNoSuchAttributeException;
@class STNoSuchPropertyException;
@class STErrorListener;
@class STGroup;
@class OBJCMethod;
/**
 * Just an alias for ArrayList, but this way I can track whether a
 * list is something ST created or it's an incoming list.
 */

@interface AttributeList : AMutableArray {
}

+ (id) newAttributeList;
+ (id) arrayWithCapacity:(NSInteger)size;

- (id) init;

- (id) initWithCapacity:(NSInteger)size;
- (void) addObject:(id)anObject;
- (void) insertObject:(id)anObject atIndex:(NSInteger)anIdx;
- (NSString *) get:(NSString *)name;
- (NSString *) description;
- (NSString *) description:(NSInteger)i;
- (NSString *) toString;
- (NSString *) toString:(NSInteger)i;

@end

/** Events during template hierarchy construction (not evaluation) */
@interface DebugState : NSObject {

    /** Record who made us? ConstructionEvent creates Exception to grab stack */
    __strong ConstructionEvent *newSTEvent;
    /** Track construction-time add attribute "events"; used for ST user-level debugging */
    __strong LinkedHashMap *addAttrEvents;
}

@property (retain) ConstructionEvent *newSTEvent;
@property (retain) LinkedHashMap *addAttrEvents;

+ (id) newDebugState;
- (id) init;
- (void) dealloc;

- (LinkedHashMap *)setAddAttrEvents;
@end

/**
 * An instance of the StringTemplate. It consists primarily of
 * a reference to its implementation (shared among all instances)
 * and a hash table of attributes.  Because of dynamic scoping,
 * we also need a reference to any enclosing instance. For example,
 * in a deeply nested template for an HTML page body, we could still reference
 * the title attribute defined in the outermost page template.
 * 
 * To use templates, you create one (usually via STGroup) and then inject
 * attributes using add(). To render its attacks, use render().
 */

@interface ST : NSObject {
    
    /**
     * The implementation for this template among all instances of same tmpelate .
     */
    __strong CompiledST *impl;
    
    /**
     * Safe to simultaneously write via add, which is synchronized.  Reading
     * during exec is, however, NOT synchronized.  So, not thread safe to
     * add attributes while it is being evaluated.  Initialized to EMPTY_ATTR
     * to distinguish null from empty.
     */
    __strong AMutableArray *locals;
    
    /**
     * Enclosing instance if I'm embedded within another template.
     * IF-subtemplates are considered embedded as well. We look up
     * dynamically scoped attributes with this ptr.
     */
    __strong ST *enclosingInstance;
    
	/** If Interpreter.trackCreationEvents, track creation, add-attr events
	 *  for each object. Create this object on first use.
	 */
	__strong DebugState *debugState;

    /**
     * Created as instance of which group? We need this to init interpreter
     * via render.  So, we create st and then it needs to know which
     * group created it for sake of polymorphism:
     * 
     * st = skin1.getInstanceOf("searchbox");
     * result = st.render(); // knows skin1 created it
     * 
     * Say we have a group, g1, with template t and import t and u templates from
     * another group, g2.  g1.getInstanceOf("u") finds u in g2 but remembers
     * that g1 created it.  If u includes t, it should create g1.t not g2.t.
     * 
     * g1 = {t(), u()}
     * |
     * v
     * g2 = {t()}
     */
    __strong STGroup *groupThatCreatedThisInstance;
}

+ (void) initialize;

+ (STNoSuchAttributeException *) cachedNoSuchAttrException;
+ (void) setCachedNoSuchAttrException:(id)e;

+ (NSInteger) NO_WRAP;
+ (NSString *)UNKNOWN_NAME;
+ (NSString *) EMPTY_ATTR;
+ (DebugState *)debugState;
//+ (AttributeList *) attributeList;

+ (ST *) newST;
+ (ST *) newSTWithTemplate:(NSString *)template;
+ (ST *) newST:(NSString *)template delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
+ (ST *) newST:(STGroup *)aGroup template:(NSString *)template;
+ (ST *) newSTWithProto:(ST *)proto;
- (id) init:(STGroup *)aGroup template:(NSString *)template;
- (id) initWithProto:(ST *)proto;
- (void)dealloc;
- (ST *) add:(NSString *)name value:(id)value;
- (ST *) addInt:(NSString *)name value:(NSInteger)value;
- (ST *) addAggr:(NSString *)aggrSpec values:(id)values;
- (void) remove:(NSString *)name;
- (void) rawSetAttribute:(NSString *)name value:(id)value;
- (id) getAttribute:(NSString *)name;
- (LinkedHashMap *) getAttributes;
+ (AttributeList *) convertToAttributeList:(id)curvalue;
- (NSString *) getName;
- (NSInteger) write:(Writer *)wr1;
- (NSInteger) write:(Writer *)wr1 locale:(NSLocale *)locale;
- (NSInteger) write:(Writer *)wr1 listener:(id<STErrorListener>)listener;
- (NSInteger) write:(Writer *)wr1 locale:(NSLocale *)locale listener:(id<STErrorListener>)listener;
- (NSInteger) writeFile:(NSString *)outputFile Listener:(id<STErrorListener>)listener;
- (NSInteger) writeFile:(NSString *)outputFile Listener:(id<STErrorListener>)listener Encoding:(NSStringEncoding)encoding;
- (NSInteger) writeFile:(NSString *)outputFile Listener:(id<STErrorListener>)listener Encoding:(NSStringEncoding)encoding LineWidth:(NSInteger)lineWidth;
- (NSInteger) writeFile:(NSString *)outputFile Listener:(id<STErrorListener>)listener Encoding:(NSStringEncoding)encoding LineWidth:(NSInteger)lineWidth Locale:(NSLocale *)locale;
- (NSString *) render;
- (NSString *) renderWithLineWidth:(NSInteger)lineWidth;
- (NSString *) render:(NSLocale *)locale;
- (NSString *) render:(NSLocale *)locale lineWidth:(NSInteger)lineWidth;
- (AMutableArray *)getEvents;
- (AMutableArray *)getEventsWithLineWidth:(NSInteger)lineWidth;
- (AMutableArray *)getEvents:(NSLocale *)locale;
- (AMutableArray *)getEvents:(NSLocale *)locale lineWidth:(NSInteger)lineWidth;
- (NSString *) description;
- (NSString *) toString;
+ (NSString *) format:(NSString *)template attributes:(id)attributes;
+ (NSString *) format:(NSString *)template attributes:(id)attributes lineWidth:(NSInteger)lineWidth;

- (DebugState *)debugState;

- (BOOL) getIsAnonSubtemplate;

// getters and setters

@property (retain) CompiledST *impl;
@property (retain) AMutableArray *locals;
@property (retain) ST *enclosingInstance;
@property (retain) STGroup *groupThatCreatedThisInstance;
@property (retain, getter=debugState, setter = setDebugState:) DebugState *debugState;

@end
