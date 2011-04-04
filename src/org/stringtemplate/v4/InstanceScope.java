/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
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

package org.stringtemplate.v4;

import org.stringtemplate.v4.debug.EvalTemplateEvent;
import org.stringtemplate.v4.debug.InterpEvent;

import java.util.ArrayList;
import java.util.List;

/** */
public class InstanceScope {
	public InstanceScope parent;	// template that invoked us
	public ST st;      				// template we're executing
	public int ret_ip; 				// return address

	/* Includes the EvalTemplateEvent for this template.  This
	*  is a subset of Interpreter.events field. The final
	*  EvalTemplateEvent is stored in 3 places:
	*
	*  	1. In enclosing instance's childTemplateEvents
	*  	2. In this event list
	*  	3. In the overall event list
	*
	*  The root ST has the final EvalTemplateEvent in its list.
	*
	*  All events get added to the enclosingInstance's event list.
	*/
	public List<InterpEvent> events = new ArrayList<InterpEvent>();

	/** All templates evaluated and embedded in this ST. Used
	 *  for tree view in STViz.
	 */
	public List<EvalTemplateEvent> childEvalTemplateEvents =
		new ArrayList<EvalTemplateEvent>();

	public InstanceScope(InstanceScope parent, ST st) {
		this.parent = parent;
		this.st = st;
	}
}