#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import <SenTestingKit/SenTestingKit.h>
#import "ST.h"
#import "STGroup.h"
#import "STGroupDir.h"
#import "STGroupFile.h"

@interface TestImports : SenTestCase {
}

- (void) testImportTemplate;
- (void) testImportStatementWithDir;
- (void) testImportStatementWithFile;
- (void) testImportTemplateInGroupFileFromDir;
- (void) testImportTemplateInGroupFileFromGroupFile;
- (void) testImportTemplateFromSubdir;
- (void) testImportTemplateFromGroupFile;
- (void) testPolymorphicTemplateReference;
- (void) testSuper;
- (void) testUnloadImportedTemplate;
@end
