#import <ANTLR/ANTLR.h>

@interface STToken : CommonToken {
}

+ (id) newToken:(id<CharStream>)anInput Type:(NSInteger)aType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop;
+ (id) newToken:(NSInteger)type Text:(NSString *)text;

- (id) initWithInput:(id<CharStream>)anInput Type:(NSInteger)aType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop;
- (id) init:(NSInteger)type Text:(NSString *)text;

- (void) dealloc;
- (NSString *) description;
@end

