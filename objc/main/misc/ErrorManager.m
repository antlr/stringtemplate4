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
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "ErrorManager.h"
#import "Misc.h"

@class ST;
@class CompiledST;

@implementation ErrorManager_Anon1 // Default Listener

+ (id) newErrorManager_Anon1
{
    return [[ErrorManager_Anon1 alloc] init];
}

- (id) init
{
    self=[super init];
    return self;
}

- (void) compileTimeError:(STMessage *)aMsg
{
    if (aMsg.error == SYNTAX_ERROR) {
        NSLog(@"%@", aMsg.arg );
    }
    else
        NSLog([ErrorType ErrorNum:aMsg.error], aMsg.arg);
}

- (void) runTimeError:(STMessage *)aMsg
{
    if (aMsg.error != NO_SUCH_PROPERTY) {
        NSLog( [ErrorType ErrorNum:aMsg.error], aMsg.arg, aMsg.arg2, aMsg.arg3);
    }
}

- (void) IOError:(STMessage *)aMsg
{
    if (aMsg.error != CANT_LOAD_GROUP_FILE) {
        NSLog(@"%@", [ErrorType ErrorNum:aMsg.error]);
    }
    else {
        NSLog([ErrorType ErrorNum:aMsg.error], aMsg.arg);
    }
}

- (void) internalError:(STMessage *)aMsg
{
    NSLog(@"%@", [ErrorType ErrorNum:aMsg.error]);
}

- (void) error:(NSString *)s
{
    [self error:s e:nil];
}

- (void) error:(NSString *)s e:(NSException *)e
{
    NSArray *cs;
    NSString *str;
    NSLog(@"%@", s);
    if (e != nil) {
#pragma mark error -- fix this
#ifdef DONTUSEYET
        [e printStackTrace:System.err];
#endif
        cs = [e callStackSymbols];
        for (int i=0; i < [cs count]; i++ ) {
            str = [cs objectAtIndex:i];
            NSLog( @"CallStack = %@\n", str );
        }
    }
}

@end


@implementation ErrorManager
static id<STErrorListener>DEFAULT_ERROR_LISTENER;
static ErrorManager *DEFAULT_ERR_MGR;

+ (void) initialize
{
    DEFAULT_ERROR_LISTENER = [ErrorManager_Anon1 newErrorManager_Anon1];
    DEFAULT_ERR_MGR = [ErrorManager newErrorManager];
}

+ (id<STErrorListener>) DEFAULT_ERROR_LISTENER
{
    return DEFAULT_ERROR_LISTENER;
}

+ (id) DEFAULT_ERR_MGR
{
    return DEFAULT_ERR_MGR;
}

+ (id) newErrorManager
{
    return [[ErrorManager alloc] init];
}

+ (id) newErrorManagerWithListener:(id<STErrorListener>)aListener
{
    return [[ErrorManager alloc] initWithListener:aListener];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        listener = DEFAULT_ERROR_LISTENER;
        if ( listener ) [listener retain];
    }
    return self;
}

- (id) initWithListener:(id<STErrorListener>)aListener
{
    self=[super init];
    if ( self != nil ) {
        listener = aListener;
        if ( listener ) [listener retain];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in ErrorManager" );
#endif
    if ( listener ) [listener release];
    [super dealloc];
}

- (void) compileTimeError:(ErrorTypeEnum)anError templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t
{
    NSString *srcName = [t.input getSourceName];
    if (srcName != nil)
        srcName = [Misc getFileName:srcName];
    [listener compileTimeError:[STCompiletimeMessage newMessage:anError srcName:srcName templateToken:aTemplateToken t:t cause:nil arg:t.text]];
}

- (void) compileTimeError:(ErrorTypeEnum)anError templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t arg:(id)arg
{
    NSString *srcName = [t.input getSourceName];
    srcName = [Misc getFileName:srcName];
    [listener compileTimeError:[STCompiletimeMessage newMessage:anError srcName:srcName templateToken:aTemplateToken t:t cause:nil arg:arg]];
}

- (void) compileTimeError:(ErrorTypeEnum)anError templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t arg:(id)arg arg2:(id)arg2
{
    NSString *srcName = [t.input getSourceName];
    if (srcName != nil)
        srcName = [Misc getFileName:srcName];
    [listener compileTimeError:[STCompiletimeMessage newMessage:anError srcName:srcName templateToken:aTemplateToken t:t cause:nil arg:arg arg2:arg2]];
}

- (void) compileTimeError:(ErrorTypeEnum)anError templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t argN:(NSInteger)arg arg2N:(NSInteger)arg2
{
    NSString *srcName = [t.input getSourceName];
    if (srcName != nil)
        srcName = [Misc getFileName:srcName];
    [listener compileTimeError:[STCompiletimeMessage newMessage:anError srcName:srcName templateToken:aTemplateToken t:t cause:nil argN:arg arg2N:arg2]];
}

- (void) lexerError:(NSString *)srcName msg:(NSString *)aMsg templateToken:(CommonToken *)aTemplateToken e:(RecognitionException *)e
{
    [listener compileTimeError:[STLexerMessage newMessage:srcName msg:aMsg templateToken:aTemplateToken cause:e]];
}

- (void) groupSyntaxError:(ErrorTypeEnum)anError srcName:(NSString *)srcName e:(RecognitionException *)e msg:(NSString *)aMsg
{
    [listener compileTimeError:[STGroupCompiletimeMessage newMessage:anError srcName:srcName t:e.token cause:e arg:aMsg]];
}

- (void) groupLexerError:(ErrorTypeEnum)anError srcName:(NSString *)srcName e:(RecognitionException *)e msg:(NSString *)aMsg
{
    [listener compileTimeError:[STGroupCompiletimeMessage newMessage:anError srcName:srcName t:e.token cause:e arg:aMsg]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError arg:(id)arg
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho arg:arg]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError e:(NSException *)e arg:(id)arg
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho cause:e arg:arg]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError arg:(id)arg arg2:(id)arg2
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho cause:nil arg:arg arg2:arg2]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError argN:(NSInteger)arg arg2N:(NSInteger)arg2
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho cause:nil argN:arg arg2N:arg2]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError arg:(id)arg arg2:(id)arg2 arg3:(id)arg3
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho cause:nil arg:arg arg2:arg2 arg3:arg3]];
}

- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)anError argN:(NSInteger)arg arg2:(id)arg2 arg3N:(NSInteger)arg3
{
    [listener runTimeError:[STRuntimeMessage newMessage:interp error:anError ip:ip who:aWho cause:nil argN:arg arg2:arg2 arg3N:arg3]];
}

- (void) IOError:(ST *)aWho error:(ErrorTypeEnum)anError e:(NSException *)e
{
    [listener IOError:[STMessage newMessage:anError who:aWho cause:e]];
}

- (void) IOError:(ST *)aWho error:(ErrorTypeEnum)anError e:(NSException *)e arg:(id)arg
{
    [listener IOError:[STMessage newMessage:anError who:aWho cause:e arg:arg]];
}

- (void) internalError:(ST *)aWho msg:(NSString *)aMsg e:(NSException *)e
{
    [listener internalError:[STMessage newMessage:INTERNAL_ERROR who:aWho cause:e arg:aMsg]];
}

@synthesize listener;
@end
