#import "Test.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupDir.h"
#import "STGroupFile.h"
#import "Assert.h"

@interface TestImports : BaseTest {
}

- (void) testImportDir;
- (void) testImportDirInJarViaCLASSPATH;
- (void) testImportGroupAtSameLevelInJar;
- (void) testImportGroupInJarViaCLASSPATH;
- (void) testImportRelativeDir;
- (void) testImportGroupFileSameDir;
- (void) testImportRelativeGroupFile;
- (void) testImportTemplateFileSameDir;
- (void) testImportRelativeTemplateFile;
- (void) testImportTemplateFromAnotherGroupObject;
- (void) testImportTemplateInGroupFileFromDir;
- (void) testImportTemplateInGroupFileFromGroupFile;
- (void) testImportTemplateFromSubdir;
- (void) testImportTemplateFromGroupFile;
- (void) testPolymorphicTemplateReference;
- (void) testSuper;
- (void) testUnloadImportedTemplate;
@end
