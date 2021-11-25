package org.decision_deck.utils.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ForwardingMap;
import com.google.common.eventbus.Subscribe;

public class ObservableMap<K, V> extends ForwardingMap<K, V> {

	private final ObservableMapHelper<K, V> m_helper;
	private final Map<K, V> m_delegate;

	/**
	 * Registers all handler methods on {@code object} to receive events. A
	 * handler method is one that is marked with the {@link Subscribe} annotation.
	 * 
	 * @param observer object whose handler methods should be registered.
	 */
	public void register(Object observer) {
		m_helper.register(observer);
	}

	/**
	 * Unregisters all handler methods on a registered {@code object}.
	 * 
	 * @param observer object whose handler methods should be unregistered.
	 * @throws IllegalArgumentException if the object was not previously registered.
	 */
	public void unregister(Object observer) {
		m_helper.unregister(observer);
	}

	public ObservableMap(Map<K, V> delegate) {
		m_delegate = delegate;
		m_helper = new ObservableMapHelper<K, V>(delegate);
	}

	@Override
	public V remove(Object object) {
		if (!containsKey(object)) {
			return null;
		}
		@SuppressWarnings("unchecked")
		final K key = (K) object;
		final V value = get(key);
		return m_helper.remove(key, value);
	}

	@Override
	public void clear() {
		m_helper.clear(delegate().entrySet());
	}

	@Override
	public V put(K key, V value) {
		final boolean contained = containsKey(key);
		final V previousValue = get(key);
		return m_helper.put(key, value, contained, previousValue);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		standardPutAll(map);
	}

	@Override
	public Set<K> keySet() {
		// TODO();
		return Collections.unmodifiableSet(delegate().keySet());
	}

	@Override
	public Collection<V> values() {
		// TODO();
		return Collections.unmodifiableCollection(delegate().values());
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO();
		return Collections.unmodifiableSet(delegate().entrySet());
	}

	@Override
	protected Map<K, V> delegate() {
		return m_delegate;
	}

}
