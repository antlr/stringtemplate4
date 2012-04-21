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
#import "StringTable.h"

@implementation StringTable

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        table = [[LinkedHashMap newLinkedHashMap:8] retain];
        i = -1;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in StringTable" );
#endif
    if ( table ) [table release];
    [super dealloc];
}

- (NSInteger) addObject:(NSString *)s
{
    NSString *I = [table get:s];
    
    if (I != nil)
        return [I integerValue];
    i++;
    [table put:s value:[ACNumber numberWithInteger:i]];
    return i;
}

- (AMutableArray *) toArray
{
    AMutableArray *a = [AMutableArray arrayWithCapacity:8];
    NSString *key;
    LHMKeyIterator *it = (LHMKeyIterator *)[table newKeyIterator];
    while ( [it hasNext] ) {
        key = (NSString *)[it next];
        [a addObject:key];
    }
    return a;
}

- (void) put:(id)aKey value:(id)obj
{
    [table put:aKey value:obj];
}

@synthesize table;
@synthesize i;
@end
