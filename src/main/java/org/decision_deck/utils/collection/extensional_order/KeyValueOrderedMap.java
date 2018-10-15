package org.decision_deck.utils.collection.extensional_order;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;

import org.decision_deck.utils.collection.CollectionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * <p>
 * A navigable map whose sorting logic is based on values as well as keys.
 * </p>
 * <p>
 * Implementation is based on 1) a sorted tree (or navigable set) containing the keys and 2) a map (called base map) to
 * associate values to the keys. The sorted tree uses a key comparator. The keys comparator queries the base map to get
 * the values and handles the job to the user-provided entry comparator. This object ensures that the base map contains
 * the entries before the new entries are added to the sorted tree. This is necessary for the key comparator to be able
 * to do the work.
 * </p>
 * <p>
 * The submap views provided by this object are only valid as long as the existing ordering at time the map view is
 * queried for is unchanged. Results are undetermined if the ordering changes after the view has been created. Changing
 * ordering is normally not possible in a navigable map as the ordering only depends on the key. Here, changing the
 * ordering is possible through setting a different value to an existing key. This results in the same key possibly
 * being ordered differently than previously, hence its ceiling key, and so on, may change. Doing this is permitted by
 * this object, but such operations invalidate the submap views obtained previously.
 * </p>
 * <p>
 * The views do not fully support removal (not implemented yet).
 * </p>
 * <p>
 * The comparator used by this navigable map is extentional: it defines an order only on the objects known to this map
 * (this is a partial order on the set of keys possibly accepted by this map). It may not be used to compare objects
 * which are unknown to this map.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <K>
 *            the type of keys maintained by this map.
 * @param <V>
 *            the type of mapped values.
 */
public class KeyValueOrderedMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V> {

    /**
     * The base map always contains all keys entries from the tree. It may contain more keys than the tree (this happens
     * when this object is used to provide a submap view).
     */
    private final Map<K, V> m_baseMap;
    private final Ordering<Map.Entry<K, V>> m_comparator;
    private final NavigableSet<K> m_treeSet;

    private KeyValueOrderedMap(Ordering<Map.Entry<K, V>> comparator, Map<K, V> baseMap, NavigableSet<K> baseNav) {
	m_comparator = comparator;
	m_baseMap = baseMap;
	m_treeSet = baseNav;
	assert baseMap.keySet().containsAll(m_treeSet);
    }

    @Override
    public boolean containsValue(Object value) {
	/** We could search in the base map first, but it's probably not faster. */
	// if (!m_baseMap.containsValue(value)) {
	// return false;
	// }
	/**
	 * We have to search for the value because the base map may contain more values than are effectively in this
	 * object, because this object could be a partial view over a part.
	 */
	return super.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
	/** Need to check this, otherwise, the key comparator will fail. */
	if (!m_baseMap.containsKey(key)) {
	    return false;
	}
	/** Need to check this, because the key could be in the base map and yet not be in this object. */
	return m_treeSet.contains(key);
    }

    /**
     * <p>
     * Creates a new navigable map which initially contains the entries contained in the given base map.
     * </p>
     * <p>
     * This object assumes ownership over the provided base map.
     * </p>
     * 
     * @param comparator
     *            not <code>null</code>.
     * @param baseMap
     *            not <code>null</code>.
     */
    public KeyValueOrderedMap(Comparator<Map.Entry<K, V>> comparator, Map<K, V> baseMap) {
	m_comparator = Ordering.from(comparator);
	m_baseMap = baseMap;
	final Function<K, Map.Entry<K, V>> asEntry = CollectionUtils.getFunctionAsEntry(m_baseMap);
	final Ordering<K> keyOrdering = m_comparator.onResultOf(asEntry);
	m_treeSet = Sets.newTreeSet(keyOrdering);
	m_treeSet.addAll(baseMap.keySet());
    }

    static public <K, V> KeyValueOrderedMap<K, V> create(Comparator<Entry<K, V>> comparator) {
	return new KeyValueOrderedMap<K, V>(comparator, Maps.<K, V> newHashMap());
    }

    /**
     * This is an extentional comparator. May be used only to query objects known to this map.
     * 
     * @return not <code>null</code>.
     */
    @Override
    public Comparator<? super K> comparator() {
	return m_treeSet.comparator();
    }

    @Override
    public K firstKey() {
	return m_treeSet.first();
    }

    @Override
    public K lastKey() {
	return m_treeSet.last();
    }

    @Override
    public Map.Entry<K, V> lowerEntry(K key) {
	final K lower = m_treeSet.lower(key);
	return new SimpleEntry<K, V>(lower, m_baseMap.get(lower));
    }

    @Override
    public K lowerKey(K key) {
	return m_treeSet.lower(key);
    }

    @Override
    public Map.Entry<K, V> floorEntry(K key) {
	final K floor = m_treeSet.floor(key);
	return new SimpleEntry<K, V>(floor, m_baseMap.get(floor));
    }

    @Override
    public K floorKey(K key) {
	return m_treeSet.floor(key);
    }

    @Override
    public Map.Entry<K, V> ceilingEntry(K key) {
	final K ceiling = m_treeSet.ceiling(key);
	return CollectionUtils.newEntry(ceiling, get(ceiling));
    }

    @Override
    public V get(Object key) {
	return m_baseMap.get(key);
    }

    @Override
    public K ceilingKey(K key) {
	return m_treeSet.ceiling(key);
    }

    @Override
    public Map.Entry<K, V> higherEntry(K key) {
	final K higherKey = higherKey(key);
	return CollectionUtils.newEntry(higherKey, get(higherKey));
    }

    @Override
    public K higherKey(K key) {
	return m_treeSet.higher(key);
    }

    @Override
    public Map.Entry<K, V> firstEntry() {
	final K firstKey = firstKey();
	return CollectionUtils.newEntry(firstKey, get(firstKey));
    }

    @Override
    public Map.Entry<K, V> lastEntry() {
	final K lastKey = lastKey();
	return CollectionUtils.newEntry(lastKey, get(lastKey));
    }

    @Override
    public Map.Entry<K, V> pollFirstEntry() {
	if (m_treeSet.isEmpty()) {
	    return null;
	}
	final K key = firstKey();
	final V removed = remove(key);
	return CollectionUtils.newEntry(key, removed);
    }

    @Override
    public Map.Entry<K, V> pollLastEntry() {
	if (m_treeSet.isEmpty()) {
	    return null;
	}
	final K key = lastKey();
	final V removed = remove(key);
	return CollectionUtils.newEntry(key, removed);
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
	return new KeyValueOrderedMap<K, V>(m_comparator.reverse(), m_baseMap, m_treeSet.descendingSet());
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
	return Sets.unmodifiableNavigableSet(m_treeSet);
    }

    @Override
    public V put(K key, V value) {
	// final V old = m_baseMap.get(key);
	// if (old != null && !old.equals(value)) {
	// final boolean removed = m_treeSet.remove(key);
	// assert(removed);
	// }
	/**
	 * If the value changed, the key position in the ordering may change. Removing the key from the tree makes sure
	 * the key is correctly inserted.
	 */
	/** We have to remove before changing the map! */
	final boolean contained = containsKey(key);
	if (contained) {
	    final boolean removed = m_treeSet.remove(key);
	    assert (removed);
	}
	final V old = m_baseMap.put(key, value);
	assert (contained == (old != null));
	final boolean added = m_treeSet.add(key);
	assert (added);
	return old;
    }

    @Override
    public V remove(Object key) {
	if (!containsKey(key)) {
	    return null;
	}
	final boolean removed = m_treeSet.remove(key);
	assert (removed);
	assert (m_baseMap.containsKey(key));
	final V value = m_baseMap.remove(key);
	return value;
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
	final NavigableSet<K> descendingSet = m_treeSet.descendingSet();
	/** We should observe this to sync removal operations with the base map, but not implemented yet. */
	return Sets.unmodifiableNavigableSet(descendingSet);
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
	final V toValue = get(toKey);
	final V fromValue = get(fromKey);
	final Map.Entry<K, V> fromEntry = CollectionUtils.newEntry(fromKey, fromValue);
	final Map.Entry<K, V> toEntry = CollectionUtils.newEntry(toKey, toValue);
	final NavigableSet<K> subSet = m_treeSet.subSet(fromKey, fromInclusive, toKey, toInclusive);
	return new KeyValueOrderedMap<K, V>(m_comparator, m_baseMap, subSet) {
	    @Override
	    public V put(K key, V value) {
		if (containsKey(key) && get(key).equals(value)) {
		    return value;
		}
		/** A check on not too much change (this is incomplete check but it can be done quickly). */
		assert get(fromEntry.getKey()).equals(fromValue);
		assert get(toEntry.getKey()).equals(toValue);
		final Map.Entry<K, V> newEntry = CollectionUtils.newEntry(key, value);
		checkArgument(m_comparator.compare(fromEntry, newEntry) > 0);
		checkArgument(m_comparator.compare(newEntry, toEntry) < 0);
		return KeyValueOrderedMap.this.put(key, value);
	    }
	};
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
	final V toValue = get(toKey);
	final Map.Entry<K, V> toEntry = CollectionUtils.newEntry(toKey, toValue);
	final NavigableSet<K> headSet = m_treeSet.headSet(toKey, inclusive);
	return new KeyValueOrderedMap<K, V>(m_comparator, m_baseMap, headSet) {
	    @Override
	    public V put(K key, V value) {
		if (containsKey(key) && get(key).equals(value)) {
		    return value;
		}
		/** A check on not too much change (this is incomplete check but it can be done quickly). */
		assert get(toEntry.getKey()).equals(toValue);
		final Map.Entry<K, V> newEntry = CollectionUtils.newEntry(key, value);
		checkArgument(m_comparator.compare(newEntry, toEntry) < 0);
		return KeyValueOrderedMap.this.put(key, value);
	    }
	};
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
	final V fromValue = get(fromKey);
	final Map.Entry<K, V> fromEntry = CollectionUtils.newEntry(fromKey, fromValue);
	final NavigableSet<K> tailSet = m_treeSet.tailSet(fromKey, inclusive);
	return new KeyValueOrderedMap<K, V>(m_comparator, m_baseMap, tailSet) {
	    @Override
	    public V put(K key, V value) {
		if (containsKey(key) && get(key).equals(value)) {
		    return value;
		}
		/** A check on not too much change (this is incomplete check but it can be done quickly). */
		assert get(fromEntry.getKey()).equals(fromValue);
		final Map.Entry<K, V> newEntry = CollectionUtils.newEntry(key, value);
		checkArgument(m_comparator.compare(fromEntry, newEntry) > 0);
		return KeyValueOrderedMap.this.put(key, value);
	    }
	};
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
	return subMap(fromKey, true, toKey, false);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
	return headMap(toKey, false);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
	return tailMap(fromKey, true);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
	final AbstractSet<Map.Entry<K, V>> abstractSet = new AbstractSet<Map.Entry<K, V>>() {

	    @Override
	    public boolean remove(Object o) {
		final boolean containsKey = KeyValueOrderedMap.this.containsKey(o);
		if (!containsKey) {
		    return false;
		}
		KeyValueOrderedMap.this.remove(o);
		return true;
	    }

	    @Override
	    public void clear() {
		KeyValueOrderedMap.this.clear();
	    }

	    @Override
	    public Iterator<Map.Entry<K, V>> iterator() {
		return Iterators.transform(m_treeSet.iterator(), new Function<K, Map.Entry<K, V>>() {
		    @Override
		    public Map.Entry<K, V> apply(K input) {
			return CollectionUtils.newEntry(input, get(input));
		    }
		});
	    }

	    @Override
	    public int size() {
		return m_treeSet.size();
	    }
	};
	return abstractSet;
    }

}
