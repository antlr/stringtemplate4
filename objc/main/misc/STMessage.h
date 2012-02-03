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
#import <Cocoa/Cocoa.h>
#import "ErrorType.h"
#import <ANTLR/CommonToken.h>

@class ST;

/**
 * Upon error, ST creates an STMessage or subclass instance and notifies
 * the listener.  This root class is used for IO and internal errors.
 * 
 * @see STRuntimeMessage
 * @see STCompiletimeMessage
 */

@interface STMessage : NSObject {

  /**
   * if in debug mode, has created instance, add attr events and eval
   * template events.
 */
  ST *who;
  ErrorTypeEnum error;
  id arg;
  id arg2;
  id arg3;
  NSException *cause;
}

+ (id) newMessage:(ErrorTypeEnum)anError;
+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho;
+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause;
+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg;
+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause where:(CommonToken *)where  arg:(id)arg;
+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2;
+ (id) newMessage:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2 arg3:(id)arg3;

#ifdef DONTUSENOMO
- (id) init:(ErrorTypeEnum)anError;
- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho;
- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause;
- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg;
- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause where:(CommonToken *)where arg:(id)arg;
- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2;
#endif
- (id) init:(ErrorTypeEnum)anError who:(ST *)aWho cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2 arg3:(id)arg3;

- (void) dealloc;
- (NSString *) description;
- (NSString *) toString;

@property (retain) ST *who;
@property (assign) ErrorTypeEnum error;
@property (retain) id arg;
@property (retain) id arg2;
@property (retain) id arg3;
@property (retain) NSException *cause;

@end
