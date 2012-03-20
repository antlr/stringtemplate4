#import "JTreeSTModel.h"

@implementation Wrapper

- (id) initWithSt:(DebugST *)aST
{
    if ( (self=[super init]) != nil ) {
        st = aST;
    }
    return self;
}

- (BOOL) isEqualTo:(id)obj
{
    if (self == obj)
        return YES;
    if (obj == nil || [self class] != [obj class])
        return NO;
    Wrapper *wrapper = (Wrapper *)obj;
    if (st != nil ? ![st isEqualTo:wrapper.st] : wrapper.st != nil)
        return NO;
    return YES;
}

- (NSInteger) hash
{
    return (st != nil ? [st hash] : 0);
}

- (NSString *) description
{
    if ([st anonSubtemplate])
        return @"{...}";
    return [NSString stringWithFormat:@"%@ %@:%@", [st description], [st.newSTEvent fileName], [st.newSTEvent line]];
}

- (NSString *) toString
{
    return [self description];
}

- (void) dealloc {
    [st release];
    [super dealloc];
}

@end

@implementation JTreeSTModel

@synthesize root;

- (id) init:(Interpreter *)interp root:(DebugST *)root {
    if ( (self=[super init]) != nil ) {
        interp = interp;
        root = [[Wrapper alloc] init:root];
    }
    return self;
}

- (NSInteger) getChildCount:(id)parent {
    DebugST *st = ((Wrapper *)parent).st;
    return [[interp getEvents:st] size];
}

- (NSInteger) getIndexOfChildDBG:(DebugST *)parent child:(DebugST *)child {
    return [self getIndexOfChild:[[Wrapper alloc] init:parent] child:[[Wrapper alloc] init:child]];
}

- (NSInteger) getIndexOfChild:(id)parent child:(id)child {
    if (parent == nil)
        return -1;
    DebugST *parentST = ((Wrapper *)parent).st;
    DebugST *childST = ((Wrapper *)child).st;
    NSInteger i = 0;
    ArrayIterator *it = [[interp getEvents:parentST] objectEnumerator];
    InterpEvent *e;
//    for (InterpEvent *e in [interp getEvents:parentST]) {
    while ( [it hasNext] ) {
        e = (InterpEvent *)[it nextObject];
        if (e.self == childST)
            return i;
        i++;
    }
    
    return -1;
}

- (id) getChild:(id)parent index:(NSInteger)index {
    DebugST *st = ((Wrapper *)parent).st;
    return [[Wrapper alloc] init:[[interp getEvents:st] objectAtIndex:index].who];
}

- (BOOL) isLeaf:(id)node {
    return [self getChildCount:node] == 0;
}

- (void) valueForPathChanged:(TreePath *)treePath obj:(id)obj {
}

- (void) addTreeModelListener:(TreeModelListener *)treeModelListener {
}

- (void) removeTreeModelListener:(TreeModelListener *)treeModelListener {
}

- (void) dealloc {
    [root release];
    [interp release];
    [super dealloc];
}

@end
