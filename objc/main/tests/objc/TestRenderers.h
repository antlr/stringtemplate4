#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import <GHUnit/GHTestCase.h>
#import "BaseTest.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "STGroupDir.h"
#import "DateRenderer.h"
#import "NumberRenderer.h"
#import "StringRenderer.h"
//#import "GregorianCalendar.h"

@interface TestRenderers : BaseTest {
}

- (void) test01RendererForGroup;
- (void) test02RendererWithFormat;
- (void) test03RendererWithPredefinedFormat;
- (void) test04RendererWithPredefinedFormat2;
- (void) test05RendererWithPredefinedFormat3;
- (void) test06RendererWithPredefinedFormat4;
- (void) test07StringRendererWithPrintfFormat;
- (void) test08NumberRendererWithPrintfFormat;
- (void) test09InstanceofRenderer;
- (void) test10LocaleWithNumberRenderer;
- (void) test11RendererWithFormatAndList;
- (void) test12RendererWithFormatAndSeparator;
- (void) test13RendererWithFormatAndSeparatorAndNull;
@end
