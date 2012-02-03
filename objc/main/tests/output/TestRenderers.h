#import "Test.h"
#import "NSMutableArray.h"
#import "GregorianCalendar.h"
#import "NSMutableArray.h"
#import "Locale.h"
#import "Assert.h"

@interface TestRenderers : BaseTest {
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
