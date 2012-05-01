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

+ (NumberRenderer *) newRenderer
{
    return [[NumberRenderer alloc] init];
}

- (id) init
{
    self = [super init];
    return self;
}

- (NSString *) formatObj:(ACNumber *)obj format:(NSString *)str locale:(NSLocale *)locale
{
    char sep;
    NSUInteger i = 0;
    NSUInteger j = 0;
    NSMutableString *aFmt = [NSMutableString stringWithCapacity:16];
    NSMutableString *dst = [NSMutableString stringWithCapacity:16];
    NSNumberFormatter *numberFormatter = [[[NSNumberFormatter alloc] init] autorelease];
    
    // specify just positive format
    //    [numberFormatter setFormat:@"$#,##0.00"];
    if ( [str characterAtIndex:0] == '%' ) {
        i++;
        if ( [str characterAtIndex:[str length]-1] == 'f' ) {
            [numberFormatter setAllowsFloats:YES];
            [numberFormatter setFormat:@"#,##0.00"];
        }
        else {
            [numberFormatter setAllowsFloats:NO];
            [numberFormatter setMaximumFractionDigits:0];
            [numberFormatter setFormat:@"#,##0"];
        }
        if (  [str characterAtIndex:i] == ',' ) {
            [numberFormatter setUsesGroupingSeparator:YES];
            [numberFormatter setGroupingSeparator:@"\u00A0"];
            [numberFormatter setGroupingSize:3];
            if ( [str characterAtIndex:[str length]-1] == 'f' ) {
                [numberFormatter setFormat:@"#.##0,00"];
                [numberFormatter setDecimalSeparator:@","];
            }
            else {
                [numberFormatter setMaximumFractionDigits:0];
                [numberFormatter setFormat:@"#\u00A0##0"];
            }
            NSInteger val = [obj integerValue];
            if (val < 0)
                val *= -1;
            NSInteger dval = 1;
            for ( j = 1; dval < val; j++ ) {
                if ( (val / (dval)) < 10 ) break;
                dval *=10;
            }
            [numberFormatter setMaximumIntegerDigits:j];
            i++;
        }
        if ( locale != nil )
            [numberFormatter setLocale:locale];
        if ( [str characterAtIndex:i] == '0' ) {
            [numberFormatter setPaddingCharacter:@"0"];
            i++;
        }
        for ( j = i; j < [str length]; j++ ) {
            if ( [str characterAtIndex:j] == '.' ) break;
        }
        if ( j < [str length] ) {
            NSString *wStr = [str substringWithRange:NSMakeRange(i, (j-i))];
            [numberFormatter setFormatWidth:[wStr integerValue]];
            if ( [str length]-2 > j ) {
                NSString *wStr = [str substringWithRange:NSMakeRange(j+1, ([str length]-1)-(j+1))];
                [numberFormatter setMaximumFractionDigits:[wStr integerValue]];
            }
        }
        NSLog( @"%@", [numberFormatter format] );
        NSString *nStr = [numberFormatter stringFromNumber:[obj getNSNum]];
        [dst appendString:nStr];
    }
    return dst;
}

- (NSString *) description:(id)obj formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    if (formatString == nil)
        return ((obj!=nil)?[obj description]:@"NumberRenderer called with obj=<nil>");
    //Formatter *f = [[Formatter alloc] init:locale];
    //[f format:formatString param1:obj];
    //return [f description];
    if ( [obj isKindOfClass:[ACNumber class]] ) {
        if ( [formatString characterAtIndex:0] == '%' && [formatString characterAtIndex:1] == ',' ) {
            return [self formatObj:obj format:formatString locale:locale];
        }
        return [obj descriptionWithFormat:formatString locale:locale];
    }
/*
    if ([obj isKindOfClass:[NSString class]] )
        return [NSString stringWithFormat:formatString, obj];
 */
    return @"NumberRenderer called with obj != ACNumber class object";
}

- (NSString *) toString:(id)obj formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    return [self description:obj formatString:formatString locale:locale];
}

@end
