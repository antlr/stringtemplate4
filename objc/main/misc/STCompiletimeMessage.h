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
#import "STMessage.h"

/**
 * Used for semantic errors that occur at compile time not during
 * interpretation. For ST parsing ONLY not group parsing.
 */

@interface STCompiletimeMessage : STMessage {
  __strong CommonToken *templateToken;
  __strong CommonToken *token;
  __strong NSString *srcName;
}

+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t;
+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause;
+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg;
+ (id) newMessage:(ErrorTypeEnum)anError srcName:(NSString *)aSrcName templateToken:(CommonToken *)aTemplateToken t:(CommonToken *)t cause:(NSException *)aCause arg:(id)anArg arg2:(id)anArg2;

- (id) init:(ErrorTypeEnum)error srcName:(NSString *)srcName templateToken:(CommonToken *)templateToken t:(CommonToken *)t cause:(NSException *)aCause arg:(id)arg arg2:(id)arg2;
- (NSString *) description;
- (NSString *) toString;

@property (retain) CommonToken *templateToken;
@property (retain) CommonToken *token;
@property (retain) NSString *srcName;
@end
