#import "JTreeASTModel.h"

@implementation JTreeASTModel

@synthesize root;

- (id) init:(CommonTreeAdaptor *)anAdaptor root:(id)theRoot
{
    self=[super init];
    if ( self ) {
        adaptor = anAdaptor;
        root = theRoot;
    }
    return self;
}

- (id) initWithRoot:(id)theRoot
{
    self=[super init];
    if ( self ) {
        adaptor = [[CommonTreeAdaptor alloc] init];
        root = theRoot;
    }
    return self;
}

- (NSInteger) getChildCount:(id)parent
{
    return [adaptor getChildCount:parent];
}

- (NSInteger) getIndexOfChild:(id)parent child:(id)child
{
    if (parent == nil)
        return -1;
    return [adaptor getChildIndex:child];
}

- (id) getChild:(id)parent index:(NSInteger)index
{
    return [adaptor getChild:parent param1:index];
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
    [adaptor release];
    [root release];
    [super dealloc];
}

@end
