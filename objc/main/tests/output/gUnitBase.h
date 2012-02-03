#import <ANTLR/ANTLR.h>
#import "ObjectModelAdaptor.h"

@interface gUnitBase : NSObject {
  NSString *lexerClassName;
  NSString *parserClassName;
  NSString *adaptorClassName;
}

- (NSObject *) execParser:(NSString *)ruleName input:(NSString *)input scriptLine:(int)scriptLine;
@end
