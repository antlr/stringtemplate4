
/**
 * @author Terence Parr
 */

#import <AppKit/AppKit.h>

@interface STViewFrame : NSView {
    JToolBar *toolBar1;
    NSSplitView *treeContentSplitPane;
    NSSplitView *treeAttributesSplitPane;
    NSScrollView *treeScrollPane;
    JTree *tree;
    NSScrollView *attributeScrollPane;
    JTree *attributes;
    NSSplitView *outputTemplateSplitPane;
    NSScrollView *scrollPane7;
    NSTextView *output;
    JTabbedPane *templateBytecodeTraceTabPanel;
    NSPanel *panel1;
    NSScrollView *scrollPane3;
    NSTextView *template;
    NSScrollView *scrollPane2;
    JTree *ast;
    NSScrollView *scrollPane15;
    NSTextView *bytecode;
    NSScrollView *scrollPane1;
    NSTextView *trace;
    NSScrollView *errorScrollPane;
    JList *errorList;
}

+ (STViewFrame *)newSTViewFrame;
- (id) init;
@end
