package org.decision_deck.utils.collection.extensional_order.mess;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import org.decision_deck.utils.collection.CollectionUtils;

public abstract class KeyValueNavigableMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V> {
	private final NavigableSet<java.util.Map.Entry<K, V>> m_delegateSet;
	private final Map<K, V> m_delegateMap;

	public KeyValueNavigableMap(NavigableSet<Entry<K, V>> delegateSet, Map<K, V> delegateMap) {
		m_delegateSet = delegateSet;
		m_delegateMap = delegateMap;
	}

	@Override
	public Comparator<? super K> comparator() {
		return new Comparator<K>() {

			@Override
			public int compare(K k1, K k2) {
				final V v1 = m_delegateMap.get(k1);
				final V v2 = m_delegateMap.get(k2);
				final Entry<K, V> e1 = CollectionUtils.newEntry(k1, v1);
				final Entry<K, V> e2 = CollectionUtils.newEntry(k2, v2);
				return m_delegateSet.comparator().compare(e1, e2);
			}
		};
	}

}
