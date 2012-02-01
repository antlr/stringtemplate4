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
#import <ANTLR/ANTLR.h>

#import "ST.h"
#import "ErrorType.h"
#import "STErrorListener.h"
#import "STMessage.h"
#import "STLexerMessage.h"
#import "STRuntimeMessage.h"
#import "STGroupCompiletimeMessage.h"
#import "STCompiletimeMessage.h"

@interface ErrorManager_Anon1 : NSObject <STErrorListener> {
}

+ (id) newErrorManager_Anon1;
- (id) init;

- (void) compileTimeError:(STMessage *)msg;
- (void) runTimeError:(STMessage *)msg;
- (void) IOError:(STMessage *)msg;
- (void) internalError:(STMessage *)msg;
- (void) error:(NSString *)s;
- (void) error:(NSString *)s e:(NSException *)e;
@end

/**
 * Track errors per thread; e.g., one server transaction's errors
 * will go in one grouping since each has it's own thread.
 */

@interface ErrorManager : NSObject {
  id<STErrorListener> listener;
}

+ (void) initialize;
+ (id<STErrorListener>) DEFAULT_ERROR_LISTENER;
+ (id) DEFAULT_ERR_MGR;

+ (id) newErrorManager;
+ (id) newErrorManagerWithListener:(id<STErrorListener>)aListener;

- (id) init;
- (id) initWithListener:(id<STErrorListener>)listener;

- (void) dealloc;
- (void) compileTimeError:(ErrorTypeEnum)error templateToken:(STToken *)aTemplateToken t:(STToken *)t;
- (void) compileTimeError:(ErrorTypeEnum)error templateToken:(STToken *)aTemplateToken t:(STToken *)t arg:(id)arg;
- (void) compileTimeError:(ErrorTypeEnum)error templateToken:(STToken *)aTemplateToken t:(STToken *)t arg:(id)arg arg2:(id)arg2;
- (void) lexerError:(NSString *)srcName msg:(NSString *)msg templateToken:(STToken *)aTemplateToken e:(RecognitionException *)e;
- (void) groupSyntaxError:(ErrorTypeEnum)error srcName:(NSString *)srcName e:(RecognitionException *)e msg:(NSString *)msg;
- (void) groupLexerError:(ErrorTypeEnum)error srcName:(NSString *)srcName e:(RecognitionException *)e msg:(NSString *)msg;
- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)error;
- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)error arg:(id)arg;
- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)error e:(NSException *)e arg:(id)arg;
- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)error arg:(id)arg arg2:(id)arg2;
- (void) runTimeError:(Interpreter *)interp who:(ST *)aWho ip:(NSInteger)ip error:(ErrorTypeEnum)error arg:(id)arg arg2:(id)arg2 arg3:(id)arg3;
- (void) IOError:(ST *)who error:(ErrorTypeEnum)error e:(NSException *)e;
- (void) IOError:(ST *)who error:(ErrorTypeEnum)error e:(NSException *)e arg:(id)arg;
- (void) internalError:(ST *)who msg:(NSString *)msg e:(NSException *)e;

@property (retain) id<STErrorListener> listener;

@end
