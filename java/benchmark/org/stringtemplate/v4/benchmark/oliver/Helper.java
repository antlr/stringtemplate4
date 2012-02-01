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

package org.stringtemplate.v4.benchmark.oliver;

import java.math.BigDecimal;
import java.util.*;

public class Helper {

	public static String unifyNewlines(String source) {
		final String regex = "\\r?\\n";
		final String clearedSource = source.replaceAll(regex, "\n");
		return clearedSource;
	}

	public static Order order;
	static {
		Calendar instance = GregorianCalendar.getInstance(Locale.GERMAN);
		instance.set(2011, Calendar.JANUARY, 28);
		Date orderDate = instance.getTime();

		Customer customer = new Customer("Oliver", "Zeigermann",
				"Gaußstraße 180\n" + "22765 Hamburg\n" + "GERMANY");
		order = new Order(customer, orderDate);

		Article article1 = new Article("How to become famous", new BigDecimal(
				"17.80"));
		order.getItems().add(new Item(1, article1));

		Article article2 = new Article("Cool stuff", new BigDecimal("1.00"));
		order.getItems().add(new Item(2, article2));
	}

	public static Map<String, Object> model = new HashMap<String, Object>();
	static {
		model.put("order", Helper.order);
		model.put("separator", "----------------");
	}

}
