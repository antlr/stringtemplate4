#import "JTreeSTModel.h"

@implementation Wrapper

- (Wrapper *) newWrapper:(EvalTemplateEvent *)anEvent
{
    return [[Wrapper alloc] initWithEvent:anEvent
}

- (id) initWithEvent:(EvalTemplateEvent *)anEvent
{
    self = [super init];
    if ( self ) {
        event = anEvent;
    }
    return self;
}

- (int) hash
{
    return [event hash];
}

- (BOOL) isEqualTo:(id)obj
{
    return event == ((Wrapper *)obj).event;
}

- (NSString *) description
{
    ST *st = event.scope.st;
    if ([st anonSubtemplate])
        return @"{...}";
    if (st.debugState != nil && st.debugState.newSTEvent != nil) {
        return [NSString stringWithFormat:@"%@ @ %@:%@", [st description], [st.debugState.newSTEvent fileName], [st.debugState.newSTEvent line]];
    }
     else {
        return [st description];
    }
}

- (void) dealloc
{
    [event release];
    [super dealloc];
}

@end

@implementation JTreeSTModel

@synthesize root;

- (id) init:(Interpreter *)interp root:(EvalTemplateEvent *)root
{
    self = [super init];
    if ( self ) {
        interp = interp;
        root = [[[Wrapper alloc] init:root] autorelease];
    }
    return self;
}

- (NSObject *) getChild:(id)parent index:(int)index
{
    EvalTemplateEvent *e = ((Wrapper *)parent).event;
    return [[[Wrapper alloc] init:[e.scope.childEvalTemplateEvents get:index]] autorelease];
}

- (int) getChildCount:(id)parent
{
    EvalTemplateEvent * e = ((Wrapper *)parent).event;
    return [e.scope.childEvalTemplateEvents size];
}

- (NSInteger) getIndexOfChild:(id)parent child:(id)child
{
    EvalTemplateEvent *p = ((Wrapper *)parent).event;
    EvalTemplateEvent *c = ((Wrapper *)parent).event;
    int i = 0;

    for (EvalTemplateEvent *e in p.scope.childEvalTemplateEvents) {
        if (e.scope.st == c.scope.st) {
            return i;
        }
        i++;
    }
    return -1;
}

- (BOOL) isLeaf:(id)node
{
    return [self getChildCount:node] == 0;
}

- (void) valueForPathChanged:(TreePath *)treePath obj:(id)obj
{
}

- (void) addTreeModelListener:(TreeModelListener *)treeModelListener
{
}

- (void) removeTreeModelListener:(TreeModelListener *)treeModelListener
{
}

- (void) dealloc
{
    [interp release];
    [root release];
    [super dealloc];
}

@end
