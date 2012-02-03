/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#import "CompiledST.h"
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "ST.h"
#import "STGroup.h"
#import "Interval.h"
#import "Misc.h"
#import "PrintWriter.h"
#import "StringWriter.h"
#import "BytecodeDisassembler.h"
#import "GroupParser.h"
#import "Bytecode.h"

@implementation CompiledST

@synthesize name;
@synthesize prefix;
@synthesize template;
@synthesize templateDefStartToken;
@synthesize tokens;
@synthesize ast;
@synthesize formalArguments;
@synthesize hasFormalArgs;
@synthesize numberOfArgsWithDefaultValues;
@synthesize implicitlyDefinedTemplates;
//@synthesize nativeGroup;
@synthesize isRegion;
@synthesize regionDefType;
@synthesize isAnonSubtemplate;
@synthesize strings;
@synthesize instrs;
@synthesize codeSize;
@synthesize sourceMap;

+ (CompiledST *) newCompiledST
{
    return [[CompiledST alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        prefix = @"/";
        nativeGroup = STGroup.defaultGroup;
        instrs = [[MemBuffer newMemBufferWithLen:30] retain];
        sourceMap = [[AMutableArray arrayWithCapacity:6] retain];
        strings = [[AMutableArray arrayWithCapacity:5] retain];
        template = @"";
        [template retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in CompiledST" );
#endif
    if ( name ) [name release];
    if ( template ) [template release];
    if ( tokens ) [tokens release];
    if ( ast ) [ast release];
    if ( formalArguments ) [formalArguments release];
    if ( implicitlyDefinedTemplates ) [implicitlyDefinedTemplates release];
    nativeGroup = nil;
    if ( strings ) [strings release];
    if ( instrs ) [instrs release];
    if ( sourceMap ) [sourceMap release];
    [super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    CompiledST *copy;
    
    copy = [[[self class] allocWithZone:aZone] init];
    if ( instrs ) {
        [copy.instrs release];
        copy.instrs = [instrs copyWithZone:aZone];
    }
    if ( sourceMap ) {
        [copy.sourceMap release];
        copy.sourceMap = [sourceMap copyWithZone:aZone];
    }
    if ( strings ) {
        [copy.strings release];
        copy.strings = [strings copyWithZone:aZone];
    }
    return copy;
}

- (void) addImplicitlyDefinedTemplate:(CompiledST *)sub
{
    sub.prefix = self.prefix;
    if ( [sub.name characterAtIndex:0] != '/' ) {
        sub.name = [NSString stringWithFormat:@"\%@", sub.name];
    }
    if ( implicitlyDefinedTemplates == nil ) {
        implicitlyDefinedTemplates = [[AMutableArray arrayWithCapacity:5] retain];
    }
    [implicitlyDefinedTemplates addObject:sub];
}

- (void) defineArgDefaultValueTemplates:(STGroup *)group
{
    if ( formalArguments == nil )
        return;
//    for (NSString *s in [formalArguments allKeys]) {
    NSString *s;
    ArrayIterator *it = (ArrayIterator *)[formalArguments keyEnumerator];
    while ( [it hasNext] ) {
        s = (NSString *)[it nextObject];
        FormalArgument *fa = [formalArguments objectForKey:s];
        if (fa.defaultValueToken != nil) {
            numberOfArgsWithDefaultValues++;
            if ( fa.defaultValueToken.type == ANONYMOUS_TEMPLATE ) {
                NSString *argSTname = [NSString stringWithFormat:@"%@_default_value", fa.name];
                Compiler *c2 = [Compiler newCompiler:group];
                NSString *defArgTemplate = [Misc strip:fa.defaultValueToken.text n:1];
                fa.compiledDefaultValue = [c2 compile:[nativeGroup getFileName] name:argSTname
                       args:nil template:defArgTemplate templateToken:fa.defaultValueToken];
                fa.compiledDefaultValue.name = argSTname;
				[fa.compiledDefaultValue defineImplicitlyDefinedTemplates:group];
            }
            else if ( fa.defaultValueToken.type == T_STRING ) {
                fa.defaultValue = [Misc strip:fa.defaultValueToken.text n:1];
            }
            else {
                fa.defaultValue = (id)((fa.defaultValueToken.type == GroupParser.TTRUE)? YES : NO);
            }
        }
    }
}

- (void) defineFormalArgs:(AMutableArray *)args
{
    hasFormalArgs = YES;
    if ( args == nil )
        formalArguments = nil;
    else {
        __strong FormalArgument *a;
//        for (FormalArgument *a in args)
//            [self addArg:a];
        ArrayIterator *it = [args objectEnumerator];
        while ( [it hasNext] ) {
            a = [it nextObject];
            [self addArg:a];
        }
    }
}

/**
 *Used by ST.add() to add args one by one w/o turning on full formal args definition signal
 */
- (void) addArg:(FormalArgument *)a
{
    if (formalArguments == nil) {
        formalArguments = [[AMutableDictionary dictionaryWithCapacity:16] retain];
    }
    a.index = [formalArguments count];
    [formalArguments setObject:a forKey:a.name];
}

- (void) defineImplicitlyDefinedTemplates:(STGroup *)group
{
    if (implicitlyDefinedTemplates != nil) {
//        for (CompiledST *sub in implicitlyDefinedTemplates) {
        CompiledST *sub;
        ArrayIterator *it = [ArrayIterator newIterator:implicitlyDefinedTemplates];
        while ( [it hasNext] ) {
            sub = (CompiledST *)[it nextObject];
            [group rawDefineTemplate:sub.name code:sub defT:sub.templateDefStartToken];
            [sub defineImplicitlyDefinedTemplates:group];
        }
        
    }
}

- (NSString *) getTemplateSource
{
    Interval *r = [self getTemplateRange];
    return [template substringWithRange:NSMakeRange(r.a, (r.b + 1)-r.a)];
}

- (Interval *) getTemplateRange
{
    if (isAnonSubtemplate) {
        Interval *aStart = [sourceMap objectAtIndex:0];
        Interval *aStop = nil;
        
        for (NSInteger i = [sourceMap count] - 1; i >= 0; i--) {
            Interval *I = (Interval *)[sourceMap objectAtIndex:i];
            if (I != nil) {
                aStop = I;
                break;
            }
        }
        return [Interval newInterval:aStart.a b:aStop.b];
    }
    return [Interval newInterval:0 b:[template length] - 1];
}

- (NSString *) dis_instrs
{
    BytecodeDisassembler *dis = [BytecodeDisassembler newBytecodeDisassembler:self];
    return [dis instrs];
}

- (void) dump
{
    NSString *tmp;

    BytecodeDisassembler *dis = [BytecodeDisassembler newBytecodeDisassembler:self];
    NSLog( @"%@:%@", name, [dis disassemble] );
    NSLog( @"Strings:%@", [dis strings] );
    tmp = [dis sourceMap];
    NSLog( @"Bytecode to template map:%@", ((tmp != nil)?tmp:@"[dis sourceMap] returned nil") );
}

- (NSString *) disasm
{
    NSString *tmp;

//    BytecodeDisassembler *dis = [[BytecodeDisassembler alloc] initWithCode:self];
    BytecodeDisassembler *dis = [BytecodeDisassembler newBytecodeDisassembler:self];
    StringWriter *sw = [StringWriter newWriter];
    PrintWriter *pw = [[PrintWriter newWriterWithWriter:sw] retain];
    [pw println:[dis disassemble]];
    [pw println:[NSString stringWithFormat:@"Strings:%@", [dis strings]]];
    tmp = [dis sourceMap];
    [pw println:[NSString stringWithFormat:@"Bytecode to template map:%@", (tmp!=nil?tmp:@"[dis sourceMap] returned nil")]];
    [pw close];
    [pw release];
    return [sw toString];
}

- (STGroup *)nativeGroup
{
    if (nativeGroup == nil)
        nativeGroup = STGroup.defaultGroup;
    return nativeGroup;
}

- (void) setNativeGroup:(STGroup *)aNativeGroup
{
    if ( nativeGroup != aNativeGroup ) {
        if ( nativeGroup ) [nativeGroup release];
        if ( aNativeGroup ) [aNativeGroup retain];
    }
    nativeGroup = aNativeGroup;
}
   
@end
