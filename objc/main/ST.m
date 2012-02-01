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
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <ANTLR/RuntimeException.h>
#import "Interpreter.h"
#import "ErrorType.h"
#import "FormalArgument.h"
#import "ErrorManager.h"
#import "STException.h"
#import "STWriter.h"
#import "Writer.h"
#import "StringWriter.h"
#import "AutoIndentWriter.h"
#import "AttributeRenderer.h"
#import "ModelAdaptor.h"
#import "STGroup.h"
#import "ST.h"
#import "CompiledST.h"

RegionTypeEnum RegionTypeValueOf(NSString *text)
{
    if (text) {
        if ([text isEqualToString:@"IMPLICIT"])
            return IMPLICIT;
        else if ([text isEqualToString:@"EMBEDDED"])
            return EMBEDDED;
        else if ([text isEqualToString:@"EXPLICIT"])
            return EXPLICIT;
    }
    return -1;
}

NSString *RegionTypeDescription(RegionTypeEnum value)
{
    switch (value) {
        case IMPLICIT:
            return @"IMPLICIT";
        case EMBEDDED:
            return @"EMBEDDED";
        case EXPLICIT:
            return @"EXPLICIT";
    }
    return nil;
}

@implementation AttributeList

+ (id) newAttributeList
{
    return [[[AttributeList alloc] init] retain];
}

+ (id) arrayWithCapacity:(NSInteger)size
{
    return [[[AttributeList alloc] initWithCapacity:size] retain];
}

- (id) init
{
    if ( (self=[super initWithCapacity:16]) != nil ) {
    }
    return self;
}

- (id) initWithCapacity:(NSInteger)len
{
    if ( (self=[super initWithCapacity:len]) != nil ) {
    }
    return self;
}

- (void) addObject:(id)anObject
{
    //    if (anObject == nil) anObject = [NSNull null];
    [super addObject:anObject];
}

- (void) insertObject:(id)anObject atIndex:(NSInteger)anIdx
{
    //    if (anObject == nil) anObject = [NSNull null];
    if (anIdx < count)
        [super insertObject:anObject atIndex:anIdx];
    else
        [self addObject:anObject];
}

- (NSString *) get:(NSString *)name
{
#pragma mark error fix this (what is it supposed to do)
    return name;
}

- (NSString *) description
{
    NSInteger i;
    NSMutableString *buf = [NSMutableString stringWithCapacity:30];
    for (i = 0; i < count; i++) {
        [buf appendString:[self objectAtIndex:i]];
    }
    return buf;
}

- (NSString *) toString
{
    return [self description];
}

- (NSString *) description:(NSInteger)idx
{
    return [NSString stringWithString:[self objectAtIndex:idx]];
}

- (NSString *) toString:(NSInteger)idx
{
    return [self description:idx];
}

@end

@implementation DebugState

@synthesize newSTEvent;
@synthesize addAttrEvents;

+ (id) newDebugState
{
    return [[DebugState alloc] init];
}

/** Record who made us? ConstructionEvent creates Exception to grab stack */
+ (ConstructionEvent *)newSTEvent
{
    return [ConstructionEvent newEvent];
}

- (id) init
{
    self = [super init];
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STGroup_Anon1" );
#endif
    if ( newSTEvent ) [newSTEvent release];
    if ( addAttrEvents ) [addAttrEvents release];
    [super dealloc];
}

/** Track construction-time add attribute "events"; used for ST user-level debugging */
- (AMutableDictionary *)addAttrEvents
{
    addAttrEvents = [AMutableDictionary dictionaryWithCapacity:25];
    return addAttrEvents;
}

@end

@implementation ST

/**
 * Cache exception since this could happen a lot if people use "missing"
 * to mean boolean false.
 */
static STNoSuchAttributeException *cachedNoSuchAttrException;

static NSInteger NO_WRAP = 80;
NSString * const VERSION = @"@version@";
static NSString *UNKNOWN_NAME = @"anonymous";
static NSString *EMPTY_ATTR = @"";
static DebugState *st_debugState = nil;

@synthesize impl;
@synthesize locals;
@synthesize enclosingInstance;
@synthesize groupThatCreatedThisInstance;
//@synthesize debugState;

+ (DebugState *) debugState
{
    if ( st_debugState == nil )
        st_debugState = [DebugState newDebugState];
    return st_debugState;
}

+ (void) initialize
{
    cachedNoSuchAttrException = [STNoSuchAttributeException newException:nil];
    EMPTY_ATTR = @"";
}

+ (STNoSuchAttributeException *) cachedNoSuchAttrException;
{
    if ( cachedNoSuchAttrException == nil )
        cachedNoSuchAttrException = [STNoSuchAttributeException newException:@"NSAttr"];
    return cachedNoSuchAttrException;
}

+ (void) setCachedNoSuchAttrException:(id)e
{
    cachedNoSuchAttrException = e;
}

+ (RegionTypeEnum) IMPLICIT
{
    return IMPLICIT;
}

+ (RegionTypeEnum) EMBEDDED
{
    return EMBEDDED;
}

+ (RegionTypeEnum) EXPLICIT
{
    return EXPLICIT;
}

+ (NSInteger)NO_WRAP
{
    return NO_WRAP;
}

+ (NSString *)UNKNOWN_NAME
{
    return UNKNOWN_NAME;
}

+ (NSString *) EMPTY_ATTR
{
    return EMPTY_ATTR;
}

- (DebugState *)debugState
{
    return debugState;
}

- (void)setDebugState:(DebugState *)aState
{
    debugState = aState;
}

/*
 + (AttributeList *) attributeList
{
    return attributeList;
}
*/

/** Used by group creation routine, not by users */
+ (id) newST
{
    return [[ST alloc] init];
}

+ (id) newSTWithTemplate:(NSString *)aTemplate
{
    return [[ST alloc] initWithTemplate:(NSString *)aTemplate];
}

+ (id) newST:(NSString *)aTemplate delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar
{
    return [[ST alloc] init:aTemplate delimiterStartChar:delimiterStartChar delimiterStopChar:delimiterStopChar];
}

+ (id) newST:(STGroup *)aGroup template:(NSString *)aTemplate
{
    return [[ST alloc] init:aGroup template:aTemplate];
}

/** Clone a prototype template.
 *  Copy all fields minus debugState; don't call this(), which creates
 *  ctor event
 */
+ (id) newSTWithProto:(ST *)aProto
{
    return [[ST alloc] initWithProto:(ST *)aProto];
}

/**
 * Used by group creation routine, not by users
 */
- (id) init
{
    self=[super init];
    if ( self != nil ) {
        if (EMPTY_ATTR == nil) {
            EMPTY_ATTR = @"";
            [EMPTY_ATTR retain];
        }
        if ( st_debugState==nil ) st_debugState = [[DebugState newDebugState] retain];
        if ( STGroup.trackCreationEvents ) {
            debugState = st_debugState;
            st_debugState.newSTEvent = [[ConstructionEvent newEvent] retain];
        }
        groupThatCreatedThisInstance = STGroup.defaultGroup;
        if ( groupThatCreatedThisInstance ) [groupThatCreatedThisInstance retain];
        impl = [groupThatCreatedThisInstance compile:[groupThatCreatedThisInstance getFileName] name:nil args:nil template:nil templateToken:nil];
        impl.hasFormalArgs = NO;
        impl.name = UNKNOWN_NAME;
        [impl defineImplicitlyDefinedTemplates:groupThatCreatedThisInstance];
        [impl retain];
    }
    return self;
}

/**
 * Used to make templates inline in code for simple things like SQL or log records.
 * No formal args are set and there is no enclosing instance.
 */
- (id) initWithTemplate:(NSString *)aTemplate
{
    self=[super init];
    if ( self != nil ) {
        if (EMPTY_ATTR == nil) {
            EMPTY_ATTR = @"";
            [EMPTY_ATTR retain];
        }
        groupThatCreatedThisInstance = STGroup.defaultGroup;
        if ( groupThatCreatedThisInstance ) [groupThatCreatedThisInstance retain];
        impl = [groupThatCreatedThisInstance compile:[groupThatCreatedThisInstance getFileName] name:nil args:nil template:aTemplate templateToken:nil];
        impl.hasFormalArgs = NO;
        impl.name = UNKNOWN_NAME;
        [impl defineImplicitlyDefinedTemplates:groupThatCreatedThisInstance];
        [impl retain];
    }
    return self;
}

/**
 * Create ST using non-default delimiters; each one of these will live
 * in it's own group since you're overriding a default; don't want to
 * alter STGroup.defaultGroup.
 */
- (id) init:(NSString *)template delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    self=[super init];
    if ( self != nil ) {
        if (EMPTY_ATTR == nil) {
            EMPTY_ATTR = @"";
            [EMPTY_ATTR retain];
        }
        groupThatCreatedThisInstance = [[STGroup alloc] init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
        if ( groupThatCreatedThisInstance ) [groupThatCreatedThisInstance retain];
        impl = [groupThatCreatedThisInstance compile:[groupThatCreatedThisInstance getFileName] name:nil args:nil template:template templateToken:nil];
        impl.hasFormalArgs = NO;
        impl.name = UNKNOWN_NAME;
        [impl defineImplicitlyDefinedTemplates:groupThatCreatedThisInstance];
        [impl retain];
    }
    return self;
}

- (id) init:(STGroup *)group template:(NSString *)template
{
    self=[super init];
    if ( self != nil ) {
        if (EMPTY_ATTR == nil) {
            EMPTY_ATTR = @"";
            [EMPTY_ATTR retain];
        }
        groupThatCreatedThisInstance = group;
        if ( groupThatCreatedThisInstance ) [groupThatCreatedThisInstance retain];
        impl = [groupThatCreatedThisInstance compile:[group getFileName] name:nil args:nil template:template templateToken:nil];
        impl.hasFormalArgs = NO;
        impl.name = UNKNOWN_NAME;
        [impl defineImplicitlyDefinedTemplates:groupThatCreatedThisInstance];
        [impl retain];
    }
    return self;
}

/*
 * Clone a prototype template for application in MAP operations;
 *  copy all fields minus debugState; don't call this(), which creates ctor event
 */
- (id) initWithProto:(ST *)proto
{
    self=[super init];
    if ( self != nil ) {
        if (EMPTY_ATTR == nil) {
            EMPTY_ATTR = @"";
            [EMPTY_ATTR retain];
        }
        impl = proto.impl;
        [impl retain];
        enclosingInstance = proto.enclosingInstance;
        if ( enclosingInstance ) [enclosingInstance retain];
        if (proto.locals != nil) {
            locals = [[AMutableArray arrayWithArray:proto.locals] retain];
        }
        groupThatCreatedThisInstance = proto.groupThatCreatedThisInstance;
        if ( groupThatCreatedThisInstance ) [groupThatCreatedThisInstance retain];
    }
    return self;
}

- (void)dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in ST" );
#endif
    if ( impl ) [impl release];
    if ( locals ) [locals release];
    if ( enclosingInstance ) [enclosingInstance release];
    if ( debugState ) [debugState release];
    if ( groupThatCreatedThisInstance ) [groupThatCreatedThisInstance release];
    [super dealloc];
}

/**
 * Inject an attribute (name/value pair). If there is already an
 * attribute with that name, this method turns the attribute into an
 * AttributeList with both the previous and the new attribute as elements.
 * This method will never alter a List that you inject.  If you send
 * in a List and then inject a single value element, add() copies
 * original list and adds the new value.
 * 
 * Return self so we can chain.  t.add("x", 1).add("y", "hi");
 */
- (ST *) add:(NSString *)aName value:(id)value
{
    NSRange aRange;
    if ( aName == nil )
        return self;
    aRange = [aName rangeOfString:@"."];
    if (aRange.location != NSNotFound) {
        @throw [IllegalArgumentException newException:@"cannot have '.' in attribute names"];
    }
#pragma mark fix this
#ifdef DONTUSEYET
    if ( STGroup.trackCreationEvents ) {
        if ( debugState==nil ) debugState = [DebugState newDebugState];
        [[debugState addAttrEvents] map:aName event:[AddAttributeEvent newEvent:value forKey:aName]];
    }
#endif
    FormalArgument *arg = nil;
    if (impl.hasFormalArgs) {
        if (impl.formalArguments != nil)
            arg = [impl.formalArguments objectForKey:aName];
        if (arg == nil) {
            @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"no such attribute: %@", aName]];
        }
    }
    else {
        if (impl.formalArguments != nil) {
            arg = [impl.formalArguments objectForKey:aName];
        }
        if (arg == nil) {
            arg = [FormalArgument newFormalArgument:aName];
            [impl addArg:arg];
            if (locals == nil)
                locals = [[AMutableArray arrayWithCapacity:5] retain];
            [locals insertObject:EMPTY_ATTR atIndex:arg.index];
        }
    }
    id curvalue;
    if ([value isMemberOfClass:[ST class]])
        ((ST *)value).enclosingInstance = self;
    curvalue = [locals objectAtIndex:arg.index];
    if (curvalue == EMPTY_ATTR) {
        [locals replaceObjectAtIndex:arg.index withObject:value];
        return self;
    }
    AttributeList *multi = [ST convertToAttributeList:curvalue];
    [locals replaceObjectAtIndex:arg.index withObject:multi];
#ifdef DONTUSENOMO
    if ( [value isKindOfClass:[NSDictionary class]] ) {
        [multi addObjectsFromArray:(NSArray *)[value allValues]];
    }
    else
#endif
    if ( [value isKindOfClass:[NSArray class]] ) {
        [multi addObjectsFromArray:(AMutableArray *)value];
    }
    else {
        [multi addObject:value];
    }
    return self;
}

- (ST *) addInt:(NSString *)aName value:(NSInteger)value
{
    return [self add:aName value:[NSString stringWithFormat:@"%d", value]];
}

#pragma mark fix this
#ifdef DONTUSEYET
    /** Split "aggrName.{propName1,propName2}" into list [propName1, propName2]
     *  and the aggrName. Spaces are allowed around ','.
     */
- (ST *) addAggr:(NSString *)aggrSpec values:(id)values
{
        NSRange dot = [aggrSpec rangeOfString:@".{"];
        if ( values == nil || values.length == 0 ) {
            @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"missing values for aggregate attribute format: %@", aggrSpec]];
        }
        int finalCurly = [aggrSpec indexOfCharacter:'}'];
        if ( dot.length < 0 || finalCurly < 0 ) {
            @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"invalid aggregate attribute format: %@", aggrSpec]];
        }
        NSString *aggrName = [aggrSpec subStringWithRange:(NSMakeRange(0, dot.location)];
        NSString *propString = [aggrSpec subStringWithRange:NSMakeRange(dot.location+2, ([aggrSpec length]-(dot.location+3)))];
//      propString = propString.trim();
        NSString[] *propNames = propString.split("\\ *,\\ *");
        if ( propNames==nil || [propNames length]==0 ) {
            @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"invalid aggregate attribute format: %@", aggrSpec]];
        }
        if ( values.length != propNames.length ) {
            @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"number of properties and values mismatch for aggregate attribute format: %@", aggrSpec]];
        }
        int i=0;
        Aggregate *aggr = [Aggregate newAggregate];
        NSString *p;
        for (NSString *p in propNames) {
            id v = values[i++];
            [aggr.properties setObject:v forKey:p];
        }

        [ST add:aggrName value:aggr]; // now add as usual
        return self;
}
#endif

/**
 * Remove an attribute value entirely (can't remove attribute definitions).
 */
- (void) remove:(NSString *)name
{
    if ( impl.formalArguments == nil ) {
        if ( impl.hasFormalArgs ) {
            @throw [IllegalArgumentException  newException:[NSString stringWithFormat:@"no such attribute: %@", name]];
        }
        return;
    }
    FormalArgument *arg = [impl.formalArguments objectForKey:name];
    if ( arg == nil ) {
        @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"no such attribute: %@", name]];
    }
    if ( arg.index < [locals count] ) {
        [locals replaceObjectAtIndex:arg.index withObject:EMPTY_ATTR];
    } else if ( arg.index == [locals count] ) {
        [locals insertObject:EMPTY_ATTR atIndex:arg.index];
    }
}

/**
 * Set this.locals attr value when you only know the name, not the index.
 * This is ultimately invoked by calling ST.add() from outside so toss
 * an exception to notify them.
 */
- (void) rawSetAttribute:(NSString *)name value:(id)value
{
    if ( impl.formalArguments == nil ) {
        @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"no such attribute: %@", name]];
    }
    FormalArgument *arg = [impl.formalArguments objectForKey:name];
    if ( arg == nil ) {
        @throw [IllegalArgumentException  newException:[NSString stringWithFormat:@"no such attribute: %@", name]];
    }
    if ( arg.index < [locals count] ) {
        [locals replaceObjectAtIndex:arg.index withObject:value];
    } else if ( arg.index == [locals count] ) {
        [locals insertObject:value atIndex:arg.index];
    } else {
        [locals addObject:value];
    }
}

/** Find an attr in this template only. */
- (id) getAttribute:(NSString *)name
{
    ST *p = self;
    FormalArgument *localArg = nil;
    if ( p.impl.formalArguments != nil )
        localArg = [p.impl.formalArguments objectForKey:name];
    if ( localArg != nil ) {
        id obj = [p.locals objectAtIndex:localArg.index];
        if ( obj == EMPTY_ATTR )
            obj = nil;
        return obj;
    }
    return nil;
}

- (AMutableDictionary *) getAttributes
{
    if ( impl.formalArguments == nil )
        return nil;
    AMutableDictionary *attributes = [AMutableDictionary dictionaryWithCapacity:16];
    ArrayIterator *it = [ArrayIterator newIteratorForDictObj:impl.formalArguments];
    FormalArgument *arg;
//    for (FormalArgument *arg in [impl.formalArguments allValues]) {
    while ( [it hasNext] ) {
        arg = [it nextObject];
        id  obj = [locals objectAtIndex:arg.index];
        if ( obj == ST.EMPTY_ATTR ) {
            continue;
        }
        [attributes setObject:obj forKey:arg.name];
    }
    return attributes;
}

+ (AttributeList *)  convertToAttributeList:(id) curvalue
{
    AttributeList *multi;
    if ( curvalue == nil ) {
        multi = [AttributeList arrayWithCapacity:5]; // make list to hold multiple values
        [multi addObject:nil];                // add previous single-valued attribute
    }
    else if ( [curvalue isKindOfClass:[AttributeList class]]) { // already a list made by ST
        multi = (AttributeList *)curvalue;
    }
    else if ( [curvalue isKindOfClass:[AMutableArray class]]) { // existing attribute is non-ST List
        // must copy to an ST-managed list before adding new attribute
        // (can't alter incoming attributes)
        AMutableArray *listAttr = (AMutableArray *)curvalue;
        multi = [AttributeList arrayWithCapacity:[listAttr count]];
        [multi addObjectsFromArray:listAttr];
    }
    else if ( [curvalue isKindOfClass:[NSArray class]] ) { // copy array to list
        NSArray *a = (NSArray *)curvalue;
        multi = [AttributeList arrayWithCapacity:[a count]];
        [multi addObjectsFromArray:a]; // asList doesn't copy as far as I can tell
    }
    else {
        // curvalue nonlist and we want to add an attribute
        // must convert curvalue existing to list
        multi = [AttributeList arrayWithCapacity:5]; // make list to hold multiple values
        [multi addObject:curvalue];                 // add previous single-valued attribute
    }
    return multi;
}

- (NSString *) getName
{
    return impl.name;
}

- (BOOL) getIsAnonSubtemplate
{
    return impl.isAnonSubtemplate;
}

- (NSInteger) write:(id<STWriter>)wr1
{
    Interpreter *interp = [[Interpreter alloc] init:groupThatCreatedThisInstance errMgr:impl.nativeGroup.errMgr debug:NO];
    return [interp exec:wr1 who:self];
}

- (NSInteger) write:(id<STWriter>)wr1 locale:(NSLocale *)locale
{
    Interpreter *interp = [[Interpreter alloc] init:groupThatCreatedThisInstance locale:locale errMgr:impl.nativeGroup.errMgr debug:NO];
    return [interp exec:wr1 who:self];
}

- (NSInteger) write:(id<STWriter>)wr1 listener:(id<STErrorListener>)listener
{
    Interpreter *interp = [[Interpreter alloc] init:groupThatCreatedThisInstance errMgr:impl.nativeGroup.errMgr debug:NO];
    return [interp exec:wr1 who:self];
}

- (NSInteger) write:(id<STWriter>)wr1 locale:(NSLocale *)locale listener:(id<STErrorListener>)listener
{
    Interpreter *interp = [[Interpreter alloc] init:groupThatCreatedThisInstance locale:locale errMgr:[ErrorManager newErrorManagerWithListener:listener] debug:NO];
    return [interp exec:wr1 who:self];
}

- (NSInteger) writeFile:(NSString *)outputFile
           Listener:(id<STErrorListener>)listener
{
    return [self writeFile:outputFile Listener:listener Encoding:NSUTF8StringEncoding LineWidth:Writer.NO_WRAP Locale:[NSLocale currentLocale]];
}

- (NSInteger) writeFile:(NSString *)outputFile
           Listener:(id<STErrorListener>)listener
           Encoding:(NSStringEncoding)encoding
{
    return [self writeFile:outputFile Listener:listener Encoding:encoding LineWidth:Writer.NO_WRAP Locale:[NSLocale currentLocale]];
}

- (NSInteger) writeFile:(NSString *)outputFile
           Listener:(id<STErrorListener>)listener
           Encoding:(NSStringEncoding)encoding
           LineWidth:(NSInteger)lineWidth
{
    return [self writeFile:outputFile Listener:listener Encoding:encoding LineWidth:lineWidth Locale:[NSLocale currentLocale]];
}

- (NSInteger) writeFile:(NSString *)outputFile
           Listener:(id<STErrorListener>)listener
           Encoding:(NSStringEncoding)encoding
          LineWidth:(NSInteger)lineWidth
             Locale:(NSLocale *)locale
{
    id<STWriter>bw = nil;
    // NSError *error;
    NSInteger n = 0;

    @try {
        //NSString *fn = [outputFile stringByStandardizingPath];
        //NSURL *f = [NSURL fileURLWithPath:fn];
        //NSFileHandle *fh = [NSFileHandle fileHandleForWritingToURL:f error:&error];
        //FileOutputStream *fos = [FileOutputStream newFileOutputStream:outputFile];
        OutputStreamWriter *osw = [OutputStreamWriter newWriter:nil encoding:encoding];
        bw = [BufferedWriter newWriterWithWriter:osw];
        AutoIndentWriter *w = [AutoIndentWriter newWriter:bw];
        [w setLineWidth:lineWidth];
        n = [self write:w locale:locale listener:listener];
        [bw close];
        bw = nil;
        return n;
    }
    @finally {
        if (bw != nil) [bw close];
    }
    return n;
}

- (NSString *) render
{
    return [self render:[NSLocale currentLocale]];
}

- (NSString *) renderWithLineWidth:(NSInteger)aLineWidth
{
    return [self render:[NSLocale currentLocale] lineWidth:aLineWidth];
}

- (NSString *) render:(NSLocale *)locale
{
    return [self render:locale lineWidth:Writer.NO_WRAP];
}

- (NSString *) render:(NSLocale *)locale lineWidth:(NSInteger)aLineWidth
{
//    StringWriter *wr1 = [StringWriter stringWithCapacity:16];
    AutoIndentWriter *wr = [AutoIndentWriter newWriter];
    wr.lineWidth = aLineWidth;
    [self write:wr locale:locale];
//    return [wr1 description];
    return [wr description];
}

#pragma mark fix this sometime
#ifdef DONTUSEYET
    // LAUNCH A WINDOW TO INSPECT TEMPLATE HIERARCHY

- (STViz *)inspect
{
    return [self inspect:(NSLocale *)[NSLocale currentLocale]];
}

- (STViz *)inspect:(NSInteger)lineWidth
{
        return [self inspect:impl.nativeGroup.errMgr locale:(NSLocale *)[NSLocale currentLocale] lineWidth:(NSInteger)lineWidth];
}

- (STViz *)inspect:(NSLocale *)locale
{
    return [self inspect:impl.nativeGroup.errMgr locale:locale lineWidth:STWriter.NO_WRAP];
}

- (STViz *)inspect:(ErrorManager *)errMgr
            locale:(NSLocale *)locale
         lineWidth:(NSInteger)lineWidth
{
        ErrorBuffer *errors = [ErrorBuffer newErrorBuffer];
        [impl.nativeGroup setListener:errors];
        StringWriter *wr1 = [StringWriter newStringWriter];
        id<STWriter>wr = [AutoIndentWriter newAutoIndentWriter:wr1];
        [wr setLineWidth:lineWidth];
        Interpreter *interp =
            [Interpreter newInterpreter:groupThatCreatedThisInstance locale:locale debug:YES];
        [interp exec:wr  who:self]; // render and track events
        AMutableArray *events = [interp getEvents];
        EvalTemplateEvent *overallTemplateEval =
            [(EvalTemplateEvent *)events get([events size]-1];
        STViz *viz = [STViz newSTViz:errMgr
                                root:overallTemplateEval
                              output:[wr1 toString]
                         interpreter:interp
                               trace:[interp getExecutionTrace]
                              errors:errors.errors];
        viz.open();
        return viz;
}

#endif

// TESTING SUPPORT

- (AMutableArray *)getEvents
{
    return [self getEvents:[NSLocale currentLocale]];
}

- (AMutableArray *)getEventsWithLineWidth:(NSInteger)lineWidth
{
    return [self getEvents:[NSLocale currentLocale] lineWidth:lineWidth];
}

- (AMutableArray *)getEvents:(NSLocale *)locale
{
    return [self getEvents:locale lineWidth:Writer.NO_WRAP];
}

- (AMutableArray *)getEvents:(NSLocale *)locale lineWidth:(NSInteger)lineWidth
{
    StringWriter *wr1 = [StringWriter newWriter];
    id<STWriter>wr = [AutoIndentWriter newWriter:wr1];
    [wr setLineWidth:lineWidth];
    Interpreter *interp =
            [Interpreter newInterpreter:groupThatCreatedThisInstance locale:locale debug:YES];
    [interp exec:wr who:self]; // render and track events
    return [interp getEvents];
}

- (NSString *) description
{
    if (impl == nil)
        return @"bad-template()";
    NSString *name = [NSString stringWithFormat:@"%@()", impl.name];
    if ( impl.isRegion ) {
        name = [NSString stringWithFormat:@"@%@", [STGroup getUnMangledTemplateName:name]];
    }
    return name;
}

- (NSString *) toString
{
    return [self description];
}

// ST.format("name, phone | <name>:<phone>", n, p);
// ST.format("<%1>:<%2>", n, p);
// ST.format("<name>:<phone>", "name", x, "phone", y);
+ (NSString *) format:(NSString *)template attributes:(id)attributes
{
    return [self format:template attributes:attributes lineWidth:Writer.NO_WRAP];
}

+ (NSString *) format:(NSString *)aTemplate attributes:(AttributeList *)theAttributes lineWidth:(NSInteger)lineWidth
{
    NSRange aRange;
    NSInteger idx = 1;

    do {
        aRange = [aTemplate rangeOfString:@"\%([0-9]+)"];
        if (aRange.location != NSNotFound) {
            [aTemplate stringByReplacingCharactersInRange:aRange withString:[NSString stringWithFormat:@"arg%d", idx++]];
        }
    } while (aRange.location != NSNotFound );
    NSLog( @"Template = %@", aTemplate );
    ST *st = [ST newSTWithTemplate:aTemplate];
    NSInteger i = 1;

    id a;
    ArrayIterator *it = [ArrayIterator newIterator:theAttributes];
//    for (id a in theAttributes) {
    while ( [it hasNext] ) {
        a = [it nextObject];
        [st add:[NSString stringWithFormat:@"arg%d", i] value:a];
        i++;
    }
    return [st renderWithLineWidth:lineWidth];
}

// getters and setters

@end
