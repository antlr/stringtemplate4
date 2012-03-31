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
#import "NumberRenderer.h"
#import <ANTLR/ACNumber.h>

@implementation NumberRenderer

- (id) init
{
    self = [super init];
    return self;
}

- (NSString *) description:(id)obj formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    if (formatString == nil)
        return ((obj!=nil)?[obj description]:@"NumberRenderer called with obj=<nil>");
    //Formatter *f = [[Formatter alloc] init:locale];
    //[f format:formatString param1:obj];
    //return [f description];
    if ( [obj isKindOfClass:[ACNumber class]] )
        return [obj description];
    if ([obj isKindOfClass:[NSString class]] )
        return [NSString stringWithFormat:formatString, obj];
    return @"NumberRenderer called with obj != NSString class object";
}

- (NSString *) toString:(id)obj formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    return [self description:obj formatString:formatString locale:locale];
}

@end
