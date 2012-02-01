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
#import "DateRenderer.h"

@implementation DateRenderer_Anon1

@synthesize aDict;

+ (id) newDictionary
{
    return [[DateRenderer_Anon1 alloc] init];
}

+ (id) DictionaryWithCapacity:(NSInteger)len
{
    return [[DateRenderer_Anon1 alloc] initWithCapacity:len];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        aDict = [[AMutableDictionary dictionaryWithCapacity:16] retain];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"short"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterMediumStyle] forKey:@"medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterLongStyle] forKey:@"long"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterFullStyle] forKey:@"full"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"date:short"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterMediumStyle] forKey:@"date:medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterLongStyle] forKey:@"date:long"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterFullStyle] forKey:@"date:full"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"time:short"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterMediumStyle] forKey:@"time:medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterLongStyle] forKey:@"time:long"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterFullStyle] forKey:@"time:full"];
    }
    return self;
}

- (id) initWithCapacity:(NSInteger)len
{
    self=[super init];
    if ( self != nil ) {
        aDict = [[AMutableDictionary dictionaryWithCapacity:len] retain];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"short"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterMediumStyle] forKey:@"medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterLongStyle] forKey:@"long"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterFullStyle] forKey:@"full"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"date:short"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterMediumStyle] forKey:@"date:medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterLongStyle] forKey:@"date:long"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterFullStyle] forKey:@"date:full"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterShortStyle] forKey:@"time:short"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterMediumStyle] forKey:@"time:medium"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterLongStyle] forKey:@"time:long"];
        [aDict setObject:[NSString stringWithFormat:@"%ld", (long)NSDateFormatterFullStyle] forKey:@"time:full"];
    }
    return self;
}

- (void)dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in DateRenderer_Anon1" );
#endif
    if ( aDict ) [aDict release];
    [super dealloc];
}

- (void) setObject:(id)anObject forKey:(id)aKey
{
    [aDict setObject:[anObject retain] forKey:[aKey retain]];
}

- (id) objectForKey:(id)aKey
{
    return [aDict objectForKey:aKey];
}

@end

@implementation DateRenderer

static DateRenderer_Anon1 *formatToInt;

+ (void) initialize
{
    formatToInt = [DateRenderer_Anon1 newDictionary];
}

+ (id) newDateRenderer
{
    return [[DateRenderer alloc] init];
}

- (id) init
{
    if ( (self=[super init]) != nil ) {
        formatToInt = [DateRenderer_Anon1 newDictionary];
    }
    return self;
}

- (NSString *) description:(NSDate *)d formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    NSString *style;
    NSInteger styleI;
    NSString *dateStr;
    if (formatString == nil)
        formatString = @"short";
//    if ([obj isMemberOfClass:[NSDate class]])
//        d = [((NSCalendar *)obj) time];
//    else
//        d = (NSDate *)obj;
    style = (NSString *)[formatToInt objectForKey:formatString];
    if (style == nil) {
        dateStr = [NSDateFormatter localizedStringFromDate:d dateStyle:NSDateFormatterMediumStyle timeStyle:NSDateFormatterMediumStyle];
    }
    else {
        styleI = [style integerValue];
        if ([formatString hasPrefix:@"date:"]) {
            dateStr = [NSDateFormatter localizedStringFromDate:d dateStyle:styleI timeStyle:NSDateFormatterNoStyle];
        }
        else if ([formatString hasPrefix:@"time:"]) {
            dateStr = [NSDateFormatter localizedStringFromDate:d dateStyle:NSDateFormatterNoStyle timeStyle:styleI];
        }
        else {
            dateStr = [NSDateFormatter localizedStringFromDate:d dateStyle:styleI timeStyle:styleI];
        }
    }
    return dateStr;
}

- (NSString *) toString:(NSDate *)d formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    return [self description:d formatString:formatString locale:locale];
}

@end
