#import <ANTLR/ANTLR.h>
#import "Interpreter.h"
#import "ST.h"
#import "STGroup.h"
#import "STGroupFile.h"
#import "AddAttributeEvent.h"
//#import "DebugST.h"
#import "EvalTemplateEvent.h"
#import "InterpEvent.h"
#import "Coordinate.h"
#import "ErrorBuffer.h"
#import "ErrorManager.h"
#import "ErrorType.h"
#import "Interval.h"
#import "DictModelAdaptor.h"
#import "Misc.h"
#import "ObjectModelAdaptor.h"
#import "STCompiletimeMessage.h"
#import "STDump.h"
#import "STGroupCompiletimeMessage.h"
#import "STLexerMessage.h"
#import "STMessage.h"
#import "STModelAdaptor.h"
#import "STRuntimeMessage.h"
//#import "BadLocationException.h"
//#import "DefaultHighlighter.h"
//#import "Highlighter.h"
//#import "JTextComponent.h"
//#import "TreePath.h"
//#import "BufferedWriter.h"
//#import "File.h"
//#import "FileWriter.h"
//#import "IOException.h"

@interface STViz_Anon1 : NSObject <TreeSelectionListener> {
}

- (void) valueChanged:(TreeSelectionEvent *)treeSelectionEvent;
@end

@interface STViz_Anon2 : NSObject <TreeSelectionListener> {
}

- (void) valueChanged:(TreeSelectionEvent *)treeSelectionEvent;
@end

@interface STViz_Anon3 : NSObject <ListSelectionListener> {
}

- (void) valueChanged:(ListSelectionEvent *)e;
@end

@interface STViz_Anon4 : NSObject <CaretListener> {
}

- (void) caretUpdate:(CaretEvent *)e;
@end

@interface STViz_Anon5 : NSObject <ListSelectionListener> {
}

- (void) valueChanged:(ListSelectionEvent *)e;
@end

@interface STViz : NSObject {
    EvalTemplateEvent *root;
    InstanceScope *currentScope;
	AMutableArray *allEvents;
	JTreeSTModel *tmodel;
    ErrorManager *errMgr;
    Interpreter *interp;
	NSString *output;
	AMutableArray *trace;
    AMutableArray *errors;
	STViewFrame *viewFrame;
}

+ (id) newSTViz:(ErrorManager *)anErrMgr root:(EvalTemplateEvent *)aRoot output:(NSString *)anOutput interp:(Interpreter *)anInterp trace:(AMutableArray *)aTrace errors:(AMutableArray *)theErrors
- (id) init:(ErrorManager *)anErrMgr root:(EvalTemplateEvent *)aRoot output:(NSString *)anOutput interp:(Interpreter *)anInterp trace:(AMutableArray *)aTrace errors:(AMutableArray *)theErrors;
- (void) highlight:(JTextComponent *)comp i:(NSInteger)i j:(NSInteger)j;
- (void) updateAttributes:(DebugST *)st m:(STViewFrame *)m;
- (void) updateStack:(DebugST *)st m:(STViewFrame *)m;
- (InterpEvent *) findEventAtOutputLocation:(AMutableArray *)events charIndex:(NSInteger)charIndex;
+ (void) main:(NSArray *)args;
+ (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content;
@end
