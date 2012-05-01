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
#import "DateRenderer.h"

@implementation DateRenderer_Anon1

@synthesize aDict;

+ (id) newDateRenderer_Anon1
{
    return [[DateRenderer_Anon1 alloc] init];
}

+ (id) newDateRenderer_Anon1:(NSInteger)len
{
    return [[DateRenderer_Anon1 alloc] initWithCapacity:len];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        aDict = [[LinkedHashMap newLinkedHashMap:32] retain];
        [aDict put:@"short" value:[ACNumber numberWithInteger:NSDateFormatterShortStyle]];
        [aDict put:@"medium" value:[ACNumber numberWithInteger:NSDateFormatterMediumStyle]];
        [aDict put:@"long" value:[ACNumber numberWithInteger:NSDateFormatterLongStyle]];
        [aDict put:@"full" value:[ACNumber numberWithInteger:NSDateFormatterFullStyle]];
        [aDict put:@"date:short" value:[ACNumber numberWithInteger:NSDateFormatterShortStyle]];
        [aDict put:@"date:medium" value:[ACNumber numberWithInteger:NSDateFormatterMediumStyle]];
        [aDict put:@"date:long" value:[ACNumber numberWithInteger:NSDateFormatterLongStyle]];
        [aDict put:@"date:full" value:[ACNumber numberWithInteger:NSDateFormatterFullStyle]];
        [aDict put:@"time:short" value:[ACNumber numberWithInteger:NSDateFormatterShortStyle]];
        [aDict put:@"time:medium" value:[ACNumber numberWithInteger:NSDateFormatterMediumStyle]];
        [aDict put:@"time:long" value:[ACNumber numberWithInteger:NSDateFormatterLongStyle]];
        [aDict put:@"time:full" value:[ACNumber numberWithInteger:NSDateFormatterFullStyle]];
    }
    return self;
}

- (id) initWithCapacity:(NSInteger)len
{
    self=[super init];
    if ( self != nil ) {
        aDict = [[LinkedHashMap newLinkedHashMap:len] retain];
        [aDict put:@"short" value:[ACNumber numberWithInteger:NSDateFormatterShortStyle]];
        [aDict put:@"medium" value:[ACNumber numberWithInteger:NSDateFormatterMediumStyle]];
        [aDict put:@"long" value:[ACNumber numberWithInteger:NSDateFormatterLongStyle]];
        [aDict put:@"full" value:[ACNumber numberWithInteger:NSDateFormatterFullStyle]];
        [aDict put:@"date:short" value:[ACNumber numberWithInteger:NSDateFormatterShortStyle]];
        [aDict put:@"date:medium" value:[ACNumber numberWithInteger:NSDateFormatterMediumStyle]];
        [aDict put:@"date:long" value:[ACNumber numberWithInteger:NSDateFormatterLongStyle]];
        [aDict put:@"date:full" value:[ACNumber numberWithInteger:NSDateFormatterFullStyle]];
        [aDict put:@"time:short" value:[ACNumber numberWithInteger:NSDateFormatterShortStyle]];
        [aDict put:@"time:medium" value:[ACNumber numberWithInteger:NSDateFormatterMediumStyle]];
        [aDict put:@"time:long" value:[ACNumber numberWithInteger:NSDateFormatterLongStyle]];
        [aDict put:@"time:full" value:[ACNumber numberWithInteger:NSDateFormatterFullStyle]];
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

- (void) put:(id)aKey value:(id)anObject
{
    [aDict put:[aKey retain] value:[anObject retain]];
}

- (id) get:(id)aKey
{
    return [aDict get:aKey];
}

@end

@implementation DateRenderer

static DateRenderer_Anon1 *formatToInt;

+ (void) initialize
{
    formatToInt = [DateRenderer_Anon1 newDateRenderer_Anon1];
}

+ (id) newRenderer
{
    return [[DateRenderer alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        formatToInt = [DateRenderer_Anon1 newDateRenderer_Anon1];
    }
    return self;
}

- (NSString *) description:(NSDate *)d formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    ACNumber *style;
    NSInteger styleI;
    NSString *dateFormat;
    NSString *dateStr;
    if (formatString == nil)
        formatString = @"short";
//    if ([obj isMemberOfClass:[NSDate class]])
//        d = [((NSCalendar *)obj) time];
//    else
//        d = (NSDate *)obj;
    style = (ACNumber *)[formatToInt get:formatString];
    if (locale == nil ) 
        locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US"];
    if (style == nil) {
        NSDateFormatter *aFormatter = [[NSDateFormatter alloc] init];
        [aFormatter setDateFormat:formatString];
        
        //        dateFormat = [NSDateFormatter dateFormatFromTemplate:formatString options:0 locale:locale];
        dateStr = [aFormatter stringFromDate:d];
        //        dateStr = [NSDateFormatter localizedStringFromDate:d dateStyle:NSDateFormatterMediumStyle timeStyle:NSDateFormatterMediumStyle];
        //        dateStr = [d descriptionWithCalendarFormat:dateFormat timeZone:nil locale:locale];
        //        dateStr = [d descriptionWithCalendarFormat:formatString timeZone:nil locale:locale];
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
    return ((dateStr == nil) ? @"dateStr=<nil>" : dateStr );
}

- (NSString *) toString:(NSDate *)d formatString:(NSString *)formatString locale:(NSLocale *)locale
{
    return [self description:d formatString:formatString locale:locale];
}

@end
