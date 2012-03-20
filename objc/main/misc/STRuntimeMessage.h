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
#import "ST.h"
#import "ErrorType.h"
#import "STMessage.h"

@class Interpreter;
@class InstanceScope;
/**
 * Used to track errors that occur in the ST interpreter.
 */

@interface STRuntimeMessage : STMessage {

	Interpreter *interp;
    /** Where error occurred in bytecode memory */
    NSInteger ip;
	InstanceScope *scope;
}

@property (retain) Interpreter *interp;
@property (assign, getter=getIp, setter=setIp:) NSInteger ip;
@property (retain) InstanceScope *scope;

+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho arg:(id)anArg;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg arg2:(id)anArg2;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e argN:(NSInteger)anArg arg2N:(NSInteger)anArg2;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg arg2:(id)anArg2 arg3:(id)anArg3;
+ (id) newMessage:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e argN:(NSInteger)anArg arg2:(id)anArg2 arg3N:(NSInteger)anArg3;

- (id) init:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e arg:(id)anArg arg2:(id)anArg2 arg3:(id)anArg3;

- (id) init:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e argN:(NSInteger)anArg arg2:(id)anArg2 arg3N:(NSInteger)anArg3;

- (id) init:(Interpreter *)interp error:(ErrorTypeEnum)anError ip:(NSInteger)anIp who:(ST *)aWho cause:(NSException *)e argN:(NSInteger)anArg arg2N:(NSInteger)anArg2;

- (NSString *) getSourceLocation;
- (NSString *) description;
- (NSString *) toString;

@end
