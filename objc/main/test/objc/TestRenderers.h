#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "GregorianCalendar.h"

@interface TestRenderers : SenTestCase {
}

- (void) testRendererForGroup;
- (void) testRendererWithFormat;
- (void) testRendererWithPredefinedFormat;
- (void) testRendererWithPredefinedFormat2;
- (void) testRendererWithPredefinedFormat3;
- (void) testRendererWithPredefinedFormat4;
- (void) testStringRendererWithPrintfFormat;
- (void) testNumberRendererWithPrintfFormat;
- (void) testInstanceofRenderer;
- (void) testLocaleWithNumberRenderer;
- (void) testRendererWithFormatAndList;
- (void) testRendererWithFormatAndSeparator;
- (void) testRendererWithFormatAndSeparatorAndNull;
@end
