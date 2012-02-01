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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
	public final static BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("20.00");
	public final static BigDecimal SHIPPING_COSTS = new BigDecimal("3.00");
	
	private final Customer customer;
	private final Date orderDate;
	private final List<Item> items = new ArrayList<Item>();

	public Order(Customer customer, Date orderDate) {
		super();
		this.customer = customer;
		this.orderDate = orderDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public List<Item> getItems() {
		return items;
	}

	public BigDecimal getTotal() {
		return getTotalWithoutShipping().add(getShippingCost());
	}
	
	public BigDecimal getShippingCost() {
		if (isFreeShipping()) {
			return new BigDecimal("0");
		} else {
			return SHIPPING_COSTS;
		}
	}

	public BigDecimal getTotalWithoutShipping() {
		BigDecimal total = new BigDecimal(0);
		for (Item item : items) {
			BigDecimal part = item.getSubTotal();
			total = total.add(part);
		}
		return total;
	}

	// introduced for st3 and st4 as they can not compare values
	public boolean isFreeShipping() {
		return getTotalWithoutShipping().compareTo(FREE_SHIPPING_THRESHOLD) == 1;
	}
}
