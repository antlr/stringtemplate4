#import <ANTLR/ANTLR.h>

@interface STToken : CommonToken {
}

+ (id) newToken:(id<CharStream>)anInput Type:(NSInteger)aType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop;
+ (id) newToken:(NSInteger)type text:(NSString *)text;

- (id) initWithInput:(id<CharStream>)anInput Type:(NSInteger)aType Channel:(NSInteger)aChannel Start:(NSInteger)aStart Stop:(NSInteger)aStop;
- (id) init:(NSInteger)type text:(NSString *)text;

- (void) dealloc;
- (NSString *) toString;
- (NSString *) description;
@end

