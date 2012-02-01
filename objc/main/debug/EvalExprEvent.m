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
#import "STErrorListener.h"
#import "EvalExprEvent.h"
#import "ST.h"
#import "CompiledST.h"

@implementation EvalExprEvent

+ (id) newEvent:(InstanceScope *)aScope start:(NSInteger)aStart stop:(NSInteger)aStop exprStart:(NSInteger)anExprStart exprStop:(NSInteger)anExprStop
{
    return [[EvalExprEvent alloc] init:aScope start:aStart stop:aStop exprStart:anExprStart exprStop:anExprStop];
}

- (id) init:(InstanceScope *)aScope start:(NSInteger)aStart stop:(NSInteger)aStop exprStart:(NSInteger)anExprStart exprStop:(NSInteger)anExprStop
{
    self=[super init:aScope start:aStart stop:aStop];
    if ( self != nil ) {
        exprStartChar = anExprStart;
        exprStopChar = anExprStop;
        if (exprStartChar >= 0 && exprStopChar >= 0) {
            expr = [((CompiledST *)(scope.st.impl)).template substringWithRange:NSMakeRange(exprStartChar, (exprStopChar-exprStartChar) + 1)];
        }
    }
    return self;
}

- (void) dealloc
{
    if ( expr ) [expr release];
    [super dealloc];
}

- (NSString *) description
{
    return [NSString stringWithFormat:@"{self=%@, expr=%@, exprStartChar=%d, exprStopChar=%d start=%d, stop=%d}", [self className], expr, exprStartChar, exprStopChar, outputStartChar, outputStopChar];
}

- (NSString *) toString
{
    return [self description];
}

@synthesize exprStartChar;
@synthesize exprStopChar;
@synthesize expr;
@end
