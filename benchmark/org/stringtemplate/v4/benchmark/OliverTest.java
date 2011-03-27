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

/** Borrowed from Oliver Zeigermann */

package org.stringtemplate.v4.benchmark;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.benchmark.oliver.Helper;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/** Adapted from Oliver Zeigermann benchmarking */
public class OliverTest {
	static STGroup test = new STGroupFile("email.stg");

	public void timeEmail(int reps) {
		ST st = test.getInstanceOf("email");
		st.add("order", Helper.order);
		st.add("separator", "----------------");
		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeEmailWriteToStringBuffer(int reps) {
		ST st = test.getInstanceOf("email");
		st.add("order", Helper.order);
		st.add("separator", "----------------");
		for (int i = 0; i < reps; i++) {
			StringWriter sw = new StringWriter();
			AutoIndentWriter w = new AutoIndentWriter(sw);
			try {st.write(w);} catch (IOException ioe) {;}
		}
	}

	public void timeEmailWithRenderers(int reps) {
		STGroup test = new STGroupFile("email.stg");
		test.registerRenderer(Date.class, new DateRenderer());
		test.registerRenderer(BigDecimal.class, new BigDecimalRenderer());
		ST st = test.getInstanceOf("email");
		st.add("order", Helper.order);
		st.add("separator", "----------------");
		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public static class BigDecimalRenderer implements AttributeRenderer {
		private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
				"##,##0.00", DecimalFormatSymbols.getInstance(Locale.GERMANY));
		private static final String EURO_CHARACTER = "\u20AC";

		public String toString(Object o, String formatString, Locale locale) {
			if (formatString.equals("currency")) {
				if (o instanceof BigDecimal) {
					NumberFormat numberFormat = DECIMAL_FORMAT;
					String formatted = numberFormat.format(o) + " "
							+ EURO_CHARACTER;
					return formatted;
				}
			}
			return o.toString();
		}
	}

}
