#import "STViz.h"

@implementation STViz_Anon1

- (void) valueChanged:(TreeSelectionEvent *)treeSelectionEvent {
    currentST = ((Wrapper *)[m.tree lastSelectedPathComponent]).st;
    [self updateCurrentST:m];
}

@end

@implementation STViz_Anon2

- (void) valueChanged:(TreeSelectionEvent *)treeSelectionEvent {
    CommonTree *node = (CommonTree *)[[treeSelectionEvent newLeadSelectionPath] lastPathComponent];
    NSLog([@"select AST: " stringByAppendingString:node]);
    CommonToken *a = (CommonToken *)[currentST.impl.tokens get:[node tokenStartIndex]];
    CommonToken *b = (CommonToken *)[currentST.impl.tokens get:[node tokenStopIndex]];
    [self highlight:m.template param1:[a startIndex] param2:[b stopIndex]];
}

@end

@implementation STViz_Anon3

- (void) valueChanged:(ListSelectionEvent *)e {
    NSInteger minIndex = [m.attributes minSelectionIndex];
    NSInteger maxIndex = [m.attributes maxSelectionIndex];
    
    for (NSInteger i = minIndex; i <= maxIndex; i++) {
        if ([m.attributes isSelectedIndex:i]) {
        }
    }
    
}

@end

@implementation STViz_Anon4

- (void) caretUpdate:(CaretEvent *)e {
    NSInteger dot = [e dot];
    InterpEvent *de = [self findEventAtOutputLocation:allEvents param1:dot];
    if (de == nil)
        currentST = tmodel.root.st;
    else
        currentST = de.self;
    [self updateCurrentST:m];
}

@end

@implementation STViz_Anon5

- (void) valueChanged:(ListSelectionEvent *)e {
    NSInteger minIndex = [m.errorList minSelectionIndex];
    NSInteger maxIndex = [m.errorList maxSelectionIndex];
    NSInteger i = minIndex;
    
    while (i <= maxIndex) {
        if ([m.errorList isSelectedIndex:i])
            break;
        i++;
    }
    
    ListModel *model = [m.errorList model];
    STMessage *msg = (STMessage *)[model getElementAt:i];
    if ([msg conformsToProtocol:@protocol(STRuntimeMessage)]) {
        STRuntimeMessage *rmsg = (STRuntimeMessage *)msg;
        Interval *I = rmsg.self.impl.sourceMap[rmsg.ip];
        currentST = (DebugST *)msg.self;
        [self updateCurrentST:m];
        if (I != nil) {
            [self highlight:m.template param1:I.a param2:I.b];
        }
    }
}

@end

@implementation STViz

- (id) init:(ErrorManager *)errMgr root:(DebugST *)root output:(NSString *)output interp:(Interpreter *)interp trace:(AMutableArray *)trace errors:(AMutableArray *)errors {
    if ( (self=[super init]) != nil ) {
        errMgr = errMgr;
        currentST = root;
        interp = interp;
        allEvents = [interp events];
        errors = errors;
        STViewFrame *m = [[STViewFrame alloc] init];
        [self updateStack:currentST m:m];
        [self updateAttributes:currentST m:m];
        tmodel = [[JTreeSTModel alloc] init:interp param1:currentST];
        [m.tree setModel:tmodel];
        [m.tree addTreeSelectionListener:[[STViz_Anon1 alloc] init]];
        JTreeASTModel *astModel = [[JTreeASTModel alloc] init:[[CommonTreeAdaptor alloc] init] param1:currentST.impl.ast];
        [m.ast setModel:astModel];
        [m.ast addTreeSelectionListener:[[STViz_Anon2 alloc] init]];
        [m.attributes addListSelectionListener:[[STViz_Anon3 alloc] init]];
        [m.output setText:output];
        [m.template setText:currentST.impl.template];
        [m.bytecode setText:[currentST.impl disasm]];
        [m.trace setText:[Misc join:[trace objectEnumerator] param1:@"\n"]];
        CaretListener *caretListenerLabel = [[STViz_Anon4 alloc] init];
        [m.output addCaretListener:caretListenerLabel];
        [m setDefaultCloseOperation:JFrame.EXIT_ON_CLOSE];
        [m pack];
        [m setSize:800 param1:600];
        [m.topSplitPane setBorder:nil];
        [m.overallSplitPane setBorder:nil];
        if (errors == nil || [errors count] == 0) {
            [m.errorScrollPane setVisible:NO];
        }
        else {
            DefaultListModel *errorListModel = [[DefaultListModel alloc] init];
            
            for (STMessage *msg in errors) {
                [errorListModel addElement:msg];
            }
            
            [m.errorList setModel:errorListModel];
        }
        [m.errorList addListSelectionListener:[[STViz_Anon5 alloc] init]];
        [m.bottomSplitPane setBorder:nil];
        [m.treeScrollPane setPreferredSize:[[Dimension alloc] init:120 param1:400]];
        [m.bottomSplitPane setPreferredSize:[[Dimension alloc] init:120 param1:200]];
        [m setVisible:YES];
    }
    return self;
}

- (void) updateCurrentST:(STViewFrame *)m {
    [self updateStack:currentST m:m];
    [self updateAttributes:currentST m:m];
    [m.bytecode setText:[currentST.impl disasm]];
    JTreeASTModel *astModel = [[JTreeASTModel alloc] init:[[CommonTreeAdaptor alloc] init] param1:currentST.impl.ast];
    [m.ast setModel:astModel];
    AMutableArray *pathST = [Interpreter getEnclosingInstanceStack:currentScope topDown:YES];
    NSArray *path = [NSArray array];
    NSInteger j = 0;
    
    for (ST *s in pathST)
        path[j++] = [[Wrapper alloc] init:(DebugST *)s];
    
    [m.tree setSelectionPath:[[TreePath alloc] init:path]];
    [m.template setText:currentST.impl.template];
    Interval *r = [currentST.impl getTemplateRange];
    if (currentST.enclosingInstance != nil) {
        NSInteger i = [tmodel getIndexOfChild:(DebugST *)currentST.enclosingInstance param1:currentST];
        InterpEvent *e = [[interp getEvents:currentST.enclosingInstance] get:i];
        if ([e conformsToProtocol:@protocol(EvalTemplateEvent)]) {
            if ([currentST anonSubtemplate]) {
                [self highlight:m.template i:r.a j:r.b];
            }
            [self highlight:m.output i:e.start j:e.stop];
        }
    }
    else {
        [self highlight:m.output i:r.a j:r.b];
    }
}

- (void) highlight:(JTextComponent *)comp i:(NSInteger)i j:(NSInteger)j {
    Highlighter *highlighter = [comp highlighter];
    [highlighter removeAllHighlights];
    
    @try {
        [highlighter addHighlight:i param1:j + 1 param2:DefaultHighlighter.DefaultPainter];
    }
    @catch (BadLocationException *ble) {
        [errMgr internalError:tmodel.root.st param1:@"bad highlight location" param2:ble];
    }
}

- (void) updateAttributes:(DebugST *)st m:(STViewFrame *)m {
    DefaultListModel *attrModel = [[DefaultListModel alloc] init];
    LinkedHashMap *attrs = [st attributes];
    if (attrs != nil) {
        
//        for (NSString *a in [attrs allKeys]) {
        NSString *a;
        ArrayIterator *it = (ArrayIterator *)[ArrayIterator newIteratorForDictKey:table];
        while ( [it hasNext] ) {
            a = (NSString *)[it nextObject];
            if (st.addAttrEvents != nil) {
                AMutableArray *events = [st.addAttrEvents get:a];
                NSMutableString *locations = [NSMutableString stringWithCapacity:16];
                NSInteger i = 0;
                if (events != nil) {
                    
                    for (AddAttributeEvent *ae in events) {
                        if (i > 0)
                            [locations appendString:@", "];
                        [locations appendString:[[ae fileName] stringByAppendingString:@":"] + [ae line]];
                        i++;
                    }
                    
                }
                [attrModel addElement:[[a stringByAppendingString:@" = "] + [attrs get:a] stringByAppendingString:@" @ "] + [locations description]];
            }
            else {
                [attrModel addElement:[a stringByAppendingString:@" = "] + [attrs get:a]];
            }
        }
        
    }
    [m.attributes setModel:attrModel];
}

- (void) updateStack:(DebugST *)st m:(STViewFrame *)m {
    AMutableArray *stack = [Interpreter getEnclosingInstanceStack:currentScope topDown:YES];
    [m setTitle:[[@"STViz - [" stringByAppendingString:[Misc join:[stack objectEnumerator] param1:@" "]] stringByAppendingString:@"]"]];
}

- (InterpEvent *) findEventAtOutputLocation:(AMutableArray *)events charIndex:(NSInteger)charIndex {
    
    for (InterpEvent *e in events) {
        if (charIndex >= e.start && charIndex <= e.stop)
            return e;
    }
    
    return nil;
}

+ (void) main:(NSArray *)args {
    NSString *templates = @"method(type,name,locals,args,stats) ::= <<\n<type> <ick()> <name>(<args:{a| NSInteger <a>}; separator=\", \">) {\n    <if(locals)>NSInteger locals[<locals>];<endif>\n    <stats;separator=\"\\n\">\n}\n>>\nassign(a,b) ::= \"<a> = <b>;\"\nreturn(x) ::= <<return <x>;>>\nparen(x) ::= \"(<x>)\"\n";
    NSString *tmpdir = [System getProperty:@"java.io.tmpdir"];
    [self writeFile:tmpdir fileName:@"t.stg" content:templates];
    STGroup *group = [[STGroupFile alloc] init:[[tmpdir stringByAppendingString:@"/"] stringByAppendingString:@"t.stg"]];
    STGroup.debug = YES;
    ST *st = [group getInstanceOf:@"method"];
    [st.impl dump];
    [st add:@"type" value:@"float"];
    [st add:@"name" value:@"foo"];
    [st add:@"locals" value:3];
    [st add:@"args" value:[NSArray arrayWithObjects:@"x", @"y", @"z", nil]];
    ST *s1 = [group getInstanceOf:@"assign"];
    ST *paren = [group getInstanceOf:@"paren"];
    [paren add:@"x" value:@"x"];
    [s1 add:@"a" value:paren];
    [s1 add:@"b" value:@"y"];
    ST *s2 = [group getInstanceOf:@"assign"];
    [s2 add:@"a" value:@"y"];
    [s2 add:@"b" value:@"z"];
    ST *s3 = [group getInstanceOf:@"return"];
    [s3 add:@"x" value:@"3.14159"];
    [st add:@"stats" value:s1];
    [st add:@"stats" value:s2];
    [st add:@"stats" value:s3];
    [((DebugST *)st) inspect];
    [st render];
}

+ (void) writeFile:(NSString *)dir fileName:(NSString *)fileName content:(NSString *)content {
    
    @try {
        File *f = [[File alloc] init:dir param1:fileName];
        if (![[f parentFile] exists])
            [[f parentFile] mkdirs];
        FileWriter *w = [[FileWriter alloc] init:f];
        BufferedWriter *bw = [[BufferedWriter alloc] init:w];
        [bw write:content];
        [bw close];
        [w close];
    }
    @catch (IOException *ioe) {
        [System.err println:@"can't write file"];
        [ioe printStackTrace:System.err];
    }
}

- (void) dealloc {
    [currentST release];
    [allEvents release];
    [tmodel release];
    [errors release];
    [errMgr release];
    [interp release];
    [super dealloc];
}

@end
