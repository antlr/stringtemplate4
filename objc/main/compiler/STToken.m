#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import <STToken.h>
#import "Misc.h"
#import "STParser.h"

@implementation STToken

+ (id) newToken:(id<CharStream>)anInput Type:(NSInteger)aType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop
{
    return [[STToken alloc] initWithInput:anInput Type:aType Channel:aChannel Start:aStart Stop:aStop];
}

+ (id) newToken:(NSInteger)aType Text:(NSString *)theText
{
    return [[STToken alloc] init:aType Text:theText];
}

- (id) init:(NSInteger)aType Text:(NSString *)aText
{
    self=[super initWithType:aType Text:aText];
    if ( self != nil ) {
    }
    return self;
}

- (id) initWithInput:(id<CharStream>)anInput Type:(NSInteger)aType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop
{
    self=[super initWithInput:anInput Type:aType Channel:aChannel Start:aStart Stop:aStop];
    if ( self != nil ) {
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in CommonToken" );
#endif
    [super dealloc];
}

- (NSString *) description
{
    NSString *channelStr = @"";
    if (channel > 0) {
        channelStr = [NSString stringWithFormat:@",channel=%d", channel];
    }
    NSString *txt = self.text;
    if (txt != nil)
        txt = [Misc replaceEscapes:txt];
    else
        txt = @"<no text>";
    NSString *tokenName = nil;
    if (type == TokenTypeEOF)
        tokenName = @"<EOF>";
    else
        tokenName = [[STParser getTokenNames] objectAtIndex:type];
    return [NSString stringWithFormat:@"[@%d,%d:%d='%@',<%@>%@,%d:%d]",
        [self getTokenIndex], startIndex, stopIndex, txt, tokenName, channelStr, line, charPositionInLine];
}

@end

