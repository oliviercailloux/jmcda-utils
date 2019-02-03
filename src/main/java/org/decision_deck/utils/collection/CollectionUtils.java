package org.decision_deck.utils.collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.decision_deck.utils.PredicateUtils;
import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CollectionUtils {

	static public <K, V> Entry<K, V> newEntry(K key, V value) {
		return ImmutableEntry.create(key, value);
	}

	public static class FunctionAsEntry<K, V> implements Function<K, Map.Entry<K, V>> {

		private final Map<K, V> m_map;

		public FunctionAsEntry(Map<K, V> map) {
			checkNotNull(map);
			m_map = map;
		}

		@Override
		public Entry<K, V> apply(K input) {
			checkArgument(m_map.containsKey(input), "Given key is unknown: " + input + ".");
			return ImmutableEntry.create(input, m_map.get(input));
		}

	}

	/**
	 * <p>
	 * Creates a new total order defined on the given source set of elements. The
	 * order reflects iteration order. The returned order contains all the elements
	 * in the given source. A defensive copy is made, so changing the source content
	 * afterwards has no effect on the returned order.
	 * </p>
	 * <p>
	 * The source may not contain duplicates.
	 * </p>
	 * 
	 * @param        <E> the type of elements this order contains.
	 * @param source not <code>null</code>, may not contain duplicates.
	 * @return not <code>null</code>.
	 */
	static public <E> ExtentionalTotalOrder<E> newExtentionalTotalOrder(Iterable<E> source) {
		return ExtentionalTotalOrder.create(source);
	}

	static public class EnumToString implements Function<Enum<?>, String> {
		@Override
		public String apply(Enum<?> input) {
			return input.name();
		}
	}

	public static class FunctionSize implements Function<Collection<?>, Integer> {
		@Override
		public Integer apply(Collection<?> input) {
			return Integer.valueOf(input.size());
		}
	}

	static private class ImmutableEntry<K, V> implements Entry<K, V> {
		static public class GetKey<T> implements Function<Entry<? extends T, ?>, T> {
			@Override
			public T apply(Entry<? extends T, ?> input) {
				return input.getKey();
			}

		}

		static public class GetValue<T> implements Function<Entry<?, ? extends T>, T> {
			@Override
			public T apply(Entry<?, ? extends T> input) {
				return input.getValue();
			}

		}

		private final K m_key;
		private final V m_value;
		private static final char LEFT_ANGLE_BRACKET = '\u27E8';
		private static final char RIGHT_ANGLE_BRACKET = '\u27E9';

		public ImmutableEntry(K key, V value) {
			m_key = key;
			m_value = value;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(m_key, m_value);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Entry<?, ?>)) {
				return false;
			}
			Entry<?, ?> entry2 = (Entry<?, ?>) obj;
			return Objects.equal(m_key, entry2.getKey()) && Objects.equal(m_value, entry2.getValue());
		}

		@Override
		public K getKey() {
			return m_key;
		}

		@Override
		public V getValue() {
			return m_value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException("Entries of this object are read-only.");
		}

		/**
		 * <p>
		 * Retrieves a function which, given a pair, gives its string form in the form
		 * of the transformation of the first element using the given function, a comma
		 * to separate them, and the transformation of the second element, surrounded by
		 * angle brackets (to indicate a tuple). No <code>null</code> pairs are accepted
		 * by the function.
		 * </p>
		 * <p>
		 * This provides an easy way to get short debug strings. E.g. to get a string
		 * representing the contents of a set of pairs of alternatives <em>s</em>, use
		 * <code>Joiner.on(", ").join(Iterables.transform(s, Pair.getToStringFunction(Alternative.getIdFct(), Alternative.getIdFct())))</code>
		 * .
		 * </p>
		 * 
		 * @param           <F1> the type of the first element of the pair to transform.
		 * @param           <F2> the type of the second element of the pair to
		 *                  transform.
		 * @param toString1 a function which transforms the first element of a pair to a
		 *                  string.
		 * @param toString2 a function which transforms the second element of a pair to
		 *                  a string.
		 * 
		 * @return not <code>null</code>.
		 */
		static public <F1, F2> Function<Entry<F1, F2>, String> getToStringFunction(
				final Function<? super F1, String> toString1, final Function<? super F2, String> toString2) {
			return new Function<Entry<F1, F2>, String>() {
				@Override
				public String apply(Entry<F1, F2> input) {
					final Function<Entry<? extends F1, ?>, F1> fctElt1 = new GetKey<F1>();
					final Function<Entry<?, ? extends F2>, F2> fctElt2 = new GetValue<F2>();
					final Function<Entry<? extends F1, ?>, String> str1 = Functions.compose(toString1, fctElt1);
					final Function<Entry<?, ? extends F2>, String> str2 = Functions.compose(toString2, fctElt2);
					return LEFT_ANGLE_BRACKET + str1.apply(input) + ", " + str2.apply(input) + RIGHT_ANGLE_BRACKET;
				}
			};
		}

		@Override
		public String toString() {
			final Function<Entry<K, V>, String> toStringFunction = getToStringFunction(Functions.toStringFunction(),
					Functions.toStringFunction());
			return toStringFunction.apply(this);
		}

		static public <K, V> Map.Entry<K, V> create(K key, V value) {
			return new ImmutableEntry<K, V>(key, value);
		}
	}

	/**
	 * Tells whether a container iterable is a superset of some contained iterable
	 * and contains the objects in the same order as they are found in the contained
	 * iterable. If the container contains, in order, the strings "s1", "s2", "s3",
	 * and the contained object contains "s1", "s3", this method returns
	 * <code>true</code>. If the contained object contains "s3", "s1", this method
	 * returns <code>false</code>. If the contained object is not a subset of the
	 * contained object, this method returns <code>false</code>.
	 * 
	 * @param           <T> the type of searched objects.
	 * @param container not <code>null</code>.
	 * @param contained not <code>null</code>.
	 * @return <code>true</code> iff the given container contains every elements of
	 *         contained in the same order (and possibly more).
	 */
	static public <T> boolean containsInOrder(Iterable<T> container, Iterable<T> contained) {
		final Iterator<T> thisIter = container.iterator();
		for (T toFind : contained) {
			while (true) {
				if (!thisIter.hasNext()) {
					return false;
				}
				final T next = thisIter.next();
				if (Objects.equal(toFind, next)) {
					break;
				}
			}
		}
		return true;
	}

	public static <K, V, L, W> Map<L, W> transformMap(Map<K, V> map, Function<K, L> keyFunction,
			Function<V, W> valueFunction) {
		Map<L, W> transformedMap = Maps.newLinkedHashMap();

		for (Entry<K, V> entry : map.entrySet()) {
			final L newKey = keyFunction.apply(entry.getKey());
			if (transformedMap.containsKey(newKey)) {
				throw new IllegalArgumentException("Duplicate transformed key: " + newKey + ".");
			}
			transformedMap.put(newKey, valueFunction.apply(entry.getValue()));
		}

		return transformedMap;
	}

	public static <K, V, L> Map<L, V> transformKeys(Map<K, V> map, Function<K, L> keyFunction) {
		return transformMap(map, keyFunction, Functions.<V>identity());
	}

	/**
	 * Builds a read-only live view of the union of all the given sets. Warning: the
	 * view is said to be live because changing the content of one of the sets
	 * contained in the given iterable will be reflected in the view. However,
	 * adding a set to the iterables object after this method returns is not
	 * reflected in the object returned by this method.
	 * 
	 * @param      <T> the type of the objects in the sets.
	 * @param sets not <code>null</code>, no <code>null</code> sets inside.
	 * @return not <code>null</code>.
	 */
	public static <T> Set<T> union(Iterable<? extends Set<T>> sets) {
		Preconditions.checkNotNull(sets);
		Set<T> union = Collections.emptySet();
		for (Set<T> contents : sets) {
			union = Sets.union(union, contents);
		}
		return union;
	}

	static public <K, V> Map<K, V> newMapNoNull() {
		return Maps.filterEntries(Maps.<K, V>newLinkedHashMap(), PredicateUtils.<K, V>noNullEntries());
	}

	static public <E> Set<E> newLinkedHashSetNoNull() {
		return Sets.filter(Sets.<E>newLinkedHashSet(), Predicates.notNull());
	}

	static public <E> Set<E> newHashSetNoNull() {
		return Sets.filter(Sets.<E>newHashSet(), Predicates.notNull());
	}

	/**
	 * Tests whether the given subset is indeed a subset of the given superset, and
	 * that all its elements are contained contiguously in the super set. This is a
	 * stronger property than {@link #containsInOrder}.
	 * 
	 * @param          <T> the type of elements in the sets.
	 * @param subSet   not <code>null</code>.
	 * @param superSet not <code>null</code>.
	 * @return <code>true</code> iff the subset is contiguous in the super set.
	 */
	static public <T> boolean isContiguous(Iterable<T> subSet, Iterable<T> superSet) {
		if (!subSet.iterator().hasNext()) {
			return true;
		}
		final Iterator<T> superIterator = superSet.iterator();
		final Iterator<T> subIterator = subSet.iterator();
		final boolean foundFirst = Iterators.contains(superIterator, subIterator.next());
		if (!foundFirst) {
			return false;
		}
		while (subIterator.hasNext()) {
			if (!superIterator.hasNext()) {
				return false;
			}
			final T nextSub = subIterator.next();
			final T nextSuper = superIterator.next();
			if (!Objects.equal(nextSub, nextSuper)) {
				return false;
			}
		}
		return true;
	}

	public static Collection<String> asStrings(Enum<?>[] values) {
		final List<Enum<?>> list = Arrays.asList(values);
		return Collections2.transform(list, new EnumToString());
	}

	/**
	 * Retrieves a new empty navigable set using the given comparator. The returned
	 * set may be used only on objects known by the comparator, which is probably
	 * not the whole set of objects of type <em>E</em> as the given comparator is a
	 * subset comparator.
	 * 
	 * @param subsetComparator not <code>null</code>.
	 * @return an empty set.
	 */
	@Deprecated
	static public <E> ExtentionalTotalOrder<E> newNavigableSet(final ExtensionalComparator<E> subsetComparator) {
		return ExtentionalTotalOrder.create(subsetComparator);
	}

	/**
	 * If the given iterable contains a unique element, possibly repeated, this
	 * method returns that element, more precisely, the first one of all the equal
	 * elements. If the given iterable contains at least two different (as per
	 * equals) elements, the method returns <code>null</code>. If the given iterable
	 * is empty, this method returns <code>null</code>. If the given iterable
	 * contains only <code>null</code> elements, this method returns
	 * <code>null</code>.
	 * 
	 * @param iterable not <code>null</code>.
	 * @return may be <code>null</code>.
	 */
	static public <E> E getOmnipresentElement(Iterable<E> iterable) {
		checkNotNull(iterable);
		final Iterator<E> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			return null;
		}
		final E omnipresentElement = iterator.next();
		while (iterator.hasNext()) {
			final E thisElement = iterator.next();
			if (!Objects.equal(thisElement, omnipresentElement)) {
				return null;
			}
		}
		return omnipresentElement;
	}

	static public Function<Collection<?>, Integer> getFunctionSize() {
		return new FunctionSize();
	}

	static public <K, V> Function<K, Map.Entry<K, V>> getFunctionAsEntry(Map<K, V> map) {
		return new FunctionAsEntry<K, V>(map);
	}

	static public <K, V> ObservableMap<K, V> newObservableMap() {
		final Map<K, V> delegate = Maps.newLinkedHashMap();
		return new ObservableMap<K, V>(delegate);
	}

	static public <K, V> Function<Entry<K, V>, V> getFunctionEntryValue() {
		return new Function<Map.Entry<K, V>, V>() {

			@Override
			public V apply(Entry<K, V> entry) {
				return entry.getValue();
			}
		};
	}

	static public <K, V> Function<Entry<K, V>, K> getFunctionEntryKey() {
		return new Function<Map.Entry<K, V>, K>() {

			@Override
			public K apply(Entry<K, V> entry) {
				return entry.getKey();
			}
		};
	}

}
