package org.decision_deck.utils.collection.extensional_order;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashBiMap;

/**
 * <p>
 * A comparator defined by <a
 * href=http://en.wikipedia.org/wiki/Extensional_definition>extension</a>. An
 * instance of this comparator has a universe of objects which represents the
 * objects this comparator accepts to compare. When provided objects outside of
 * its universe, the comparator will throw an {@link IllegalArgumentException}.
 * The order this comparator uses is also defined in extension, i.e., the
 * position of each object in this comparator's known universe must be defined
 * explicitly.
 * </p>
 * <p>
 * This comparator imposes orderings that are consistent with equals.
 * </p>
 * <p>
 * The <code>null</code> element counts as a normal element for this comparator.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <E> the type of elements that may be compared by this comparator.
 */
public class ExtensionalComparator<E> extends ForwardingMap<E, Integer> implements Comparator<E>, Map<E, Integer> {

	/**
	 * <p>
	 * Creates a new empty comparator. The comparator's universe, the set of objects
	 * this comparator accepts, is empty: this comparator will not be able to
	 * compare any objects until elements are added).
	 * </p>
	 * 
	 * @return a new extensional comparator.
	 * @deprecated restricted to internal use. For public use, use
	 *             {@link ExtentionalTotalOrder}.
	 */
	@Deprecated
	static public <E> ExtensionalComparator<E> create() {
		return new ExtensionalComparator<E>();
	}

	/**
	 * <p>
	 * Creates a new comparator that represents the iteration order of the given
	 * collection. The content of the collection is used to define the universe of
	 * objects this comparator accepts, and the order of iteration on the given
	 * collection is used to define the order on the universe of objects.
	 * </p>
	 * <p>
	 * The given collection may <em>not</em> contain duplicate elements. One of the
	 * collection element may be <code>null</code> element (which counts as a normal
	 * element for this comparator).
	 * </p>
	 * 
	 * @param order not <code>null</code>. May be empty (in which case this
	 *              comparator will not be able to compare any objects until
	 *              elements are added).
	 * @return a new extensional comparator.
	 * @deprecated Use {@link ExtentionalTotalOrder}.
	 */
	@Deprecated
	static public <E> ExtensionalComparator<E> create(Iterable<E> order) {
		return new ExtensionalComparator<E>(order);
	}

	/**
	 * <p>
	 * Creates a new empty comparator. The comparator's universe, the set of objects
	 * this comparator accepts, is empty: this comparator will not be able to
	 * compare any objects until elements are added).
	 * </p>
	 * 
	 * @return a new extensional comparator.
	 */
	static <E> ExtensionalComparator<E> createInternal() {
		return new ExtensionalComparator<E>();
	}

	/**
	 * Last position possibly taken in the positions values. -1 when positions are
	 * empty.
	 */
	private int m_lastPosition;

	/**
	 * Orderings are consistent with equals as the map guarantees that objects are
	 * equal iff they have the same position. The <code>null</code> key is accepted.
	 */
	private final BiMap<E, Integer> m_positions;

	private ExtensionalComparator() {
		/** Default constructor. */
		m_lastPosition = -1;
		m_positions = HashBiMap.create();
	}

	/**
	 * Creates a new object initialized with the given collection, in order of
	 * iteration. The content of the collection is used to define the universe of
	 * objects this comparator accepts, and the order of iteration on the given
	 * collection is used to define the order on the universe of objects.
	 * 
	 * @param order not <code>null</code>. May be empty (in which case this
	 *              comparator will not be able to compare any objects until
	 *              elements are added). One of the collection element may be
	 *              <code>null</code> element (which counts as a normal element for
	 *              this comparator). The collection may not contain duplicate
	 *              elements. It is recommended to use a {@link SortedSet} when
	 *              possible to ensure a correct iteration order and no duplicate.
	 */
	private ExtensionalComparator(Collection<E> order) {
		if (order == null) {
			throw new NullPointerException();
		}
		m_positions = HashBiMap.create(order.size());
		m_lastPosition = -1;
		for (E e : order) {
			addAsHighest(e);
		}
	}

	/**
	 * Creates a new object initialized with the given collection, in order of
	 * iteration. The content of the collection is used to define the universe of
	 * objects this comparator accepts, and the order of iteration on the given
	 * collection is used to define the order on the universe of objects.
	 * 
	 * @param order not <code>null</code>. May be empty (in which case this
	 *              comparator will not be able to compare any objects until
	 *              elements are added). One of the collection element may be
	 *              <code>null</code> element (which counts as a normal element for
	 *              this comparator). The collection may not contain duplicate
	 *              elements. It is recommended to use a {@link SortedSet} when
	 *              possible to ensure a correct iteration order and no duplicate.
	 */
	private ExtensionalComparator(Iterable<E> order) {
		if (order == null) {
			throw new NullPointerException();
		}
		m_positions = HashBiMap.create();
		m_lastPosition = -1;
		for (E e : order) {
			addAsHighest(e);
		}
	}

	/**
	 * Adds an element just after an other element. The new element is higher than
	 * the given lower element, and lower than any element that was higher than the
	 * given lower element prior to this call.
	 * 
	 * @param lower must be an element in this object.
	 * @param elem  the element to add (<code>null</code> allowed as it counts as a
	 *              normal element), must not already be in this object.
	 */
	public void addAfter(E lower, E elem) {
		/**
		 * Currently, we shift all positions after the element to add to make some
		 * space. NB this could be implemented much more efficiently, with an interval
		 * between each positions to leave some holes.
		 */

		final Integer previousPos = m_positions.get(lower);
		if (previousPos == null) {
			throw new IllegalArgumentException("Previous element " + lower + " is not found in this object.");
		}
		final int prev = previousPos.intValue();
		putAt(elem, prev + 1);
	}

	/**
	 * Adds the given element as the highest of all elements already known to this
	 * object. The given element must not be already known to this object.
	 * 
	 * @param elem may be <code>null</code>, if the <code>null</code> element is not
	 *             already in the set of known objects.
	 */
	public void addAsHighest(E elem) {
		if (m_positions.containsKey(elem)) {
			throw new IllegalArgumentException("Given element " + elem + " is already in the set of elements.");
		}
		++m_lastPosition;
		final Integer lastPosition = Integer.valueOf(m_lastPosition);
		assert !m_positions.containsValue(lastPosition) : "Error adding " + elem + ", supposedly new position number "
				+ m_lastPosition + " already used.";
		m_positions.put(elem, lastPosition);
	}

	public void addAsLowest(E elem) {
		putAt(elem, 0);
	}

	@Override
	public void clear() {
		m_positions.clear();
		m_lastPosition = -1;
	}

	/**
	 * <p>
	 * Compares its two arguments for order. Returns a negative integer, zero, or a
	 * positive integer as the first argument is less than, equal to, or greater
	 * than the second, according to the order of the objects predefined at
	 * construction.
	 * </p>
	 * <p>
	 * Comparing the <code>null</code> element is allowed, if it is one of the
	 * objects known to this comparator.
	 * </p>
	 * 
	 * @throws IllegalStateException iff at least one of the given arguments is not
	 *                               in the universe of objects this comparator
	 *                               accepts.
	 */
	@Override
	public int compare(E o1, E o2) {
		final Integer pos1 = m_positions.get(o1);
		if (pos1 == null) {
			throw new IllegalStateException("Object is not in defined universe: " + o1 + ".");
		}
		final Integer pos2 = m_positions.get(o2);
		if (pos2 == null) {
			throw new IllegalStateException("Object is not in defined universe: " + o2 + ".");
		}
		return pos1.compareTo(pos2);
	}

	public boolean contains(Object o) {
		return m_positions.containsKey(o);
	}

	@Override
	public Integer get(Object element) {
		return m_positions.get(element);
	}

	/**
	 * Removes an element from this ordering if it is present.
	 * 
	 * @param o <code>null</code> is allowed.
	 * @return the previous rank associated with key, or null if there was no
	 *         mapping for key.
	 */
	@Override
	public Integer remove(Object o) {
		final Integer removed = m_positions.remove(o);
		// if (removed != null && removed.intValue() == m_lastPosition) {
		// --m_lastPosition;
		// }
		return removed;
	}

	public void replaceElement(E oldElement, E newElement) {
		final Integer pos = m_positions.remove(oldElement);
		if (pos == null) {
			throw new IllegalStateException("Unknown element: " + oldElement + ".");
		}
		m_positions.put(newElement, pos);
	}

	private void putAt(E elem, final int pos) {
		final Integer addedPos = Integer.valueOf(pos);
		if (!m_positions.containsValue(addedPos)) {
			m_positions.put(elem, addedPos);
			if (pos >= m_lastPosition) {
				++m_lastPosition;
			}
		} else {
			/**
			 * Have to iterate over the map in decreasing position order otherwise the
			 * incremented values we add are considered again just after being added.
			 */
			for (int i = m_lastPosition; i >= pos; --i) {
				final E elemToInc = m_positions.inverse().get(Integer.valueOf(i));
				if (elemToInc != null) {
					m_positions.put(elemToInc, Integer.valueOf(i + 1));
				}
			}
			m_positions.put(elem, addedPos);
			++m_lastPosition;
		}
	}

	@Override
	protected Map<E, Integer> delegate() {
		return Collections.unmodifiableMap(m_positions);
	}
}