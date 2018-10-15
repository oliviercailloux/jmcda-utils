package org.decision_deck.utils.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.ForwardingNavigableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

public class ObservableNavigableMap<K, V> extends ForwardingNavigableMap<K, V> {

    private final NavigableMap<K, V> m_delegate;
    private final ObservableMapHelper<K, V> m_helper;

    /**
     * Registers all handler methods on <code>object</code> to receive events. A handler method is one that is marked
     * with the {@link Subscribe} annotation.
     * 
     * @param observer
     *            object whose handler methods should be registered.
     */
    public void register(Object observer) {
	m_helper.register(observer);
    }

    /**
     * Unregisters all handler methods on a registered <code>object</code>.
     * 
     * @param observer
     *            object whose handler methods should be unregistered.
     * @throws IllegalArgumentException
     *             if the object was not previously registered.
     */
    public void unregister(Object observer) {
	m_helper.unregister(observer);
    }

    public ObservableNavigableMap(NavigableMap<K, V> delegate) {
	m_delegate = delegate;
	m_helper = new ObservableMapHelper<K, V>(m_delegate);
    }

    public ObservableNavigableMap() {
	this(new TreeMap<K, V>());
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
	m_helper.clear(m_delegate.entrySet());
    }

    @Override
    public V put(K key, V value) {
	final boolean contained = containsKey(key);
	final V previousValue = get(key);
	return m_helper.put(key, previousValue, contained, previousValue);
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
    public NavigableMap<K, V> descendingMap() {
	final ObservableNavigableMap<K, V> original = this;
	final ObservableNavigableMap<K, V> inverted = new ObservableNavigableMap<K, V>(m_delegate.descendingMap()) {
	    @Override
	    public ObservableNavigableMap<K, V> descendingMap() {
		return original;
	    }
	};
	return inverted;
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
	return Sets.unmodifiableNavigableSet(m_delegate.navigableKeySet());
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
	return Sets.unmodifiableNavigableSet(m_delegate.descendingKeySet());
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
	return Maps.unmodifiableNavigableMap(m_delegate.subMap(fromKey, fromInclusive, toKey, toInclusive));
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
	return Maps.unmodifiableNavigableMap(m_delegate.headMap(toKey, inclusive));
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
	final SortedMap<K, V> subMap = m_delegate.headMap(toKey);
	if (subMap instanceof NavigableMap<?, ?>) {
	    NavigableMap<K, V> nav = (NavigableMap<K, V>) subMap;
	    return Maps.unmodifiableNavigableMap(nav);
	}
	return Collections.unmodifiableSortedMap(subMap);
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
	final SortedMap<K, V> subMap = m_delegate.subMap(fromKey, toKey);
	if (subMap instanceof NavigableMap<?, ?>) {
	    NavigableMap<K, V> nav = (NavigableMap<K, V>) subMap;
	    return Maps.unmodifiableNavigableMap(nav);
	}
	return Collections.unmodifiableSortedMap(subMap);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
	final SortedMap<K, V> subMap = m_delegate.tailMap(fromKey);
	if (subMap instanceof NavigableMap<?, ?>) {
	    NavigableMap<K, V> nav = (NavigableMap<K, V>) subMap;
	    return Maps.unmodifiableNavigableMap(nav);
	}
	return Collections.unmodifiableSortedMap(subMap);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
	return Maps.unmodifiableNavigableMap(m_delegate.tailMap(fromKey, inclusive));
    }

    @Override
    protected NavigableMap<K, V> delegate() {
	return m_delegate;
    }

}
