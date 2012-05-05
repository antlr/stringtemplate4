#import "STViz.h"

@implementation STViz_Anon1

- (void) valueChanged:(TreeSelectionEvent *)treeSelectionEvent
{
    currentScope = ((Wrapper *)[viewFrame.tree lastSelectedPathComponent]).event.scope;
    [self updateCurrentST:viewFrame];
}

@end

@implementation STViz_Anon2

- (void) valueChanged:(TreeSelectionEvent *)treeSelectionEvent
{
    TreePath * path = [treeSelectionEvent newLeadSelectionPath];
    if (path == nil)
        return;
    CommonTree *node = (CommonTree *)[[treeSelectionEvent newLeadSelectionPath] lastPathComponent];
    CommonToken *a = (CommonToken *)[currentScope.st.impl.tokens get:[node tokenStartIndex]];
    CommonToken *b = (CommonToken *)[currentScope.st.impl.tokens get:[node tokenStopIndex]];
    [self highlight:viewFrame.template i:[a startIndex] j:[b stopIndex]];
}

@end

@implementation STViz_Anon3

- (void) caretUpdate:(CaretEvent *)e
{
    NSInteger dot = [e dot];
    InterpEvent *de = [self findEventAtOutputLocation:allEvents param1:dot];
    if (de == nil)
        currentScope = tmodel.root.event.scope;
    else
        currentScope = de.scope;
    NSMutableArray *stack = [Interpreter getEvalTemplateEventStack:currentScope param1:YES];
    NSArray *path = [NSArray array];
    int j = 0;
    
    for (EvalTemplateEvent *s in stack)
        path[j++] = [[[Wrapper alloc] init:s] autorelease];
    
    TreePath *p = [[[TreePath alloc] init:path] autorelease];
    [viewFrame.tree setSelectionPath:p];
    [viewFrame.tree scrollPathToVisible:p];
    [self updateCurrentST:viewFrame];
}

@end

@implementation STViz_Anon4

- (void) valueChanged:(ListSelectionEvent *)e
{
    NSInteger minIndex = [viewFrame.errorList minSelectionIndex];
    NSInteger maxIndex = [viewFrame.errorList maxSelectionIndex];
    NSInteger i = minIndex;
    
    while (i <= maxIndex) {
        if ([viewFrame.errorList isSelectedIndex:i])
            break;
        i++;
    }
    
    ListModel *model = [viewFrame.errorList model];
    STMessage *msg = (STMessage *)[model getElementAt:i];
    if ([msg conformsToProtocol:@protocol(STRuntimeMessage)]) {
        STRuntimeMessage *rmsg = (STRuntimeMessage *)msg;
        Interval *I = rmsg.self.impl.sourceMap[rmsg.ip];
        currentScope = ((STRuntimeMessage *)msg).scope;
        [self updateCurrentST:viewFrame];
        if (I != nil) {
            [self highlight:viewFrame.template i:I.a j:I.b];
        }
    }
}

@end

@implementation STViz

+ (STViz *) newSTViz:(ErrorManager *)anErrMgr root:(EvalTemplateEvent *)aRoot output:(NSString *)anOutput interp:(Interpreter *)anInterp trace:(AMutableArray *)aTrace errors:(AMutableArray *)theErrors
{
    return [[STViz alloc] init:anErrMgr root:aRoot output:anOutput interp:anInterp trace:aTrace errors:theErrors];
}

- (id) init:(ErrorManager *)anErrMgr root:(EvalTemplateEvent *)aRoot output:(NSString *)anOutput interp:(Interpreter *)anInterp trace:(AMutableArray *)aTrace errors:(AMutableArray *)theErrors
{
    if (self = [super init]) {
        errMgr = errMgr;
        currentScope = root.scope;
        output = output;
        interp = interp;
        allEvents = [interp events];
        trace = trace;
        errors = errors;
    }
    return self;
}

- (void) open
{
    viewFrame = [[[STViewFrame alloc] init] autorelease];
    [self updateStack:currentScope m:viewFrame];
    [self updateAttributes:currentScope m:viewFrame];
    NSMutableArray * events = currentScope.events;
    tmodel = [[[JTreeSTModel alloc] init:interp param1:(EvalTemplateEvent *)[events objectAtIndex:[events count] - 1]] autorelease];
    [viewFrame.tree setModel:tmodel];
    [viewFrame.tree addTreeSelectionListener:[[[STViz_Anon1 alloc] init] autorelease]];
    JTreeASTModel * astModel = [[[JTreeASTModel alloc] init:[[[CommonTreeAdaptor alloc] init] autorelease] param1:currentScope.st.impl.ast] autorelease];
    [viewFrame.ast setModel:astModel];
    [viewFrame.ast addTreeSelectionListener:[[[STViz_Anon2 alloc] init] autorelease]];
    [viewFrame.output setText:output];
    [viewFrame.template setText:currentScope.st.impl.template];
    [viewFrame.bytecode setText:[currentScope.st.impl disasm]];
    [viewFrame.trace setText:[Misc join:[trace objectEnumerator] param1:@"\n"]];
    CaretListener * caretListenerLabel = [[[STViz_Anon3 alloc] init] autorelease];
    [viewFrame.output addCaretListener:caretListenerLabel];
    if (errors == nil || [errors count] == 0) {
        [viewFrame.errorScrollPane setVisible:NO];
    }
    else {
        DefaultListModel *errorListModel = [[[DefaultListModel alloc] init] autorelease];
        
        for (STMessage *msg in errors) {
            [errorListModel addElement:msg];
        }
        
        [viewFrame.errorList setModel:errorListModel];
    }
    [viewFrame.errorList addListSelectionListener:[[[STViz_Anon4 alloc] init] autorelease]];
    Border * empty = [BorderFactory createEmptyBorder];
    [viewFrame.treeContentSplitPane setBorder:empty];
    [viewFrame.outputTemplateSplitPane setBorder:empty];
    [viewFrame.templateBytecodeTraceTabPanel setBorder:empty];
    [viewFrame.treeAttributesSplitPane setBorder:empty];
    [viewFrame.treeContentSplitPane setOneTouchExpandable:YES];
    [viewFrame.outputTemplateSplitPane setOneTouchExpandable:YES];
    [viewFrame.treeContentSplitPane setDividerSize:10];
    [viewFrame.outputTemplateSplitPane setDividerSize:8];
    [viewFrame.treeContentSplitPane setContinuousLayout:YES];
    [viewFrame.treeAttributesSplitPane setContinuousLayout:YES];
    [viewFrame.outputTemplateSplitPane setContinuousLayout:YES];
    [viewFrame setDefaultCloseOperation:JFrame.DISPOSE_ON_CLOSE];
    [viewFrame pack];
    [viewFrame setSize:900 param1:700];
    [viewFrame setVisible:YES];
}

- (void) updateCurrentST:(STViewFrame *)m
{
    [self updateStack:currentScope m:m];
    [self updateAttributes:currentScope m:m];
    [m.bytecode moveCaretPosition:0];
    [m.bytecode setText:[currentScope.st.impl disasm]];
    [m.template moveCaretPosition:0];
    [m.template setText:currentScope.st.impl.template];
    JTreeASTModel * astModel = [[[JTreeASTModel alloc] init:[[[CommonTreeAdaptor alloc] init] autorelease] param1:currentScope.st.impl.ast] autorelease];
    [viewFrame.ast setModel:astModel];
    NSMutableArray * events = currentScope.events;
    EvalTemplateEvent * e = (EvalTemplateEvent *)[events objectAtIndex:[events count] - 1];
    [self highlight:m.output i:e.outputStartChar j:e.outputStopChar];
    
    @try {
        [m.output scrollRectToVisible:[m.output modelToView:e.outputStartChar]];
    }
    @catch (BadLocationException * ble) {
        [currentScope.st.groupThatCreatedThisInstance.errMgr internalError:currentScope.st param1:[@"bad location: char index " stringByAppendingString:e.outputStartChar] param2:ble];
    }
    if ([currentScope.st anonSubtemplate]) {
        Interval * r = [currentScope.st.impl templateRange];
        [self highlight:m.template i:r.a j:r.b];
    }
}

- (void) highlight:(JTextComponent *)comp i:(int)i j:(int)j
{
    Highlighter * highlighter = [comp highlighter];
    [highlighter removeAllHighlights];
    
    @try {
        [highlighter addHighlight:i param1:j + 1 param2:DefaultHighlighter.DefaultPainter];
    }
    @catch (BadLocationException * ble) {
        [errMgr internalError:tmodel.root.event.scope.st param1:@"bad highlight location" param2:ble];
    }
}

- (void) updateAttributes:(InstanceScope *)scope m:(STViewFrame *)m {
    [m.attributes setModel:[[[JTreeScopeStackModel alloc] init:scope] autorelease]];
    [m.attributes setRootVisible:NO];
    [m.attributes setShowsRootHandles:YES];
}

- (void) updateStack:(InstanceScope *)scope m:(STViewFrame *)m {
    NSMutableArray * stack = [Interpreter getEnclosingInstanceStack:scope param1:YES];
    [m setTitle:[[@"STViz - [" stringByAppendingString:[Misc join:[stack objectEnumerator] param1:@" "]] stringByAppendingString:@"]"]];
}

- (InterpEvent *) findEventAtOutputLocation:(NSMutableArray *)events charIndex:(int)charIndex {
    
    for (InterpEvent * e in events) {
        if (charIndex >= e.outputStartChar && charIndex <= e.outputStopChar)
            return e;
    }
    
    return nil;
}

+ (void) main:(NSArray *)args {
    if (args.length > 0 && [args[0] isEqualTo:@"1"])
        [self test1];
    else if (args.length > 0 && [args[0] isEqualTo:@"2"])
        [self test2];
    else if (args.length > 0 && [args[0] isEqualTo:@"3"])
        [self test3];
    else if (args.length > 0 && [args[0] isEqualTo:@"4"])
        [self test4];
}

+ (void) test1 {
    NSString * templates = @"method(type,name,locals,args,stats) ::= <<\npublic <type> <name>(<args:{a| int <a>}; separator=\", \">) {\n    <if(locals)>int locals[<locals>];<endif>\n    <stats;separator=\"\\n\">\n}\n>>\nassign(a,b) ::= \"<a> = <b>;\"\nreturn(x) ::= <<return <x>;>>\nparen(x) ::= \"(<x>)\"\n";
    NSString *tmpdir = @"/tmp";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup * group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    ST * st = [group getInstanceOf:@"method"];
    [st.impl dump];
    [st add:@"type" value:@"float"];
    [st add:@"name" value:@"foo"];
    [st add:@"locals" value:3];
    [st add:@"args" value:(NSObject *)[NSArray arrayWithObjects:@"x", @"y", @"z", nil]];
    ST * s1 = [group getInstanceOf:@"assign"];
    ST * paren = [group getInstanceOf:@"paren"];
    [paren add:@"x" value:@"x"];
    [s1 add:@"a" value:paren];
    [s1 add:@"b" value:@"y"];
    ST * s2 = [group getInstanceOf:@"assign"];
    [s2 add:@"a" value:@"y"];
    [s2 add:@"b" value:@"z"];
    ST * s3 = [group getInstanceOf:@"return"];
    [s3 add:@"x" value:@"3.14159"];
    [st add:@"stats" value:s1];
    [st add:@"stats" value:s2];
    [st add:@"stats" value:s3];
    STViz * viz = [st inspect];
    [System.out println:[st render]];
}

+ (void) test2 {
    NSString *templates = @"t1(q1=\"Some\\nText\") ::= <<\n<q1>\n>>\n\nt2(p1) ::= <<\n<p1>\n>>\n\nmain() ::= <<\nSTART-<t1()>-END\n\nSTART-<t2(p1=\"Some\\nText\")>-END\n>>\n";
    NSString *tmpdir = @"/tmp";
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    ST *st = [group getInstanceOf:@"main"];
    STViz * viz = [st inspect];
}

+ (void) test3 {
    NSString *templates = @"main() ::= <<\nFoo: <{bar};format=\"lower\">\n>>\n";
    NSString *tmpdir = [System getProperty:@"java.io.tmpdir"];
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile newSTGroupFile:[NSString stringWithFormat:@"%@/t.stg", tmpdir]] autorelease];
    ST *st = [group getInstanceOf:@"main"];
    [st inspect];
}

+ (void) test4 {
    NSString *templates = @"main(t) ::= <<\nhi: <t>\n>>\nfoo(x,y={hi}) ::= \"<bar(x,y)>\"\nbar(x,y) ::= << <y> >>\nignore(m) ::= \"<m>\"\n";
    STGroup *group = [[STGroupString newSTGroupString:templates] autorelease];
    ST *st = [group getInstanceOf:@"main"];
    ST *foo = [group getInstanceOf:@"foo"];
    [st add:@"t" value:foo];
    ST *ignore = [group getInstanceOf:@"ignore"];
    [ignore add:@"m" value:foo];
    [st inspect];
    [st render];
}

+ (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content {
    
    @try {
        File * f = [[[File alloc] init:dir param1:fileName] autorelease];
        if (![[f parentFile] exists])
            [[f parentFile] mkdirs];
        FileWriter * w = [[[FileWriter alloc] init:f] autorelease];
        BufferedWriter * bw = [[[BufferedWriter alloc] init:w] autorelease];
        [bw write:content];
        [bw close];
        [w close];
    }
    @catch (IOException * ioe) {
        [System.err println:@"can't write file"];
        [ioe printStackTrace:System.err];
    }
}

- (void) dealloc {
    [root release];
    [currentScope release];
    [allEvents release];
    [tmodel release];
    [errMgr release];
    [interp release];
    [output release];
    [trace release];
    [errors release];
    [viewFrame release];
    [super dealloc];
}

@end
