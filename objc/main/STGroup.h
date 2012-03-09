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
#import "STErrorListener.h"
#import <ANTLR/CommonToken.h>
#import "ModelAdaptor.h"
#import "AttributeRenderer.h"

@class ErrorManager;
@class STErrorListener;
@class ST;
@class CompiledST;

@interface STGroup_Anon1 : NSObject {
    AMutableDictionary *dict;
}

+ (id) newSTGroup_Anon1;

- (id) init;
- (id) getDict;
- (id) objectForKey:(id)aKey;
- (void) setObject:(id)anObject forKey:(id)aKey;
- (NSInteger) count;

@property (retain) AMutableDictionary *dict;
@end

/**
 * A directory or directory tree of .st template files and/or group files.
 * Individual template files contain formal template definitions. In a sense,
 * it's like a single group file broken into multiple files, one for each template.
 * ST v3 had just the pure template inside, not the template name and header.
 * Name inside must match filename (minus suffix).
 */


/**
 * When we use key as a value in a dictionary, this is how we signify.
 */
@interface STGroup : NSObject {
    
    /**
     * Load files using what encoding?
     */
    NSStringEncoding encoding;
    
    /**
     * Every group can import templates/dictionaries from other groups.
     * The list must be synchronized (see importTemplates).
     */
    __strong AMutableArray *imports;
    __strong AMutableArray *importsToClearOnUnload;
    unichar delimiterStartChar;
    unichar delimiterStopChar;
    
    /**
     * Maps template name to StringTemplate object. synchronized.
     */
    __strong AMutableDictionary *templates;
    
    /**
     * Maps dict names to HashMap objects.  This is the list of dictionaries
     * defined by the user like typeInitMap ::= ["int":"0"]
     */
    __strong AMutableDictionary *dictionaries;
    
    /**
     * A dictionary that allows people to register a renderer for
     * a particular kind of object for any template evaluated relative to this
     * group.  For example, a date should be formatted differently depending
     * on the locale.  You can set Date.class to an object whose
     * toString(Object) method properly formats a Date attribute
     * according to locale.  Or you can have a different renderer object
     * for each locale.
     * 
     * Order of addition is recorded and matters.  If more than one
     * renderer works for an object, the first registered has priority.
     * 
     * Renderer associated with type t works for object obj if
     * 
     * t.isAssignableFrom(obj.getClass()) // would assignment t = obj work?
     * 
     * So it works if obj is subclass or implements t.
     * 
     * This structure is synchronized.
     */
    __strong AMutableDictionary *renderers;
    
    /**
     * A dictionary that allows people to register a model adaptor for
     * a particular kind of object (subclass or implementation). Applies
     * for any template evaluated relative to this group.
     * 
     * ST initializes with model adaptors that know how to pull
     * properties out of Objects, Maps, and STs.
     */
    __strong AMutableDictionary *adaptors;
    
    /**
     * Cache exact attribute type to adaptor object
     */
    __strong AMutableDictionary *typeToAdaptorCache;
    
	/** Cache exact attribute type to renderer object */
	__strong AMutableDictionary *typeToRendererCache;
    /**
     * The errMgr for entire group; all compilations and executions.
     * This gets copied to parsers, walkers, and interpreters.
     */
    __strong ErrorManager *errMgr;
    /** v3 compatibility; used to iterate across values not keys like v4.
     *  But to convert ANTLR templates, it's too hard to find without
     *  static typing in templates.
     */
    BOOL iterateAcrossValues;
}

+ (CompiledST *) NOT_FOUND_ST;
+ (NSString *) DEFAULT_KEY;
+ (NSString *) DICT_KEY;
+ (STGroup *) defaultGroup;
+ (void) resetDefaultGroup;

+ (ErrorManager *) DEFAULT_ERR_MGR;
+ (BOOL) debug;
+ (void) setDebug;
+ (BOOL) verbose;
+ (BOOL)trackCreationEvents;

+ (NSString *) getMangledRegionName:(NSString *)enclosingTemplateName name:(NSString *)name;
+ (NSString *) getUnMangledTemplateName:(NSString *)mangledName;

+ (id) newSTGroup;
+ (id) newSTGroup:(unichar)startChar delimiterStopChar:(unichar)stopChar;
- (id) init:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
- (void)dealloc;
- (ST *) getInstanceOf:(NSString *)name;
- (ST *) getEmbeddedInstanceOf:(Interpreter *)interp who:(ST *)enclosingInstance ip:(NSInteger)ip name:(NSString *)name;
- (ST *) createSingleton:(CommonToken *)templateToken;
- (BOOL) isDefined:(NSString *)name;
- (CompiledST *) lookupTemplate:(NSString *)name;
- (void) unload;
- (CompiledST *) load:(NSString *)name;
- (void) load;
- (CompiledST *) lookupImportedTemplate:(NSString *)name;
- (CompiledST *) rawGetTemplate:(NSString *)name;
- (AMutableDictionary *) rawGetDictionary:(NSString *)name;
- (BOOL) isDictionary:(NSString *)name;
- (CompiledST *) defineTemplate:(NSString *)templateName template:(NSString *)template;

- (CompiledST *) defineTemplate:(NSString *)aName argsS:(NSString *)argsS template:(NSString *)template;
- (CompiledST *) defineTemplate:(NSString *)templateName nameT:(CommonToken *)nameT args:(AMutableArray *)args template:(NSString *)template templateToken:(CommonToken *)templateToken;
- (CompiledST *) defineTemplateAlias:(CommonToken *)aliasT targetT:(CommonToken *)targetT;
- (CompiledST *) defineRegion:(NSString *)enclosingTemplateName regionT:(CommonToken *)regionT template:(NSString *)template templateToken:(CommonToken *)templateToken;
- (void) defineTemplateOrRegion:(NSString *)templateName regionSurroundingTemplateName:(NSString *)regionSurroundingTemplateName templateToken:(CommonToken *)templateToken template:(NSString *)template nameToken:(CommonToken *)nameToken args:(AMutableArray *)args;
- (void) rawDefineTemplate:(NSString *)name code:(CompiledST *)code defT:(CommonToken *)defT;
- (void) undefineTemplate:(NSString *)name;
- (CompiledST *) compile:(NSString *)srcName name:(NSString *)name args:(AMutableArray *)args template:(NSString *)template templateToken:(CommonToken *)templateToken;
- (void) defineDictionary:(NSString *)name mapping:(AMutableDictionary *)mapping;
- (void) importTemplates:(STGroup *)g;
- (void) importTemplatesWithFileName:(CommonToken *)fileNameToken;
- (void) importTemplates:(STGroup *)g ClearOnUnload:(BOOL)clearOnUnload;
- (AMutableArray *) getImportedGroups;
- (void) loadGroupFile:(NSString *)prefix fileName:(NSString *)fileName;
- (CompiledST *) loadTemplateFile:(NSString *)prefix fileName:(NSString *)fileName stream:(id<CharStream>)templateStream;
- (CompiledST *) loadAbsoluteTemplateFile:(NSString *) fileName;
- (void) registerModelAdaptor:(Class)attributeType adaptor:(id<ModelAdaptor>)adaptor;
- (void) invalidateModelAdaptorCache:(Class)attributeType;
- (id<ModelAdaptor>) getModelAdaptor:(Class)attributeType;
- (void) registerRenderer:(Class)attributeType r:(id<AttributeRenderer>)r;
- (void) registerRenderer:(Class)attributeType r:(id<AttributeRenderer>)r recurs:(BOOL)recursive;
- (id<AttributeRenderer>) getAttributeRenderer:(Class)attributeType;
- (ST *) createStringTemplate:(CompiledST *)anImpl;
- (ST *) createStringTemplateInternally:(CompiledST *)anImpl;
- (ST *) createStringTemplateInternallyWithProto:(ST *)proto;
- (NSURL *)getURL:(NSString *)fileName;
- (NSURL *)getRootDirURL;
- (NSString *) description;
- (NSString *) toString;
- (NSString *) show;

// getters and setters

- (NSString *) getFileName;
- (NSString *) getName;
- (id<STErrorListener>) getListener;
- (void) setListener:(id<STErrorListener>)aListener;
- (AMutableArray *) getTemplateNames;

@property (assign) NSStringEncoding encoding; 
@property (retain) AMutableArray *imports;
@property (assign) unichar delimiterStartChar;
@property (assign) unichar delimiterStopChar;
@property (retain) AMutableDictionary *templates;
@property (retain) AMutableDictionary *dictionaries;
@property (retain) AMutableDictionary *renderers;
@property (retain) AMutableDictionary *adaptors;
@property (retain) ErrorManager *errMgr;
@property (retain) AMutableDictionary *typeToAdaptorCache;
@property (retain) AMutableDictionary *typeToRendererCache;
@property (assign) BOOL iterateAcrossValues;

@end
