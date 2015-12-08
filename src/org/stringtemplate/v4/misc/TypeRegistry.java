/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
package org.stringtemplate.v4.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Sam Harwell
 */
public class TypeRegistry<V> implements Map<Class<?>, V> {

	private final Map<Class<?>, V> backingStore = new HashMap<Class<?>, V>();
	private final Map<Class<?>, Class<?>> cache = new HashMap<Class<?>, Class<?>>();

	public int size() {
		return backingStore.size();
	}

	public boolean isEmpty() {
		return backingStore.isEmpty();
	}

	public boolean containsKey(Object key) {
		if (cache.containsKey(key)) {
			return true;
		}

		if (!(key instanceof Class)) {
			return false;
		}

		return get(key) != null;
	}

	@SuppressWarnings("unchecked")
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws AmbiguousMatchException if the registry contains more than value
	 * mapped to a maximally-specific type from which {@code key} is derived.
	 */
	public V get(Object key) {
		V value = backingStore.get(key);
		if (value != null) {
			return value;
		}

		Class<?> redirect = cache.get(key);
		if (redirect != null) {
			if (redirect == Void.TYPE) {
				return null;
			}
			else {
				return backingStore.get(redirect);
			}
		}

		if (!(key instanceof Class)) {
			return null;
		}

		Class<?> keyClass = (Class<?>)key;
		List<Class<?>> candidates = new ArrayList<Class<?>>();
		for (Class<?> clazz : backingStore.keySet()) {
			if (clazz.isAssignableFrom(keyClass)) {
				candidates.add(clazz);
			}
		}

		if (candidates.isEmpty()) {
			cache.put(keyClass, Void.TYPE);
			return null;
		}
		else if (candidates.size() == 1) {
			cache.put(keyClass, candidates.get(0));
			return backingStore.get(candidates.get(0));
		}
		else {
			for (int i = 0; i < candidates.size() - 1; i++) {
				if (candidates.get(i) == null) {
					continue;
				}

				for (int j = i + 1; j < candidates.size(); j++) {
					if (candidates.get(i).isAssignableFrom(candidates.get(j))) {
						candidates.set(i, null);
						break;
					}
					else if (candidates.get(j).isAssignableFrom(candidates.get(i))) {
						candidates.set(j, null);
					}
				}
			}

			int j = 0;
			for (int i = 0; i < candidates.size(); i++) {
				Class<?> current = candidates.get(i);
				if (current == null) {
					continue;
				}

				if (i != j) {
					candidates.set(j, current);
				}

				j++;
			}

			assert j > 0;
			if (j != 1) {
				StringBuilder builder = new StringBuilder();
				builder.append(String.format("The class '%s' does not match a single item in the registry. The %d ambiguous matches are:", keyClass.getName(), j));
				for (int i = 0; i < j; i++) {
					builder.append(String.format("%n    %s", candidates.get(j).getName()));
				}

				throw new AmbiguousMatchException(builder.toString());
			}

			cache.put(keyClass, candidates.get(0));
			return backingStore.get(candidates.get(0));
		}
	}

	public V put(Class<?> key, V value) {
		V result = get(key);
		backingStore.put(key, value);
		handleAlteration(key);
		return result;
	}

	public V remove(Object key) {
		if (!(key instanceof Class)) {
			return null;
		}

		Class<?> clazz = (Class<?>)key;
		V previous = get(clazz);
		if (backingStore.remove(clazz) != null) {
			handleAlteration(clazz);
		}

		return previous;
	}

	public void putAll(Map<? extends Class<?>, ? extends V> m) {
		for (Map.Entry<? extends Class<?>, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		backingStore.clear();
		cache.clear();
	}

	public Set<Class<?>> keySet() {
		return Collections.unmodifiableSet(backingStore.keySet());
	}

	public Collection<V> values() {
		return Collections.unmodifiableCollection(backingStore.values());
	}

	public Set<Entry<Class<?>, V>> entrySet() {
		return Collections.unmodifiableSet(backingStore.entrySet());
	}

	protected void handleAlteration(Class<?> clazz) {
		for (Map.Entry<Class<?>, ?> entry : cache.entrySet()) {
			if (clazz.isAssignableFrom(entry.getKey())) {
				entry.setValue(null);
			}
		}
	}
}
