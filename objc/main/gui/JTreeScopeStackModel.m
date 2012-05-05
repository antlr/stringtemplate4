#import "JTreeScopeStackModel.h"

@implementation StringTree

@synthesize nil;

- (id) initWithText:(NSString *)text
{
    if (self = [super init]) {
        text = text;
    }
    return self;
}

- (BOOL) hasText
{
    return text == nil;
}

- (NSString *) description
{
    if (![self nil])
        return [text description];
    return @"nil";
}

- (void) dealloc
{
    [text release];
    [super dealloc];
}

@end

@implementation JTreeScopeStackModel

@synthesize root;

+ (JTreeScopeStackModel *)newModel:(InstanceScope *)aScope
{
        return [[JTreeScopeStackModel alloc] initWithScope:aScope
}

- (id) initWithScope:(InstanceScope *)aScope
{
    if (self = [super init]) {
        root = [[StringTree newStringTree:@"Scope stack:"] autorelease];
        AMutableArray *stack = [Interpreter getScopeStack:scope param1:YES];

        for (InstanceScope *s in stack) {
            StringTree *templateNode = [[StringTree newStringTree:[s.st name]] autorelease];
            [root addChild:templateNode];
            [self addAttributeDescriptions:s.st node:templateNode];
        }
    }
    return self;
}

- (void) addAttributeDescriptions:(ST *)st node:(StringTree *)node
{
    LinkedHashMap *attrs = [st attributes];
    if (attrs == nil)
        return;

    for (NSString *a in [[attrs keySet] toArray]) {
        NSString *descr = nil;
        if (st.debugState != nil && st.debugState.addAttrEvents != nil) {
            NSMutableArray *events = [st.debugState.addAttrEvents get:a];
            NSMutableString *locations = [[NSMutableString stringWithCapacity:16] autorelease];
            int i = 0;
            if ( events != nil ) {

                for (AddAttributeEvent *ae in events) {
                    if (i > 0)
                        [locations append:@", "];
                    [locations append:[[ae fileName] stringByAppendingString:@":"] + [ae line]];
                    i++;
                }

            }
            if ([locations length] > 0) {
                descr = [[a stringByAppendingString:@" = "] + [attrs get:a] stringByAppendingString:@" @ "] + [locations description];
            }
             else {
                descr = [a stringByAppendingString:@" = "] + [attrs get:a];
            }
        }
         else {
            descr = [a stringByAppendingString:@" = "] + [attrs get:a];
        }
        [node addChild:[StringTree newStringTree:descr]];
    }

}

- (id) getChild:(id)parent index:(int)i
{
    StringTree *t = (StringTree *)parent;
    return [t getChild:i];
}

- (int) getChildCount:(id)parent
{
    StringTree *t = (StringTree *)parent;
    return [t childCount];
}

- (BOOL) isLeaf:(id)node
{
    return [self getChildCount:node] == 0;
}

- (int) getIndexOfChild:(id)parent child:(id)child
{
    StringTree *c = (StringTree *)child;
    return [c childIndex];
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
    [root release];
    [super dealloc];
}

@end
