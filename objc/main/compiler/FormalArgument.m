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
#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "ST.h"
#import "FormalArgument.h"
#import "CompiledST.h"

@implementation FormalArgument

@synthesize name;
@synthesize index;
@synthesize defaultValueToken;
@synthesize defaultValue;
@synthesize compiledDefaultValue;

static NSInteger OPTIONAL = 1;     // a?
static NSInteger REQUIRED = 2;     // a
static NSInteger ZERO_OR_MORE = 4; // a*
static NSInteger ONE_OR_MORE = 8;  // a+
static NSString *suffixes[] = {
    nil,
    @"?",
    @"",
    nil,
    @"*",
    nil,
    nil,
    nil,
    @"+"
};

+ (NSInteger) OPTIONAL
{
    return OPTIONAL;
}

+ (NSInteger) REQUIRED
{
    return REQUIRED;
}

+ (NSInteger) ZERO_OR_MORE
{
    return ZERO_OR_MORE;
}

+ (NSInteger) ONE_OR_MORE
{
    return ONE_OR_MORE;
}

+ (NSString *) suffixes:(NSInteger)idx
{
    if (idx < 9)
        return suffixes[idx];
    return nil;
}

+ (id) newFormalArgument
{
    return [[FormalArgument alloc] init];
}

+ (id) newFormalArgument:(NSString *)aName
{
    return [[FormalArgument alloc] initWithName:aName];
}

+ (id) newFormalArgument:(NSString *)aName token:(CommonToken *)aToken
{
    return [[FormalArgument alloc] init:aName token:aToken];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        cardinality = REQUIRED;
        index = 0;
        name = @"";
        if ( name ) [name retain];
        defaultValueToken = nil;
        compiledDefaultValue = nil;
    }
    return self;
}

- (id) initWithName:(NSString *)aName
{
    self=[super init];
    if ( self != nil ) {
        cardinality = REQUIRED;
        index = 0;
        name = aName;
        if ( name ) [name retain];
        defaultValueToken = nil;
        if ( defaultValueToken ) [defaultValueToken retain];
        compiledDefaultValue = nil;
    }
    return self;
}

- (id) init:(NSString *)aName token:(CommonToken *)aToken
{
    self=[super init];
    if ( self != nil ) {
        cardinality = REQUIRED;
        index = 0;
        name = aName;
        if ( name ) [name retain];
        defaultValueToken = aToken;
        if ( defaultValueToken ) [defaultValueToken retain];
        compiledDefaultValue = nil;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in FormalArgument" );
#endif
    if ( name ) [name release];
    if ( defaultValueToken ) [defaultValueToken release];
    if ( compiledDefaultValue ) [compiledDefaultValue release];
    [super dealloc];
}

- (NSInteger) hash
{
    return [name hash] + [defaultValueToken hash];
}

- (BOOL) isEqualTo:(NSString *)obj
{
    if ( obj == nil || !([obj isKindOfClass:[FormalArgument class]]) ) {
        return NO;
    }
    FormalArgument *other = (FormalArgument *)obj;
    if (![name isEqualTo:other.name]) {
        return NO;
    }
    return !((defaultValueToken != nil && other.defaultValueToken == nil) ||
             (defaultValueToken == nil && other.defaultValueToken != nil));
}

- (NSString *) description
{
    if (defaultValueToken != nil)
        return [NSString stringWithFormat:@"%@=%@", ((name != nil)?name:@"nil"), defaultValueToken.text];
    return name;
}

- (NSString *) toString
{
    return [self description];
}

@end
