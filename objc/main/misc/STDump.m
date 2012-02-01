#import "STErrorListener.h"
#import "STDump.h"
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
#import "ST.h"
#import "CompiledST.h"
#import "Misc.h"

NSInteger compare(NSString *s1, NSString *s2, void *context);

@implementation STDump

@synthesize who;

+ (id) newSTDumpWithWho:(ST *) aWho
{
    return [[STDump alloc] initWithWho:(ST *)aWho];
}

+ (NSString *) description:(ST *)aWho
{
    STDump *d = [STDump newSTDumpWithWho:aWho];
    return [d description];
}

+ (NSString *) toString:(ST *)aWho
{
    return [STDump description:aWho];
}

- (id) initWithWho:(ST *)aWho
{
    self=[super init];
    if ( self != nil ) {
        who = aWho;
        if ( who ) [who retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STDump" );
#endif
    if ( who ) [who release];
    [super dealloc];
}

- (NSString *) description
{
    return [self description:0];
}

- (NSString *) toString
{
    return [self description:0];
}

- (NSString *) toString:(NSInteger)n
{
    return [self description:n];
}

- (NSString *) description:(NSInteger)n
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:100];
    [buf appendFormat:@"%@:", [self getTemplateDeclaratorString]];
    n++;
    AMutableDictionary *attributes = [who getAttributes];
    if (attributes != nil) {
        AMutableArray *attrNames = [AMutableArray arrayWithCapacity:5];
        [attrNames addObjectsFromArray:[attributes allKeys]];
        [attrNames sortUsingFunction:compare context:nil];
        NSString *longestName = [attrNames objectAtIndex:0];
        NSInteger w = [longestName length];
        
        NSString *fmtStr;
        fmtStr = [NSString stringWithFormat:@"%%-%d$@= %%@", w];
        id attrName;
        ArrayIterator *it = [ArrayIterator newIterator:attrNames];
//        for (id attrName in attrNames) {
        while ( [it hasNext] ) {
            attrName = [it nextObject];
            NSString *name = (NSString *)attrName;
            [buf appendString:@"\n"];
            [self indent:buf n:n];
            [buf appendFormat:fmtStr, [name cStringUsingEncoding:NSASCIIStringEncoding]];
            id value = [attributes objectForKey:name];
            [buf appendString:[self getValueDebugString:value n:n]];
        }
        
    }
    [buf appendString:[Misc newline]];
    n--;
    [self indent:buf n:n];
    [buf appendString:@"]"];
    return [buf description];
}
            
- (NSString *) getValueDebugString:(id)value n:(NSInteger)n
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:16];
    value = [Interpreter convertAnythingIteratableToIterator:value];
    if ([value isKindOfClass:[ST class]]) {
        STDump *d = [STDump newSTDumpWithWho:(ST *)value];
        [buf appendString:[d toString:n]];
    }
    else if ([value isKindOfClass:[ArrayIterator class]]) {
        ArrayIterator *it = (ArrayIterator *)value;
        id obj;
        NSInteger na = 0;
        while ([it hasNext]) {
            obj = [it nextObject];
            NSString *v = [self getValueDebugString:obj n:n];
            if ( na > 0 )
                [buf appendString:@", "];
            [buf appendString:v];
            na++;
        }
    }
    else {
        [buf appendString:value];
    }
    return [buf description];
}

- (NSString *) getTemplateDeclaratorString
{
    NSMutableString *buf = [NSMutableString stringWithCapacity:150];
    [buf appendFormat:@"<%@(", [who getName]];
    if (((CompiledST *)who.impl).formalArguments != nil) {
        ArrayIterator *it = [ArrayIterator newIteratorForDictKey:(NSDictionary *)who.impl.formalArguments];
        id obj;
        NSInteger na = 0;
        while ([it hasNext]) {
            obj = [it nextObject];
            if ( na > 0 )
                [buf appendString:@", "];
            [buf appendString:obj];
            na++;
        }
    }
    [buf appendString:@")@"];
    [buf appendFormat:@"%d", [self hash]];
    [buf appendString:@">"];
    return [buf description];
}

- (void) indent:(NSMutableString *)buf n:(NSInteger)n
{
    
    for (NSInteger i = 1; i <= n; i++)
        [buf appendString:@"   "];
}

@end

NSInteger compare(NSString *s1, NSString *s2, void *context)
{
    int v1 = [s1 length];
    int v2 = [s2 length];
    if (v1 < v2)
        return NSOrderedAscending;
    else if (v1 > v2)
        return NSOrderedDescending;
    return NSOrderedSame;
}


