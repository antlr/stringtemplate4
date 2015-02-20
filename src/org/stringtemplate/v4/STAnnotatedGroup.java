/*
 * [The "BSD license"]
 *  Copyright (c) 2011-today Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *	 notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *	 notice, this list of conditions and the following disclaimer in the
 *	 documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *	 derived from this software without specific prior written permission.
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
package org.stringtemplate.v4;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.stringtemplate.v4.compiler.CompiledST;
import org.stringtemplate.v4.compiler.FormalArgument;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * A {@link STGroup} wrapper to provide a non-intrusive API to
 * access {@link STAnnotation annotations}.
 * @author <a href="mailto:rydnr@acm-sl.org">rydnr</a>
 */
public class STAnnotatedGroup extends STGroup {
	/**
	 * The wrapped {@link STGroup}.
	 */
	private final STGroup wrappedGroup;

	/**
	 * Creates a new {@code STAnnotatedGroup} wrapping given {@link STGroup}.
	 * @param group the group.
	 */
	public STAnnotatedGroup(STGroup group) {
		super(group.delimiterStartChar, group.delimiterStopChar);
		this.wrappedGroup = group;
	}

	/**
	 * Retrieves the wrapped group.
	 * @return such group.
	 */
	public STGroup getSTGroup() {
		return this.wrappedGroup;
	}

	//-- wrapped methods --//
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ST getInstanceOf(String name) {
		return this.wrappedGroup.getInstanceOf(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ST createSingleton(Token templateToken) {
		return this.wrappedGroup.createSingleton(templateToken);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDefined(String name) {
		return this.wrappedGroup.isDefined(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST lookupTemplate(String name) {
		return this.wrappedGroup.lookupTemplate(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void unload() {
		this.wrappedGroup.unload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		this.wrappedGroup.load();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST rawGetTemplate(String name) {
		return this.wrappedGroup.rawGetTemplate(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> rawGetDictionary(String name) {
		return this.wrappedGroup.rawGetDictionary(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDictionary(String name) {
		return this.wrappedGroup.isDictionary(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST defineTemplate(String templateName, String template) {
		return this.wrappedGroup.defineTemplate(templateName, template);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST defineTemplate(String name, String argsS, String template) {
		return this.wrappedGroup.defineTemplate(name, argsS, template);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST defineTemplate(
		String fullyQualifiedTemplateName,
		Token nameT,
		List<FormalArgument> args,
		String template,
		Token templateToken) {
		return
			this.wrappedGroup.defineTemplate(
				fullyQualifiedTemplateName,
				nameT,
				args,
				template,
				templateToken);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST defineTemplate(
		String fullyQualifiedTemplateName,
		Token nameT,
		List<FormalArgument> args,
		String template,
		Token templateToken,
		List<STAnnotation> annotations) {
		return
			this.wrappedGroup.defineTemplate(
				fullyQualifiedTemplateName,
				nameT,
				args,
				template,
				templateToken,
				annotations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST defineTemplateAlias(Token aliasT, Token targetT) {
		return this.wrappedGroup.defineTemplateAlias(aliasT, targetT);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST defineRegion(
		String enclosingTemplateName,
		Token regionT,
		String template,
		Token templateToken) {
		return
			this.wrappedGroup.defineRegion(
				enclosingTemplateName,
				regionT,
				template,
				templateToken);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void defineTemplateOrRegion(
		String fullyQualifiedTemplateName,
		String regionSurroundingTemplateName,
		Token templateToken,
		String template,
		Token nameToken,
		List<FormalArgument> args) {
		this.wrappedGroup.defineTemplateOrRegion(
			fullyQualifiedTemplateName,
			regionSurroundingTemplateName,
			templateToken,
			template,
			nameToken,
			args);				
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void defineTemplateOrRegion(
		String fullyQualifiedTemplateName,
		String regionSurroundingTemplateName,
		Token templateToken,
		String template,
		Token nameToken,
		List<FormalArgument> args,
		List<STAnnotation> annotations) {
		this.wrappedGroup.defineTemplateOrRegion(
			fullyQualifiedTemplateName,
			regionSurroundingTemplateName,
			templateToken,
			template,
			nameToken,
			args,
			annotations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rawDefineTemplate(String name, CompiledST code, Token defT) {
		this.wrappedGroup.rawDefineTemplate(name, code, defT);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undefineTemplate(String name) {
		this.wrappedGroup.undefineTemplate(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST compile(
		String srcName,
		String name,
		List<FormalArgument> args,
		String template,
		Token templateToken) {
		return this.wrappedGroup.compile(srcName, name, args, template, templateToken);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void defineDictionary(String name, Map<String,Object> mapping) {
		this.wrappedGroup.defineDictionary(name, mapping);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importTemplates(STGroup g) {
		this.wrappedGroup.importTemplates(g);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importTemplates(Token fileNameToken) {
		this.wrappedGroup.importTemplates(fileNameToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<STGroup> getImportedGroups() {
		return this.wrappedGroup.getImportedGroups();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadGroupFile(String prefix, String fileName) {
		this.wrappedGroup.loadGroupFile(prefix, fileName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST loadAbsoluteTemplateFile(String fileName) {
		return this.wrappedGroup.loadAbsoluteTemplateFile(fileName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompiledST loadTemplateFile(String prefix, String unqualifiedFileName, CharStream templateStream) {
		return this.wrappedGroup.loadTemplateFile(prefix, unqualifiedFileName, templateStream);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerModelAdaptor(Class<?> attributeType, ModelAdaptor adaptor) {
		this.wrappedGroup.registerModelAdaptor(attributeType, adaptor);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAdaptor getModelAdaptor(Class<?> attributeType) {
		return this.wrappedGroup.getModelAdaptor(attributeType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerRenderer(Class<?> attributeType, AttributeRenderer r) {
		this.wrappedGroup.registerRenderer(attributeType, r);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerRenderer(Class<?> attributeType, AttributeRenderer r, boolean recursive) {
		this.wrappedGroup.registerRenderer(attributeType, r, recursive);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AttributeRenderer getAttributeRenderer(Class<?> attributeType) {
		return this.wrappedGroup.getAttributeRenderer(attributeType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ST createStringTemplate(CompiledST impl) {
		return this.wrappedGroup.createStringTemplate(impl);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ST createStringTemplateInternally(CompiledST impl) {
		return this.wrappedGroup.createStringTemplateInternally(impl);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ST createStringTemplateInternally(ST proto) {
		return this.wrappedGroup.createStringTemplateInternally(proto);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.wrappedGroup.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileName() {
		return this.wrappedGroup.getFileName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getRootDirURL() {
		return this.wrappedGroup.getRootDirURL();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getURL(String fileName) {
		return this.wrappedGroup.getURL(fileName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String show() {
		return this.wrappedGroup.show();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public STErrorListener getListener() {
		return this.wrappedGroup.getListener();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setListener(STErrorListener listener) {
		this.wrappedGroup.setListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getTemplateNames() {
		return this.wrappedGroup.getTemplateNames();
	}

	//-- STAnnotation logic --//
	/**
	 * Retrieves all annotations.
	 * @return such annotations.
	 */
	public List<STAnnotation> getAllAnnotations() {
		List<STAnnotation> result = new ArrayList<STAnnotation>();
		
		for (String name : this.wrappedGroup.templates.keySet()) {
			CompiledST c = rawGetTemplate(name);
			if ( c.isAnonSubtemplate || c==NOT_FOUND_ST ) continue;
			for (STAnnotation annotation : c.annotations) {
				result.add(annotation);
			}
		}

		return result;
	}

	public List<STAnnotation> getMatchingAnnotations(final String text) {
		
		List<STAnnotation> result = new ArrayList<STAnnotation>();

		for (STAnnotation annotation : getAllAnnotations()) {
			if (   (annotation != null)
				&& (annotation.matches(text))) {
				result.add(annotation);
			}
		}

		return result;
	}

	//-- effective java stuff --//
	/**
	 * Checks whether this instance is semantically equal to
	 * given object.
	 * @param target the target.
	 * @return {@code true} if both instances are equal.
	 */
	@Override
	public boolean equals(final Object target) {

		final boolean result;

		if (this == target) {
			result = true;
		} else if (target == null) {
			result = false;
		} else if (target instanceof STGroup) {
			final STGroup group = (STGroup) target;
			
			result = group.equals(this.wrappedGroup);
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * Retrieves the hashcode.
	 * @return such value.
	 */
	@Override
	public int hashCode() {
		final int result;

		if (this.wrappedGroup == null) {
			result = super.hashCode();
		} else {
			result = this.wrappedGroup.hashCode();
		}

		return result;
	}

	/**
	 * Retrieves the string representation of the group.
	 * @return such text.
	 */
	@Override
	public String toString() {
		final String result;

		if (this.wrappedGroup == null) {
			result = "{ \"class\": \"" + STAnnotatedGroup.class.getName() + "\" }";
		} else {
			result = this.wrappedGroup.toString();
		}

		return result;
	}
}
