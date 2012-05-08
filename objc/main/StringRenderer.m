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
#import "StringRenderer.h"

@implementation StringRenderer

+ (StringRenderer *) newRenderer
{
    return [[StringRenderer alloc] init];
}

- (id) init
{
    self = [super init];
    return self;
}

- (NSString *) description:(id)obj formatString:(NSString *)formatString locale:(NSLocale *)locale {
    NSString *s;
    if ( [obj isKindOfClass:[NSString class]] )  
        s = (NSString *)obj;
    else
        return ( @"Invalid Class Object for obj\n" );

    if ( formatString == nil )
        return s;
    if ( [formatString isEqualToString:@"upper"] )
        return [s uppercaseString];
    if ( [formatString isEqualToString:@"lower"] )
        return [s lowercaseString];
    if ( [formatString isEqualToString:@"cap"] ) {
        return (([s length] > 0) ? [s capitalizedString] : s);
    }
    if ( [formatString isEqualToString:@"url-encode"] ) {
        //return [URLEncoder encode:s ];
        return [s stringByStandardizingPath];
    }
#pragma error to resolve
    if ( [formatString isEqualToString:@"xml-encode"] ) {
        return [StringRenderer escapeHTML:s];
    }
    char *str;
    str = [s cStringUsingEncoding:NSASCIIStringEncoding];
    return [NSString stringWithFormat:formatString, str];
}

+ (NSString *) escapeHTML:(NSString *)s {
    if (s == nil) {
        return nil;
    }
    BOOL aboveASCII;
    BOOL control;
    NSInteger len = [s length];
    NSMutableString *buf = [NSMutableString stringWithCapacity:len];
    
    for (NSInteger i = 0; i < len; i++) {
        unichar c = [s characterAtIndex:i];
        
        switch (c) {
            case '&':
                [buf appendString:@"&amp;"];
                break;
            case '<':
                [buf appendString:@"&lt;"];
                break;
            case '>':
                [buf appendString:@"&gt;"];
                break;
            case '\r':
            case '\n':
            case '\t':
                [buf appendFormat:@"%c", c];
                break;
            default:
                control = (c < ' ');
                aboveASCII = (c > 126);
                if (control || aboveASCII) {
                    [buf appendFormat:@"&#%d;", (NSInteger)c];
                }
                else
                    [buf appendFormat:@"%c", c];
        }
    }
    
    return buf;
}

@end
