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
/**
 * Generic StringTemplate output writer filter.
 * 
 * Literals and the elements of expressions are emitted via write().
 * Separators are emitted via writeSeparator() because they must be
 * handled specially when wrapping lines (we don't want to wrap
 * in between an element and it's separator).
 */

@protocol STWriter <NSObject>
+ (NSInteger) NO_WRAP;
- (void) pushIndentation:(NSString *)indent;
- (NSString *) popIndentation;
- (void) pushAnchorPoint;
- (void) popAnchorPoint;
- (NSInteger) write:(char)c;
- (NSInteger) writeStr:(NSString *)str;
- (NSInteger) write:(NSString *)str wrap:(NSString *)wrap;
- (NSInteger) writeWrap:(NSString *)wrap;
- (NSInteger) writeSeparator:(NSString *)str;
- (NSInteger) index;
- (void) close;
- (id) copyWithZone:(NSZone *)aZone;
- (void) print:(id)msg;
- (void) println:(id)msg;

@end
